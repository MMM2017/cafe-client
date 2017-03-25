package ru.lazycodersinc.smartcafeclient.model.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.lazycodersinc.smartcafeclient.R;
import ru.lazycodersinc.smartcafeclient.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob on 25.03.17.
 */
public class OrdersAdapter extends GenericAdapter<Order>
{
	private static final int COLOR_OPEN = 0xFF00D000;
	private static final int COLOR_CLOSED = 0xFFD06060;

	public OrdersAdapter(Context context)
	{
		super(context, R.layout.order_list_item);
	}

	@Override
	protected void fillViewWithData(Order item, View v)
	{
		ViewHolder h = (ViewHolder) v.getTag();
		if (h == null)
		{
			h = new ViewHolder(v);
			v.setTag(h);
		}

		Resources res = ctx.getResources();

		h.table.setText(res.getString(R.string.orderTableTextTemplate, item.table));
		Order.State s = item.getOrderState();
		h.status.setText(s.stringId);
		switch (s)
		{
			case CLOSED:
				h.status.setTextColor(COLOR_CLOSED);
				break;

			case OPEN:
				h.status.setTextColor(COLOR_OPEN);
				break;
		}
		if (item.comment.isEmpty())
			h.comment.setText(R.string.orderEmptyComment);
		else
			h.comment.setText(item.comment);

		int active = 0, preparing = 0, done = 0;
		for (Order.Entry e: item.entries)
		{
			switch (e.state)
			{
				case ACCEPTED:
					active++;
					break;
				case PROCESSING:
					preparing++;
					break;
				case READY:
					done++;
					break;
			}
		}
		h.overview.setText(res.getString(R.string.orderEntriesOverview, active, preparing, done));
	}

	private class ViewHolder
	{
		TextView table, status, overview, comment;
//		View parent;

		ViewHolder(View v)
		{
//			parent = v;
			table = (TextView) v.findViewById(R.id.orderTableText);
			status = (TextView) v.findViewById(R.id.orderStatusText);
			overview = (TextView) v.findViewById(R.id.orderOverviewText);
			comment = (TextView) v.findViewById(R.id.orderCommentText);
		}
	}
}
