package ru.lazycodersinc.smartcafeclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnListUpdatedListener} interface
 * to handle interaction events.
 * Use the {@link RefreshableListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RefreshableListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
	private static final String ARG_SEARCH_QUERY = "refreshableSearchQuery";

	private String searchQuery;

	private OnListUpdatedListener mListener;

	protected SwipeRefreshLayout mainRefresher, emptyRefresher;
	protected TextView emptyListMessage;
	protected ListView listView;
	protected SearchView searchView;
	protected View root;

	private int layoutId;
	private boolean searchEnabled;
	protected BaseAdapter adapter;

	public void setSearchEnabled(boolean enabled)
	{
		searchEnabled = enabled;
		if (searchView != null)
		{
			searchView.setVisibility(enabled? View.VISIBLE : View.GONE);
		}
	}

	public void setEmptyText(int strId)
	{
		if (emptyListMessage != null)
		{
			emptyListMessage.setText(strId);
		}
	}

	public RefreshableListFragment()
	{
		// Required empty public constructor
	}


	protected void provideValues(String query, BaseAdapter adapter, int layoutId)
	{
		Bundle args = new Bundle();
		args.putString(ARG_SEARCH_QUERY, query);
		this.setArguments(args);

		this.adapter = adapter;
		this.layoutId = layoutId;
	}
	protected void provideValues(String query, BaseAdapter adapter)
	{
		provideValues(query, adapter, R.layout.refreshable_list_layout);
	}
	protected void provideValues(String query)
	{
		provideValues(query, null);
	}

	public static RefreshableListFragment newInstance(
		String query,
		BaseAdapter dataAdapter,
		int layoutId
	)
	{
		RefreshableListFragment fragment = new RefreshableListFragment();
		fragment.provideValues(query, dataAdapter, layoutId);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			searchQuery = getArguments().getString(ARG_SEARCH_QUERY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		root = inflater.inflate(R.layout.refreshable_list_layout, container, false);

		mainRefresher = (SwipeRefreshLayout) root.findViewById(R.id.refreshableMainRefresher);
		emptyRefresher = (SwipeRefreshLayout) root.findViewById(R.id.refreshableEmptyRefresher);

		mainRefresher.setOnRefreshListener(this);
		emptyRefresher.setOnRefreshListener(this);

		emptyListMessage = (TextView) root.findViewById(R.id.refreshableEmptyMessage);
		listView = (ListView) root.findViewById(R.id.refreshableListView);
		listView.setAdapter(adapter);

		listView.setEmptyView(emptyRefresher);

		searchView = (SearchView) root.findViewById(R.id.refreshableSearchView);

		return root;
	}

	public void onListUpdated()
	{
		if (mListener != null)
		{
			mListener.onListUpdated();
		}
	}

	protected void setRefreshing(boolean refreshing)
	{
		mainRefresher.setRefreshing(refreshing);
		emptyRefresher.setRefreshing(refreshing);
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if (context instanceof OnListUpdatedListener)
		{
			mListener = (OnListUpdatedListener) context;
		}
		else
		{
//			throw new RuntimeException(context.toString() + " must implement OnListUpdatedListener");
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
	public interface OnListUpdatedListener
	{
		void onListUpdated();
	}
}
