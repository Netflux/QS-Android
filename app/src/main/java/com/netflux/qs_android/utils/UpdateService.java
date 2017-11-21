package com.netflux.qs_android.utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.netflux.adp.util.BackgroundThreadPoster;
import com.netflux.adp.util.MainThreadPoster;
import com.netflux.adp.util.NetworkUtil;
import com.netflux.qs_android.App;
import com.netflux.qs_android.data.models.TicketModel;
import com.netflux.qs_android.data.pojos.Ticket;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class UpdateService extends Service {

	private class NetworkReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (NetworkUtil.hasInternetConnection(context)) {
				setupWebSocket();
			} else if (_ws != null) {
				_ws.disconnect();
			}
		}

	}

	private static final String TAG = "UpdateService";

	public class UpdateServiceBinder extends Binder {

		public UpdateService getService() {
			return UpdateService.this;
		}

	}

	public interface UpdateServiceListener {

		/**
		 * Callback when an update is started.
		 */
		void onStartUpdate();

		/**
		 * Callback when an update has finished.
		 */
		void onFinishUpdate();

	}

	private final NetworkReceiver _receiver = new NetworkReceiver();
	private final IBinder _binder = new UpdateServiceBinder();

	private NetworkManager _networkManager;
	private TicketModel _ticketModel;
	private boolean _isUpdating;
	private WebSocket _ws;

	// Thread-safe list of listeners. Supports registration/unregistration from other threads
	private Set<UpdateServiceListener> _listeners = Collections.newSetFromMap(
			new ConcurrentHashMap<UpdateServiceListener, Boolean>(1)
	);

	@Override
	public IBinder onBind(Intent intent) {
		return _binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		_networkManager = ((App) getApplication()).getNetworkManager();
		_ticketModel = ((App) getApplication()).getTicketModel();
		_isUpdating = false;

		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(_receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(_receiver);
		if (_ws != null) {
			_ws.disconnect();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleUpdate();
		return START_STICKY;
	}

	private void handleUpdateSync() {
		if (!_isUpdating) {
			_isUpdating = true;
			notifyStartUpdate();

			long timestamp = System.currentTimeMillis();
			List<Ticket> result = _networkManager.getAllTickets();
			Bundle bundle = _networkManager.getSystemStatus();

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = prefs.edit();
			if (bundle != null) {
				editor.putBoolean(Constants.Prefs.SYSTEM_STATUS, bundle.getInt(Constants.Prefs.SYSTEM_STATUS) == 1);
				editor.putString(Constants.Prefs.SYSTEM_LOCATION, bundle.getString(Constants.Prefs.SYSTEM_LOCATION));
				editor.putInt(Constants.Prefs.SYSTEM_REMAINING, bundle.getInt(Constants.Prefs.SYSTEM_REMAINING));
			}

			if (result != null && result.size() > 0) {
				_ticketModel.addOrUpdateSync(result);
				editor.putLong(Constants.Prefs.LAST_FETCH, timestamp);
			}

			editor.apply();

			// Start the notification service
			Intent intent = new Intent(this, NotificationService.class);
			startService(intent);

			_isUpdating = false;
			notifyFinishUpdate();
		}
	}

	public void handleUpdate() {
		BackgroundThreadPoster.getInstance().post(new Runnable() {
			@Override
			public void run() {
				handleUpdateSync();
			}
		});
	}

	private void setupWebSocket() {
		try {
			_ws = new WebSocketFactory().createSocket(Constants.SERVER_URL_WS);
			_ws.addListener(new WebSocketAdapter() {
				@Override
				public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
					handleUpdate();
				}

				@Override
				public void onTextMessage(WebSocket websocket, String text) throws Exception {
					handleUpdate();
				}

				@Override
				public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
					setupWebSocket();
				}
			});
			_ws.connectAsynchronously();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
	}

	private void notifyStartUpdate() {
		MainThreadPoster.getInstance().post(new Runnable() {
			@Override
			public void run() {
				for (UpdateServiceListener listener : _listeners) {
					listener.onStartUpdate();
				}
			}
		});
	}

	private void notifyFinishUpdate() {
		MainThreadPoster.getInstance().post(new Runnable() {
			@Override
			public void run() {
				for (UpdateServiceListener listener : _listeners) {
					listener.onFinishUpdate();
				}
			}
		});
	}

	/**
	 * Register a {@link UpdateServiceListener listener} for update events.
	 * @param listener The {@link UpdateServiceListener listener}.
	 */
	public void registerListener(UpdateServiceListener listener) {
		if (listener != null) _listeners.add(listener);
	}

	/**
	 * Unregister a {@link UpdateServiceListener listener} for update events.
	 * @param listener The {@link UpdateServiceListener listener}.
	 */
	public void unregisterListener(UpdateServiceListener listener) {
		if (listener != null) _listeners.remove(listener);
	}

}
