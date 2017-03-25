package ru.lazycodersinc.smartcafeclient;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import ru.lazycodersinc.smartcafeclient.model.FailableActionListener;
import ru.lazycodersinc.smartcafeclient.model.Order;
import ru.lazycodersinc.smartcafeclient.model.adapters.OrdersAdapter;

import java.util.List;

/**
 * Created by bob on 25.03.17.
 */
public class OrderContentListFragment extends RefreshableListFragment
{
	public OrderContentListFragment()
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
	public static OrderContentListFragment newInstance(String query)
	{
		OrderContentListFragment fragment = new OrderContentListFragment();
		fragment.provideValues(query);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		if (adapter == null)
		{
			adapter = new OrdersAdapter(getContext());
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = super.onCreateView(inflater, container, savedInstanceState);
		setEmptyText(R.string.noOrders);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Order clicked = (Order) adapter.getItem(i);
				// TODO
				Toast.makeText(getContext(), clicked.table, Toast.LENGTH_SHORT).show();
			}
		});

		return v;
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
//		if (context instanceof OnOrdersListUpdatedListener)
//		{
//			mListener = (OnOrdersListUpdatedListener) context;
//		}
//		else
//		{
//			throw new RuntimeException(context.toString()
//				+ " must implement OnOrdersListUpdatedListener");
//		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
//		mListener = null;
	}

	@Override
	public void onRefresh()
	{
		super.onRefresh();

		CafeApp.fetchOrders(new FailableActionListener()
		{
			@Override
			public void onSuccess(Object... params)
			{
				List<Order> result = (List<Order>) params[0];
				((OrdersAdapter) adapter).updateContent(result);
				setRefreshing(false);
			}

			@Override
			public void onError(Object... params)
			{
				Toast.makeText(getContext(), "Error while refreshing!", Toast.LENGTH_SHORT).show();
				setRefreshing(false);
			}
		});
	}
}
