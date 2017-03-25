package ru.lazycodersinc.smartcafeclient.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by bob on 14.03.17.
 */
public class Notification
{
	public String text;
	public Order relatedTo;
	public boolean read;
	public Calendar createdAt;

	public Notification(String message, Order related, Calendar creationDate)
	{
		text = message;
		relatedTo = related;
		read = false;
		createdAt = creationDate;
	}

	@Override
	public String toString()
	{
		return text;
	}
}
