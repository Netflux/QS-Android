package com.netflux.qs_android.screens.home.controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
				Ticket currentTicket = _ticketModel.getCurrentSync();

				if (currentTicket == null) {
					// Fetch a new ticket from the server
					Ticket ticket = _networkManager.getNewTicket();

					if (ticket != null) {
						_ticketModel.addOrUpdateSync(ticket);
					}
				} else {
					// Cancel the current ticket on the server
					if (!_networkManager.cancelTicket(currentTicket)) {
						// TODO - Show failure message
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
		Ticket currentTicket = _ticketModel.getCurrentSync();
		Ticket servingTicket = _ticketModel.getServingSync();
		Ticket nextTicket = _ticketModel.getNextSync();

		_view.bindData(currentTicket, servingTicket, nextTicket);
		_view.toggleTicketButtonMode(currentTicket != null);
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
