package ru.lazycodersinc.smartcafeclient.waiter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.lazycodersinc.smartcafeclient.CafeApp;
import ru.lazycodersinc.smartcafeclient.R;
import ru.lazycodersinc.smartcafeclient.model.FailableActionListener;
import ru.lazycodersinc.smartcafeclient.model.NotiesAdapter;
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

	private NotiesAdapter adapter;

	private boolean isWorking = false;

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
		adapter = new NotiesAdapter(getContext());
		notiesList.setAdapter(adapter);

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

		notiesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l)
			{
				// TODO: also open related order activity
				CafeApp.readNotification((Notification) adapter.getItem(i), new FailableActionListener()
				{
					@Override
					public void onSuccess(Object... params)
					{
						adapter.readNoty(i, view);
					}
					@Override
					public void onError(Object... params)
					{

					}
				});
			}
		});

		FloatingActionButton fab = (FloatingActionButton) notiesView.findViewById(R.id.readAllNotiesFab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (isWorking) return;
				refresher.setRefreshing(true);
				emptyRefresher.setRefreshing(true);
				isWorking = true;
				CafeApp.readAll(refreshListener);
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

	private void updateStatus()
	{
		if (lastUpdatedTime == null)
		{
			return;
		}
		long diff = new Date().getTime() - lastUpdatedTime.getTime();
		String howMuchAgo = CafeApp.getDateOffsetString(diff);
		String result = getResources().getString(R.string.notificationsLastUpdated, howMuchAgo);
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

	FailableActionListener refreshListener = new FailableActionListener()
	{
		@Override
		public void onSuccess(Object... params)
		{
			List<Notification> result = (List<Notification>) params[0];
			adapter.updateList(result);

			lastUpdatedTime = new Date();
			setStatus(R.string.notificationsUpdatedJustNow);

			refresher.setRefreshing(false);
			emptyRefresher.setRefreshing(false);
			isWorking = false;
		}

		@Override
		public void onError(Object... params)
		{
			setStatusError(R.string.notificationsFetchingError);
			refresher.setRefreshing(false);
			emptyRefresher.setRefreshing(false);
			isWorking = false;
		}
	};

	@Override
	public void onRefresh()
	{
		if (isWorking)
		{
			return;
		}
		isWorking = true;
		CafeApp.fetchNotifications(refreshListener);
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
