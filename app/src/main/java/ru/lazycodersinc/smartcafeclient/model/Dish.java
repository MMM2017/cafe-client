package ru.lazycodersinc.smartcafeclient.model;

/**
 * Created by bob on 14.03.17.
 */
public class Dish
{
	public Type type;
	public String name;
	public String description;
	public String quantity;
	public int price;

	public Dish()
	{
		type = Type.FIRST;
		quantity = name = "(none)";
		price = 0;
		description = "No description provided";
	}

	public String getQuantityString()
	{
		return (price / 100F) + " RUB / " + quantity;
	}

	public enum Type
	{
		FIRST("First course"),
		SECOND("Second course"),
		DRINK("Drinks"),
		DESSERT("Desserts");

		public String name;

		Type(String n)
		{
			name = n;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}
