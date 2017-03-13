package ru.lazycodersinc.smartcafeclient.model;

/**
 * Created by bob on 14.03.17.
 */
public class Dish
{
	public Type type;
	public String name;

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
	}
}
