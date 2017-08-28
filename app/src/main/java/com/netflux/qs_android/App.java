package com.netflux.qs_android;

import android.app.Application;

import com.netflux.adp.data.BaseDBOpenHelper;
import com.netflux.qs_android.data.QSDatabase;
import com.netflux.qs_android.data.models.TicketModel;
import com.netflux.qs_android.utils.NetworkManager;


public class App extends Application {

	private NetworkManager _networkManager;
	private BaseDBOpenHelper _helper;
	private TicketModel _ticketModel;

	@Override
	public void onCreate() {
		super.onCreate();

		_networkManager = new NetworkManager(this);
		_helper = new BaseDBOpenHelper(this, new QSDatabase());
		_ticketModel = new TicketModel(this, _helper);
	}

	public NetworkManager getNetworkManager() {
		return _networkManager;
	}

	public BaseDBOpenHelper getOpenDBHelper() {
		return _helper;
	}

	public TicketModel getTicketModel() {
		return _ticketModel;
	}

}
