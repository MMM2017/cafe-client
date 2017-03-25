package ru.lazycodersinc.smartcafeclient.model;

import ru.lazycodersinc.smartcafeclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob on 14.03.17.
 */
public class Order
{
	public List<Entry> entries;
	public String comment;
	public String table;

	public Order()
	{
		entries = new ArrayList<>();
		comment = "";
		table = "unknown table";
	}

	public State getOrderState()
	{
		for (Entry entry: entries)
		{
			if (!entry.state.isFinal)
				return State.OPEN;
		}
		return State.CLOSED;
	}

	public class Entry
	{
		public Dish dish;
		public float amount = 1F;
		public String comment = "";
		public User cook;
		public EntryState state;

		public Entry(Dish d, float amount)
		{
			dish = d;
			this.amount = amount;
			cook = null;
			state = EntryState.ACCEPTED; // initial state at order creation
		}
	}

	public enum EntryState
	{
		ACCEPTED(false),
		PROCESSING(false),
		READY(false),
		PAYMENT_PENDING(false),
		CLOSED(true),
		CANCELED(true);

		public boolean isFinal;

		EntryState(boolean f) { isFinal = f; }
	}

	public enum State
	{
		OPEN(R.string.orderStatusOpen),
		CLOSED(R.string.orderStatusClosed);

		public int stringId;

		State(int strId)
		{
			stringId = strId;
		}
	}
}
