package ru.lazycodersinc.smartcafeclient.waiter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ru.lazycodersinc.smartcafeclient.CafeApp;
import ru.lazycodersinc.smartcafeclient.R;
import ru.lazycodersinc.smartcafeclient.model.FailableActionListener;
import ru.lazycodersinc.smartcafeclient.model.Notification;

import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNotiesReadListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
	private OnNotiesReadListener mListener;

	private SwipeRefreshLayout refresher;
	private SwipeRefreshLayout emptyRefresher;
	private TextView statusText;
	private ListView notiesList;

	private Date lastUpdatedTime = null;

	public NotificationsListFragment()
	{
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment NotificationsListFragment.
	 */
	public static NotificationsListFragment newInstance()
	{
		NotificationsListFragment fragment = new NotificationsListFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View notiesView = inflater.inflate(R.layout.notifications_view_layout, container, false);

		notiesList = (ListView) notiesView.findViewById(R.id.notificationsListView);

		refresher = (SwipeRefreshLayout) notiesView.findViewById(R.id.notiesSwipeRefresher);
		refresher.setOnRefreshListener(this);
		emptyRefresher = (SwipeRefreshLayout) notiesView.findViewById(R.id.notiesEmptySwipeRefresher);
		notiesList.setEmptyView(emptyRefresher);
		emptyRefresher.setOnRefreshListener(this);

		// refreshing automatically on startup
		refresher.post(new Runnable() {
			@Override
			public void run() {
				refresher.setRefreshing(true);
			}
		});

		onRefresh();

		statusText = (TextView) notiesView.findViewById(R.id.unreadCountText);

		return notiesView;
	}

	public void onNotiesReadPressed()
	{
		if (mListener != null)
		{
			mListener.onNotificationsRead(0);
		}
	}

	private void setStatus(int textId)
	{
		statusText.setText(textId);
		statusText.setTextColor(Color.GRAY);
	}
	private void setStatusError(int textId)
	{
		statusText.setText(textId);
		statusText.setTextColor(android.R.color.holo_red_dark);
	}

	private int[] units = new int[] { R.plurals.second, R.plurals.minute, R.plurals.hour };
	private void updateStatus()
	{
		if (lastUpdatedTime == null)
		{
			return;
		}
		long diff = new Date().getTime() - lastUpdatedTime.getTime();
		diff /= 1000; // now in seconds
		int iteration = 0; // 0 for second, 1 for minute, 2 for hour
		while (diff > 60)
		{
			++iteration;
			diff /= 60;
			if (iteration == 2) break;
		}

		Resources res = getResources();
		String howMuchAgo = res.getQuantityString(units[iteration], (int) diff, (int) diff);
		String result = res.getString(R.string.notificationsLastUpdated, howMuchAgo);
		statusText.setText(result);
		statusText.setTextColor(android.R.color.tertiary_text_light);
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if (context instanceof OnNotiesReadListener)
		{
			mListener = (OnNotiesReadListener) context;
		}
		else
		{
			throw new RuntimeException(context.toString()
				+ " must implement OnNotiesReadListener");
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onRefresh()
	{
		CafeApp.fetchNotifications(new FailableActionListener()
		{
			@Override
			public void onSuccess(Object... params)
			{
				List<Notification> result = (List<Notification>) params[0];
				ArrayAdapter<Notification> adapter = new ArrayAdapter<>(
					NotificationsListFragment.this.getContext(),
					android.R.layout.simple_list_item_1,
					result);
				notiesList.setAdapter(adapter);

				lastUpdatedTime = new Date();
				setStatus(R.string.notificationsUpdatedJustNow);

				refresher.setRefreshing(false);
				emptyRefresher.setRefreshing(false);
			}

			@Override
			public void onError(Object... params)
			{
				setStatusError(R.string.notificationsFetchingError);
				refresher.setRefreshing(false);
				emptyRefresher.setRefreshing(false);
			}
		});
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnNotiesReadListener
	{
		void onNotificationsRead(int unreadLeft);
	}
}
