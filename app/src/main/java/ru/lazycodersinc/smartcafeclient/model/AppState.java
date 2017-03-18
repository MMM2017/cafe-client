package ru.lazycodersinc.smartcafeclient.model;

import org.json.JSONException;
import org.json.JSONObject;
import ru.lazycodersinc.smartcafeclient.network.ApiCallListener;
import ru.lazycodersinc.smartcafeclient.network.ApiCallResult;
import ru.lazycodersinc.smartcafeclient.network.NetworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class provides static info about application state.
 * Class serves as central point to perform any operations on data.
 */
public class AppState
{
	public static String getAppVersion() { return "SmartCafe client v. 0.2.0"; }

	private static User loggedInAs;
	public static boolean isLoggedIn() { return loggedInAs != null; }

	public static User currentUser() { return loggedInAs; }

	//
	// registries
	//

	private static Map<Integer, Order> ordersRegistry;
	private static Map<Integer, Notification> notificationsRegistry;
	private static Map<Integer, User> usersRegistry;
	private static Map<Integer, Dish> menuRegistry;

	static
	{
		ordersRegistry = new HashMap<>();
		notificationsRegistry = new HashMap<>();
		usersRegistry = new HashMap<>();
		menuRegistry = new HashMap<>();

		// fake data
		int i = 0;
		Dish d;
		while (i < 30)
		{
			d = new Dish();
			d.name = "Apple";
			d.quantity = "1 unit";
			d.price = (i + 5) * 100;
			d.type = Dish.Type.DESSERT;
			menuRegistry.put(i++, d);

			d = new Dish();
			d.name = "Potato";
			d.quantity = "1 kg";
			d.price = (i + 5) * 100;
			d.type = Dish.Type.SECOND;
			menuRegistry.put(i++, d);

			d = new Dish();
			d.name = "Soup";
			d.quantity = "1 vedro";
			d.price = (i + 5) * 100;
			d.type = Dish.Type.FIRST;
			menuRegistry.put(i++, d);
		}
	}

	private static Order registerOrder(int id, Order content)
	{
		if (ordersRegistry.containsKey(id))
			return ordersRegistry.get(id);
		ordersRegistry.put(id, content);
		return content;
	}

	private static void gcOrders()
	{
		// wiping away all closed and therefore not relevant orders
		ArrayList<Integer> closed = new ArrayList<>();
		for (Integer id: ordersRegistry.keySet())
		{
			Order val = ordersRegistry.get(id);
			if (val.getOrderState() == Order.State.CLOSED)
			{
				closed.add(id);
			}
		}

		for (Integer rem: closed)
		{
			ordersRegistry.remove(rem);
		}
	}

	//
	// data manipulation
	//

	private static NetworkManager net = new NetworkManager("fake");

	public static void logIn(final String username, String password, final FailableActionListener l)
	{
		JSONObject data = new JSONObject();
		try
		{
			data.put("login", username);
			data.put("password", password);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			l.onError(ApiCallResult.STATUS_UNDEFINED, e);
			return;
		}
		net.post("/auth", data, new ApiCallListener()
		{
			@Override
			public void onResult(ApiCallResult data)
			{
				if (data.isOk())
				{
					// use result to record current user data
					// TODO
					loggedInAs = new User();
					loggedInAs.login = username;
					loggedInAs.role = User.Role.WAITER;
					// TODO: set auth token to net
				}
				if (l == null) return;

				if (data.isOk())
					l.onSuccess(data.result);
				else
					l.onError(data.status, data.result);
			}
		});
	}

	public static void logOut()
	{
		net.logOut();
	}

	public static void updateMenu(final FailableActionListener listener)
	{
		net.get("/menu", new ApiCallListener()
		{
			@Override
			public void onResult(ApiCallResult data)
			{
				if (data.isOk())
				{
					// TODO: update cache
				}
				if (listener == null) return;
				if (data.isOk())
				{
					listener.onSuccess(data.result);
				}
				else
				{
					listener.onError(data.status, data.result);
				}
			}
		});
	}

	public static List<Dish> getMenuCache()
	{
		List<Dish> result = new ArrayList<>();
		result.addAll(menuRegistry.values());
		return result;
	}
}
