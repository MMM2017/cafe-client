package ru.lazycodersinc.smartcafeclient;

import android.app.Application;
import org.json.JSONException;
import org.json.JSONObject;
import ru.lazycodersinc.smartcafeclient.model.Dish;
import ru.lazycodersinc.smartcafeclient.model.FailableActionListener;
import ru.lazycodersinc.smartcafeclient.model.User;
import ru.lazycodersinc.smartcafeclient.network.ApiCallListener;
import ru.lazycodersinc.smartcafeclient.network.ApiCallResult;
import ru.lazycodersinc.smartcafeclient.network.NetworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bob on 18.03.17.
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

	public User currentUser() { return loggedInAs; }

	public void logIn(final String username, String password, final FailableActionListener l)
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
					net.logIn("I AM THE ADMIN");
				}
				if (l == null) return;

				if (data.isOk())
					l.onSuccess(data.result);
				else
					l.onError(data.status, data.result);
			}
		});
	}

	public void logOut()
	{
		net.logOut();
	}

	//
	// MENU
	//

	private HashMap<Integer, Dish> menuCache = null;

	public void getMenu(final FailableActionListener listener)
	{
		if (menuCache == null)
			menuCache = new HashMap<>();

		if (menuCache.isEmpty())
		{
			net.get("/menu", new ApiCallListener()
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
						menuCache.put(i++, d);

						d = new Dish();
						d.name = "Potato";
						d.quantity = "1 kg";
						d.price = (i + 5) * 100;
						d.type = Dish.Type.SECOND;
						menuCache.put(i++, d);

						d = new Dish();
						d.name = "Soup";
						d.quantity = "1 vedro";
						d.price = (i + 5) * 100;
						d.type = Dish.Type.FIRST;
						menuCache.put(i++, d);
					}

					// cache is filled, provide listener with data
					listener.onSuccess(getMenuAsList());
				}
			});
		}
		else // menu cache is not empty
		{
			listener.onSuccess(getMenuAsList());
		}
	}

	private List<Dish> getMenuAsList()
	{
		List<Dish> result = new ArrayList<>();
		result.addAll(menuCache.values());
		return result;
	}
}
