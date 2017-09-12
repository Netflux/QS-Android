package com.netflux.qs_android.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.netflux.qs_android.R;
import com.netflux.qs_android.screens.common.controllers.MainActivity;


public class NotificationService extends Service implements UpdateService.UpdateServiceListener {

	private ServiceConnection _serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			_updateService = ((UpdateService.UpdateServiceBinder) service).getService();
			_updateService.registerListener(NotificationService.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			_updateService.unregisterListener(NotificationService.this);
			_updateService = null;
		}
	};

	private UpdateService _updateService;

	private final int NOTIFICATION_ID = 1;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (_updateService != null) {
			_updateService.unregisterListener(NotificationService.this);
			unbindService(_serviceConnection);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleNotification();

		return START_STICKY;
	}

	@Override
	public void onStartUpdate() {
		// No action required
	}

	@Override
	public void onFinishUpdate() {
		handleNotification();
	}

	public Notification buildNotification(long currentTicketID, long servingTicketID) {
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent =
				PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		return new NotificationCompat.Builder(this)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(getString(R.string.label_yourNumber) + " " + currentTicketID)
				.setContentText(getString(R.string.label_curServing) + " " + servingTicketID)
				.setContentIntent(pendingIntent)
				.build();
	}

	public void handleNotification() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		long currentTicketID = prefs.getLong(Constants.Prefs.TICKET_CURRENT_ID, -1);
		long servingTicketID = prefs.getLong(Constants.Prefs.TICKET_SERVING_ID, -1);

		if (currentTicketID == -1 || servingTicketID == -1) {
			stopForeground(true);
			stopSelf();
		} else {
			startForeground(NOTIFICATION_ID, buildNotification(currentTicketID, servingTicketID));

			Intent serviceIntent = new Intent(this, UpdateService.class);
			bindService(serviceIntent, _serviceConnection, BIND_AUTO_CREATE);
		}
	}

}
