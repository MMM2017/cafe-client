package ru.lazycodersinc.smartcafeclient.network;

import ru.lazycodersinc.smartcafeclient.model.Dish;
import ru.lazycodersinc.smartcafeclient.model.User;

/**
 * Class provides access to client-server API methods.
 * Implements singletone pattern.
 */
public class NetworkManager
{
	//
	// Singletone
	//

	private NetworkManager instance;
	public NetworkManager getInstance()
	{
		if (instance == null) instance = new NetworkManager();
		return instance;
	}

	//
	// Private members
	//
	private String authToken = null;

	//
	// Public API
	//

	// authentication

	public User logIn(String username, String password)
	{
		return null;
	}
	public void logOut()
	{
		authToken = null;
	}

	public boolean isLoggedIn() { return authToken == null; }

	// dishes

	public Dish[] listMenu(MenuSearchQuery searchQuery)
	{
		return new Dish[0];
	}

	public Dish[] listMenu()
	{
		return listMenu(new MenuSearchQuery());
	}
}
