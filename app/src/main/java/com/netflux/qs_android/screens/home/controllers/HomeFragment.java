package com.netflux.qs_android.screens.home.controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.netflux.adp.ui.controller.BaseFragment;
import com.netflux.adp.util.BackgroundThreadPoster;
import com.netflux.adp.util.MainThreadPoster;
import com.netflux.qs_android.App;
import com.netflux.qs_android.R;
import com.netflux.qs_android.data.models.TicketModel;
import com.netflux.qs_android.data.pojos.Ticket;
import com.netflux.qs_android.screens.common.controllers.MainActivity;
import com.netflux.qs_android.screens.home.views.HomeView;
import com.netflux.qs_android.screens.home.views.IHomeView;
import com.netflux.qs_android.screens.settings.controllers.SettingsFragment;
import com.netflux.qs_android.utils.Constants;
import com.netflux.qs_android.utils.NetworkManager;
import com.netflux.qs_android.utils.UpdateService;


public class HomeFragment extends BaseFragment implements
		IHomeView.HomeViewListener,
		UpdateService.UpdateServiceListener {

	private ServiceConnection _serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			_updateService = ((UpdateService.UpdateServiceBinder) service).getService();
			_updateService.registerListener(HomeFragment.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			_updateService.unregisterListener(HomeFragment.this);
			_updateService = null;
		}
	};

	private HomeView _view;

	private SharedPreferences _prefs;
	private NetworkManager _networkManager;
	private TicketModel _ticketModel;
	private UpdateService _updateService;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		_view = new HomeView(inflater, container);
		_view.setListener(this);

		setupToolbar();

		return _view.getRootView();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		_networkManager = ((App) getActivity().getApplication()).getNetworkManager();
		_ticketModel = ((App) getActivity().getApplication()).getTicketModel();

		updateTicketDisplay();
	}

	@Override
	public void onStart() {
		super.onStart();

		// Bind to the Update Service
		Intent intent = new Intent(getActivity(), UpdateService.class);
		getActivity().bindService(intent, _serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();

		// If bound, unbind from the Update Service
		if (_updateService != null) {
			_updateService.unregisterListener(this);
			getActivity().unbindService(_serviceConnection);
		}
	}

	@Override
	public void handleTicket() {
		BackgroundThreadPoster.getInstance().post(new Runnable() {
			@Override
			public void run() {
				long id = _prefs.getLong(Constants.Prefs.TICKET_CURRENT_ID, -1);

				if (id == -1) {
					// Fetch a new ticket from the server
					final Ticket ticket = _networkManager.getNewTicket();

					if (ticket != null) {
						SharedPreferences.Editor editor = _prefs.edit();
						editor.putLong(Constants.Prefs.TICKET_CURRENT_ID, ticket.getId());
						editor.apply();

						_ticketModel.addOrUpdateSync(ticket);
					}
				} else {
					// Cancel the current ticket on the server
					Ticket ticket = _ticketModel.getSync(id);

					if (ticket != null && _networkManager.cancelTicket(ticket)) {
						SharedPreferences.Editor editor = _prefs.edit();
						editor.putLong(Constants.Prefs.TICKET_CURRENT_ID, -1);
						editor.apply();
					}
				}

				// Update the UI display
				MainThreadPoster.getInstance().post(new Runnable() {
					@Override
					public void run() {
						updateTicketDisplay();
					}
				});
			}
		});
	}

	private void updateTicketDisplay() {
		long currentTicketID = _prefs.getLong(Constants.Prefs.TICKET_CURRENT_ID, -1);
		long servingTicketID = _prefs.getLong(Constants.Prefs.TICKET_SERVING_ID, -1);

		_view.setTicketNumber(currentTicketID);
		_view.setServingNumber(servingTicketID);
		_view.toggleTicketButtonMode(currentTicketID != -1);
	}

	private void setupToolbar() {
		((MainActivity) getActivity()).resetToolbar();
		Toolbar toolbar = getToolbar();
		toolbar.setTitle(R.string.app_name);
		toolbar.inflateMenu(R.menu.menu_home);
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_settings:
						replaceFragment(SettingsFragment.class, true, null);
						return true;
				}

				return false;
			}
		});
	}

	@Override
	public void onStartUpdate() {
		// No action required
	}

	@Override
	public void onFinishUpdate() {
		updateTicketDisplay();
	}

}
