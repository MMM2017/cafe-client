package ru.lazycodersinc.smartcafeclient.model;

/**
 * Created by bob on 14.03.17.
 */
public class Notification
{
	public String text;
	public Order relatedTo;

	public Notification(String message, Order related)
	{
		text = message;
		relatedTo = related;
	}
}
