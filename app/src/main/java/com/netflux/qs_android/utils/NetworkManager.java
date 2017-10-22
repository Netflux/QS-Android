package com.netflux.qs_android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.netflux.qs_android.data.pojos.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class NetworkManager {

	private static final String TAG = "NetworkManager";

	private final Context _context;

	public NetworkManager(Context context) {
		_context = context;
	}

	@Nullable
	public List<Ticket> getAllTickets() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
		long lastFetch = prefs.getLong(Constants.Prefs.LAST_FETCH, 0);
		HttpURLConnection conn = null;
		InputStream in = null;

		try {
			conn = buildConnection(Constants.SERVER_URL + "/api/tickets?timestamp=" + lastFetch, "GET");
			in = conn.getInputStream();

			return Utils.Json.readJsonTickets(in);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}

			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	@Nullable
	public Ticket getNewTicket() {
		JSONObject payload = new JSONObject();
		try {
			payload.put("key", Utils.getUUID(_context));
			payload.put("secret", Utils.getRandomString());
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return null;
		}

		HttpURLConnection conn = null;
		OutputStream out = null;
		InputStream in = null;

		try {
			conn = buildConnection(Constants.SERVER_URL + "/api/tickets", "POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoOutput(true);

			out = conn.getOutputStream();
			out.write(payload.toString().getBytes("UTF-8"));
			out.close();

			if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
				in = conn.getInputStream();
				return Utils.Json.readJsonNewTicket(in);
			}

			return null;
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}

			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public boolean cancelTicket(Ticket ticket) {
		JSONObject payload = new JSONObject();
		try {
			payload.put("key", Utils.getUUID(_context));
			payload.put("secret", ticket.getSecret());
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return false;
		}

		HttpURLConnection conn = null;
		OutputStream out = null;

		try {
			conn = buildConnection(Constants.SERVER_URL + "/api/tickets/" + ticket.getId(), "DELETE");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoOutput(true);

			out = conn.getOutputStream();
			out.write(payload.toString().getBytes("UTF-8"));
			out.close();

			return conn.getResponseCode() == 204;
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return false;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * Helper function to build a HTTP URL connection object.
	 * @param address - The target address.
	 * @param method - The HTTP method.
	 * @return {@link HttpURLConnection HTTP URL Connection} object.
	 * @throws IOException
	 */
	private HttpURLConnection buildConnection(String address, String method) throws IOException {
		// Setup the HTTP connection
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(5000);
		conn.setConnectTimeout(5000);
		conn.setRequestMethod(method);
		conn.setDoInput(true);

		return conn;
	}

}
