package ru.lazycodersinc.smartcafeclient.model;

/**
 * Class provides static info about application state
 */
public class AppState
{
	private static User loggedInAs;
	public static boolean isLoggedIn() { return loggedInAs == null; }

	public static void loggedIn(User as) { loggedInAs = as; }
}
