package ru.lazycodersinc.smartcafeclient.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ru.lazycodersinc.smartcafeclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob on 25.03.17.
 */
public abstract class GenericAdapter<T> extends BaseAdapter
{
	protected List<T> content = new ArrayList<>();
	protected Context ctx;
	protected int listItemLayoutId;

	public GenericAdapter(Context context, int layoutId)
	{
		ctx = context;
		listItemLayoutId = layoutId;
	}

	public void updateContent(List<T> newItems)
	{
		content.clear();
		content.addAll(newItems);
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return content.size();
	}

	@Override
	public Object getItem(int i)
	{
		return content.get(i);
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
			v = inflater.inflate(listItemLayoutId, viewGroup, false);
		}

		fillViewWithData((T) getItem(i), v);

		return v;
	}

	protected abstract void fillViewWithData(T item, View v);
}
