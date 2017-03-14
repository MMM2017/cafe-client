package ru.lazycodersinc.smartcafeclient.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import ru.lazycodersinc.smartcafeclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob on 14.03.17.
 */
public class MenuAdapter extends ArrayAdapter<Dish>
{
	private List<Dish> data = new ArrayList<>();

	public MenuAdapter(Context context, int resource)
	{
		super(context, resource);
	}

	public MenuAdapter(Context context, int resource, List<Dish> objects)
	{
		super(context, resource, objects);
		data.addAll(objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;

		if (v == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.menu_list_item, null);
		}

		Dish d = data.get(position);
		if (d != null)
		{
			TextView name = (TextView) v.findViewById(R.id.dishNameText);
			TextView portion = (TextView) v.findViewById(R.id.dishPortionText);
			ImageButton addBtn = (ImageButton) v.findViewById(R.id.orderDishButton);

			name.setText(d.name);
			portion.setText(d.portionSize + " " + d.portionUnit);

			// TODO: update callbacks
		}

		return v;
	}
}
