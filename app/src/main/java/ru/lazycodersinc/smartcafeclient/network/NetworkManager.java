package ru.lazycodersinc.smartcafeclient.network;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class provides access to client-server API methods.
 */
public class NetworkManager
{
	//
	// Private members
	//
	private String authToken = null;
	private String apiPrefix;

	//
	// Public API
	//

	public NetworkManager(String apiPrefix)
	{
		this.apiPrefix = apiPrefix;
	}

	public void logIn(String token)
	{
		authToken = token;
	}
	public void logOut()
	{
		authToken = null;
	}
	public boolean isLoggedIn() { return authToken == null; }

	public void send(String method, String endpoint, Object data, ApiCallListener listener)
	{
		String url = apiPrefix + endpoint;
		new ApiCallTask().execute(method, url, data, listener);
	}
	public void get(String endpoint, ApiCallListener listener)
	{
		send("GET", endpoint, null, listener);
	}
	public void post(String endpoint, Object data, ApiCallListener listener)
	{
		send("POST", endpoint, data, listener);
	}
	public void put(String endpoint, Object data, ApiCallListener listener)
	{
		send("PUT", endpoint, data, listener);
	}
	public void delete(String endpoint, Object data, ApiCallListener listener)
	{
		send("DELETE", endpoint, data, listener);
	}

	private class ApiCallTask extends AsyncTask<Object, Void, ApiCallResult>
	{
		HttpURLConnection urlConnection = null;
		ApiCallListener listener;

		@Override
		protected ApiCallResult doInBackground(Object... apiCallParams)
		{
			String method = (String) apiCallParams[0];
			String endpoint = (String) apiCallParams[1];
			Object data = apiCallParams[2];
			listener = (ApiCallListener) apiCallParams[3];

			if (endpoint.startsWith("fake"))
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException ex) {}
				return new ApiCallResult(200);
			}

			try
			{
				URL url = new URL(endpoint);

				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod(method);
				urlConnection.setDoInput(true);
				urlConnection.setUseCaches(false);
				if (data != null)
				{
					// writing data
					// shit, java, go fuck yourself
					urlConnection.setDoOutput(true);
					byte[] bodyBytes = data.toString().getBytes("UTF-8");
					OutputStream request = urlConnection.getOutputStream();
					request.write(bodyBytes);
					request.close();

					// assuming we are sending data in json
					urlConnection.setRequestProperty("Content-Type","application/json");
					urlConnection.setRequestProperty("Content-Length", Integer.toString(bodyBytes.length));
				}
				urlConnection.connect();

				InputStream inputStream = urlConnection.getInputStream();

				JsonReader r = new JsonReader(new InputStreamReader(inputStream));

				ApiCallResult result = new ApiCallResult(urlConnection.getResponseCode());
				result.result = r;
				return result;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return ApiCallResult.OFFLINE;
			}
		}

		@Override
		protected void onPostExecute(ApiCallResult apiCallResult)
		{
			super.onPostExecute(apiCallResult);
			if (listener != null)
				listener.onResult(apiCallResult);
		}
	}
}
