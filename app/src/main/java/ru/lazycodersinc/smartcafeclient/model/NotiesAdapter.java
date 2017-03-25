package ru.lazycodersinc.smartcafeclient.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.lazycodersinc.smartcafeclient.CafeApp;
import ru.lazycodersinc.smartcafeclient.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by bob on 25.03.17.
 */
public class NotiesAdapter extends BaseAdapter
{
	private List<Notification> noties = new ArrayList<>();
	private Context ctx;

	public NotiesAdapter(Context context)
	{
		ctx = context;
	}

	public void updateList(List<Notification> newNoties)
	{
		noties.clear();
		noties.addAll(newNoties);
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return noties.size();
	}

	@Override
	public Object getItem(int i)
	{
		return noties.get(i);
	}

	@Override
	public long getItemId(int i)
	{
		return i;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup viewGroup)
	{
		View v = convertView;
		if (v == null)
		{
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.notification_list_item, viewGroup, false);
			v.setTag(new ViewHolder(v));
		}
		ViewHolder h = (ViewHolder) v.getTag();
		Notification noty = (Notification) getItem(i);

		h.message.setText(noty.text);
		h.date.setText(formatDate(noty.createdAt));
		h.ago.setText(createAgoText(noty.createdAt));

		return v;
	}

	private String createAgoText(Calendar d)
	{
		long millis = Calendar.getInstance().getTimeInMillis() - d.getTimeInMillis();
		if (millis < 1000)
			return ctx.getResources().getString(R.string.justNow);

		return ctx.getResources().getString(R.string.timesAgo, CafeApp.getDateOffsetString(millis));
	}

	private String formatDate(Calendar d)
	{
		int h = d.get(Calendar.HOUR);
		int m = d.get(Calendar.MINUTE);
		int D = d.get(Calendar.DAY_OF_MONTH);
		int M = d.get(Calendar.MONTH);
		String hh = h > 10? h + "" : "0" + h;
		String mm = m > 10? m + "" : "0" + m;
		String DD = D > 10? D + "" : "0" + D;
		String MM = M > 10? M + "" : "0" + M;
		return hh + ":" + mm + " " + DD + "." + MM;
	}

	private class ViewHolder
	{
		TextView message, date, ago;
		View item;
		ViewHolder(View v)
		{
			item = v;
			message = (TextView) v.findViewById(R.id.notyMessage);
			date = (TextView) v.findViewById(R.id.notyDateText);
			ago = (TextView) v.findViewById(R.id.notyAgoText);
		}
	}
}
