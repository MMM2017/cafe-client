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

	public MenuSearchQuery()
	{
		allowedTypes = EnumSet.allOf(Dish.Type.class);
		searchString = "";
	}
}
