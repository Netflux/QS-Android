package com.netflux.qs_android;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.netflux.adp.data.BaseDBOpenHelper;
import com.netflux.qs_android.data.QSDatabase;
import com.netflux.qs_android.data.models.TicketModel;
import com.netflux.qs_android.utils.Constants;

import java.util.UUID;


public class App extends Application {

	BaseDBOpenHelper _helper;
	TicketModel _ticketModel;

	@Override
	public void onCreate() {
		super.onCreate();

		_helper = new BaseDBOpenHelper(this, new QSDatabase());
		_ticketModel = new TicketModel(_helper);

		// If a unique UUID has not been stored, generate it
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.contains(Constants.Prefs.UUID)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(Constants.Prefs.UUID, UUID.randomUUID().toString());
			editor.apply();
		}
	}

	public BaseDBOpenHelper getOpenDBHelper() {
		return _helper;
	}

	public TicketModel getTicketModel() {
		return _ticketModel;
	}
}
