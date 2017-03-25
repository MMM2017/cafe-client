package ru.lazycodersinc.smartcafeclient.model.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.lazycodersinc.smartcafeclient.model.Order;

/**
 * Created by bob on 25.03.17.
 */
public class OrderContentAdapter extends GenericAdapter<Order.Entry>
{

	public OrderContentAdapter(Context ctx, Order data)
	{
		super(ctx, 0); // TODO
	}

	@Override
	protected void fillViewWithData(Order.Entry item, View v)
	{

	}

	private class ViewHolder
	{
		TextView title;

		ViewHolder(View v)
		{
			//
		}
	}
}
