package ru.lazycodersinc.smartcafeclient.network;

import android.util.JsonReader;

/**
 * Created by bob on 14.03.17.
 */
public class ApiCallResult
{
	public int status;
	public JsonReader result;

	public ApiCallResult(int status)
	{
		this.status = status;
		this.result = null;
	}

	public boolean isOffline() { return status == 0; }

	public static final ApiCallResult OFFLINE = new ApiCallResult(0);
}
