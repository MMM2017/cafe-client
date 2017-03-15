package ru.lazycodersinc.smartcafeclient.model;

/**
 * Created by bob on 14.03.17.
 */
public class User
{
	public enum Role { WAITER, COOK }

	public Role role;
	public String login;
	public String name;
}
