package ru.lazycodersinc.smartcafeclient.network;

import ru.lazycodersinc.smartcafeclient.model.Dish;

import java.util.EnumSet;

/**
 * Created by bob on 14.03.17.
 */
public class MenuSearchQuery
{
	public EnumSet<Dish.Type> allowedTypes;
	public String searchString;
	public int minPrice, maxPrice;

	public MenuSearchQuery()
	{
		allowedTypes = EnumSet.allOf(Dish.Type.class);
		searchString = "";
		minPrice = 0;
		maxPrice = Integer.MAX_VALUE;
	}

	public boolean suits(Dish d)
	{
		if (!d.name.contains(searchString)) return false;
		if (!allowedTypes.contains(d.type)) return false;
		if (d.price < minPrice) return false;
		if (d.price > maxPrice) return false;
		return true;
	}

	public static MenuSearchQuery getIdentity() { return new MenuSearchQuery(); }
}
