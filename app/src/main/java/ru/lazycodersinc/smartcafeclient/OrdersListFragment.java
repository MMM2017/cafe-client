package ru.lazycodersinc.smartcafeclient;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnOrdersListUpdatedListener} interface
 * to handle interaction events.
 * Use the {@link OrdersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersListFragment extends Fragment
{
	private static final String ARG_SEARCH_QUERY = "ordersListSearchQuery";

	private String searchQuery;

	private OnOrdersListUpdatedListener mListener;

	public OrdersListFragment()
	{
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param query Search query.
	 * @return A new instance of fragment OrdersListFragment.
	 */
	public static OrdersListFragment newInstance(String query)
	{
		OrdersListFragment fragment = new OrdersListFragment();
		Bundle args = new Bundle();
		args.putString(ARG_SEARCH_QUERY, query);
		fragment.setArguments(args);
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
		return inflater.inflate(R.layout.orders_list_layout, container, false);
	}

	public void onListUpdated()
	{
		if (mListener != null)
		{
			mListener.onOrdersListUpdated();
		}
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if (context instanceof OnOrdersListUpdatedListener)
		{
			mListener = (OnOrdersListUpdatedListener) context;
		}
		else
		{
			throw new RuntimeException(context.toString()
				+ " must implement OnOrdersListUpdatedListener");
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mListener = null;
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
	public interface OnOrdersListUpdatedListener
	{
		// TODO: Update argument type and name
		void onOrdersListUpdated();
	}
}
