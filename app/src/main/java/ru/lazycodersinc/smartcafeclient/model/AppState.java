package ru.lazycodersinc.smartcafeclient.model;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class provides static info about application state
 */
public class AppState
{
	private static User loggedInAs;
	public static boolean isLoggedIn() { return loggedInAs == null; }

	public static void loggedIn(User as) { loggedInAs = as; }

	// registries
	public static Map<Integer, Order> ordersRegistry;
	public static Map<Integer, Notification> notificationsRegistry;
	public static Map<Integer, User> usersRegistry;

	static
	{
		ordersRegistry = new HashMap<>();
		notificationsRegistry = new HashMap<>();
		usersRegistry = new HashMap<>();
	}

	public static Order registerOrder(int id, Order content)
	{
		if (ordersRegistry.containsKey(id))
			return ordersRegistry.get(id);
		ordersRegistry.put(id, content);
		return content;
	}
}
