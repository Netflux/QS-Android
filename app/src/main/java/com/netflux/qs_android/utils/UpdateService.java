package com.netflux.qs_android.utils;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.netflux.adp.util.BackgroundThreadPoster;
import com.netflux.qs_android.App;
import com.netflux.qs_android.data.models.TicketModel;
import com.netflux.qs_android.data.pojos.Ticket;

import java.util.List;


public class UpdateService extends Service {

	private NetworkManager _networkManager;
	private TicketModel _ticketModel;
	private boolean _isUpdating;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		_networkManager = ((App) getApplication()).getNetworkManager();
		_ticketModel = ((App) getApplication()).getTicketModel();
		_isUpdating = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		BackgroundThreadPoster.getInstance().post(new Runnable() {
			@Override
			public void run() {
				handleUpdate();
			}
		});

		return START_STICKY;
	}

	public void handleUpdate() {
		if (!_isUpdating) {
			_isUpdating = true;

			Pair<Long, List<Ticket>> result = _networkManager.getAllTickets();

			if (result != null && result.second.size() > 0) {
				_ticketModel.addOrUpdateSync(result.second);

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putLong(Constants.Prefs.LAST_ID, result.first);
				editor.apply();
			}

			_isUpdating = false;
		}

		stopSelf();
	}

}
