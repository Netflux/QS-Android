package com.netflux.qs_android.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.netflux.adp.util.BackgroundThreadPoster;
import com.netflux.qs_android.App;
import com.netflux.qs_android.R;
import com.netflux.qs_android.data.models.TicketModel;
import com.netflux.qs_android.data.pojos.Ticket;
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
	private SharedPreferences _prefs;
	private TicketModel _ticketModel;

	private final int NOTIFICATION_ID = 1;

	@Override
	public void onCreate() {
		super.onCreate();

		_prefs = PreferenceManager.getDefaultSharedPreferences(this);
		_ticketModel = ((App) getApplication()).getTicketModel();
	}

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

	public Notification buildNotification(long currentTicketID, long servingTicketID, long nextTicketID, int remainingCount) {
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent =
				PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		if (remainingCount <= 3 && servingTicketID != -1 && currentTicketID != servingTicketID) {
			Uri tone = Uri.parse(_prefs.getString(getString(R.string.prefs_notificationTone), Constants.DEFAULT_NOTIFICATION_URI));
			String vibration = _prefs.getString(getString(R.string.prefs_vibrate), "1");
			long[] pattern = new long[] { 0, 0 };

			switch (vibration) {
				case "1":
					pattern = new long[] { 0, 250, 250, 250 };
					break;
				case "2":
					pattern = new long[] { 0, 250 };
					break;
				case "3":
					pattern = new long[] { 0, 500, 250, 500 };
					break;
			}

			builder.setSound(tone);
			builder.setVibrate(pattern);
		}

		return builder
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(getString(R.string.label_yourNumber) + " " + currentTicketID)
				.setContentText(buildNotificationText(servingTicketID, nextTicketID, remainingCount))
				.setContentIntent(pendingIntent)
				.build();
	}

	private String buildNotificationText(long servingTicketID, long nextTicketID, int remainingCount) {
		StringBuilder contentText = new StringBuilder();
		if (servingTicketID != -1) {
			contentText.append(getString(R.string.label_curServing)).append(" ").append(servingTicketID);
		} else {
			contentText.append(getString(R.string.label_nextTicket)).append(" ").append(nextTicketID);
		}
		contentText.append(" (");
		if (remainingCount > 0) {
			contentText.append(getString(R.string.label_remainingTickets, remainingCount));
		} else {
			contentText.append(getString(R.string.label_yourTicket));
		}
		contentText.append(")");

		return contentText.toString();
	}

	public void handleNotification() {
		final long currentTicketID = _prefs.getLong(Constants.Prefs.TICKET_CURRENT_ID, -1);
		final long servingTicketID = _prefs.getLong(Constants.Prefs.TICKET_SERVING_ID, -1);

		if (currentTicketID == -1) {
			stopForeground(true);
			stopSelf();
		} else {
			BackgroundThreadPoster.getInstance().post(new Runnable() {
				@Override
				public void run() {
					Ticket nextTicket = _ticketModel.getNextSync();
					long nextTicketID = nextTicket != null ? nextTicket.getId() : -1;
					int remainingCount = _ticketModel.getRemainingCountSync(currentTicketID);
					startForeground(NOTIFICATION_ID, buildNotification(currentTicketID, servingTicketID, nextTicketID, remainingCount));
				}
			});

			Intent serviceIntent = new Intent(this, UpdateService.class);
			bindService(serviceIntent, _serviceConnection, BIND_AUTO_CREATE);
		}
	}

}
