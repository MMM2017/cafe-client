package ru.lazycodersinc.smartcafeclient;

import android.app.Application;
import android.content.res.Resources;
import org.json.JSONException;
import org.json.JSONObject;
import ru.lazycodersinc.smartcafeclient.model.*;
import ru.lazycodersinc.smartcafeclient.network.ApiCallListener;
import ru.lazycodersinc.smartcafeclient.network.ApiCallResult;
import ru.lazycodersinc.smartcafeclient.network.NetworkManager;

import java.util.*;

/**
 * Main application class, also serves as an API wrapper
 */
public class CafeApp extends Application
{
	private static CafeApp app;
	private NetworkManager net;

	@Override
	public void onCreate()
	{
		super.onCreate();
		if (app == null)
			app = this;
		else
			throw new RuntimeException("Uhm, ok... you're starting the app twice?");

		net = new NetworkManager("fake");
		menuCache = new HashMap<>();
	}

	public static String getAppVersion() { return "SmartCafe client v. 0.2.0"; }

	//
	// USER
	//

	private static User loggedInAs;
	public static boolean isLoggedIn() { return loggedInAs != null; }

	public static User currentUser() { return loggedInAs; }

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
		app.net.post("/auth", data, new ApiCallListener()
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
					app.net.logIn("I AM THE ADMIN");
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
		app.net.logOut();
	}

	//
	// MENU
	//

	private HashMap<Integer, Dish> menuCache = null;

	public static void getMenu(final FailableActionListener listener)
	{
		if (app.menuCache == null)
			app.menuCache = new HashMap<>();

		if (app.menuCache.isEmpty())
		{
			app.net.get("/menu", new ApiCallListener()
			{
				@Override
				public void onResult(ApiCallResult data)
				{
					// TODO: read data from response and handle errors
					int i = 0;
					Dish d;
					while (i < 30)
					{
						d = new Dish();
						d.name = "Apple";
						d.quantity = "1 unit";
						d.price = (i + 5) * 100;
						d.type = Dish.Type.DESSERT;
						app.menuCache.put(i++, d);

						d = new Dish();
						d.name = "Potato";
						d.quantity = "1 kg";
						d.price = (i + 5) * 100;
						d.type = Dish.Type.SECOND;
						app.menuCache.put(i++, d);

						d = new Dish();
						d.name = "Soup";
						d.quantity = "1 vedro";
						d.price = (i + 5) * 100;
						d.type = Dish.Type.FIRST;
						app.menuCache.put(i++, d);
					}

					// cache is filled, provide listener with data
					listener.onSuccess(app.getMenuAsList());
				}
			});
		}
		else // menu cache is not empty
		{
			listener.onSuccess(app.getMenuAsList());
		}
	}

	private List<Dish> getMenuAsList()
	{
		List<Dish> result = new ArrayList<>();
		result.addAll(menuCache.values());
		return result;
	}

	//
	// NOTIES
	//

	public static void fetchNotifications(final FailableActionListener listener)
	{
		app.net.get("/notification", new ApiCallListener()
		{
			@Override
			public void onResult(ApiCallResult data)
			{
				if (data.isOk())
				{
					// generating test data
					// TODO: obtain one from response
					List<Notification> result = new ArrayList<>();
					for (int i = 0; i < (int) Math.floor(Math.random() * 25); i++)
					{
						result.add(new Notification("Noty #" + (i + 1), null, Calendar.getInstance()));
					}
					listener.onSuccess(result);
				}
				else
				{
					listener.onError(data.status);
				}
			}
		});
	}

	public static void readNotification(final Notification n, final FailableActionListener listener)
	{
		// TODO
		app.net.post("/notification", null, new ApiCallListener()
		{
			@Override
			public void onResult(ApiCallResult data)
			{
				n.read = true;
				listener.onSuccess();
			}
		});
	}

	public static void readAll(FailableActionListener listener)
	{
		// TODO
		// Warning: listener expects that List<Notification> will be returned at first param
		// (just like in fetchNotifications) and it will contain all actual noties.
		fetchNotifications(listener);
	}

	//
	// ORDERS
	//

	public static void fetchOrders(final FailableActionListener listener)
	{
		app.net.get("/order", new ApiCallListener()
		{
			@Override
			public void onResult(ApiCallResult data)
			{
				if (data.isOk())
				{
					// TODO
					List<Order> test = new ArrayList<>();
					for (int i = 0; i < 10; i++)
					{
						Order o = new Order();
						o.table = "#" + i;
						if (i % 4 == 0) o.comment = "OMG THAT'S A COMMENT WOW MUCH LONG SO MULTILINE WHEEEEEEEEEE EEEEEEEEE";
						test.add(o);
					}
					listener.onSuccess(test);
				}
				else
				{
					listener.onError(data.status, data.result);
				}
			}
		});
	}

	//
	// UTILS
	//

	private static int[] units = new int[] { R.plurals.second, R.plurals.minute, R.plurals.hour };
	public static String getDateOffsetString(long millis)
	{
		millis /= 1000; // now in seconds

		int iteration = 0; // 0 for second, 1 for minute, 2 for hour
		while (millis > 60)
		{
			++iteration;
			millis /= 60;
			if (iteration == 2) break;
		}

		Resources res = app.getResources();
		String howMuchAgo = res.getQuantityString(units[iteration], (int) millis, (int) millis);
		return howMuchAgo;
	}
}
