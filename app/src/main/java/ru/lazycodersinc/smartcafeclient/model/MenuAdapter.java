package ru.lazycodersinc.smartcafeclient.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.lazycodersinc.smartcafeclient.R;
import ru.lazycodersinc.smartcafeclient.network.MenuSearchQuery;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by bob on 14.03.17.
 */
public class MenuAdapter extends BaseAdapter
{
	private List<Dish> data = new ArrayList<>();
	private List<Dish> filteredData = new ArrayList<>();
	private MenuSearchQuery filter;
	private Context ctx;

	private OnItemClickListener icl;
	private OnItemButtonClickListener ibcl;

	public MenuAdapter(Context context, List<Dish> objects)
	{
		ctx = context;
		data.addAll(objects);
		filter = MenuSearchQuery.getIdentity();
		applyFilter();
		notifyDataSetChanged();
	}

	public void setOnItemClickListener(OnItemClickListener listener)
	{
		icl = listener;
	}

	public void setOnItemButtonClickListener(OnItemButtonClickListener listener)
	{
		ibcl = listener;
	}

	@Override
	public int getCount()
	{
		return filteredData.size();
	}

	@Override
	public Object getItem(int i)
	{
		return filteredData.get(i);
	}

	@Override
	public long getItemId(int i)
	{
		return data.indexOf(filteredData.get(i));
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent)
	{
		View v = convertView;

		if (v == null)
		{
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.menu_list_item, parent, false);
		}

		Dish d = filteredData.get(position);
		if (d != null)
		{
			TextView name = (TextView) v.findViewById(R.id.dishNameText);
			TextView portion = (TextView) v.findViewById(R.id.dishPortionText);
			ImageButton addBtn = (ImageButton) v.findViewById(R.id.orderDishButton);

			name.setText(d.name);
			portion.setText(d.getQuantityString());

			addBtn.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					ibcl.onItemButtonClick(position, MenuAdapter.this);
				}
			});

			v.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					if (icl != null)
						icl.onItemClick(view, position, MenuAdapter.this);
				}
			});
		}

		return v;
	}

	public void setFilter(String query)
	{
		filter.searchString = query;
		applyFilter();
		notifyDataSetChanged();
	}
	public void setFilter(Dish.Type category)
	{
		filter.allowedTypes = EnumSet.of(category);
		applyFilter();
		notifyDataSetChanged();
	}
	public void clearFilter()
	{
		filter.searchString = "";
		filter.allowedTypes = EnumSet.allOf(Dish.Type.class);
		applyFilter();
		notifyDataSetChanged();
	}

	private void applyFilter()
	{
		filteredData.clear();
		for (Dish d: data)
		{
			if (filter.suits(d))
				filteredData.add(d);
		}
	}

	public interface OnItemClickListener
	{
		void onItemClick(View v, int position, MenuAdapter adapter);
	}

	public interface OnItemButtonClickListener
	{
		void onItemButtonClick(int position, MenuAdapter adapter);
	}
}
