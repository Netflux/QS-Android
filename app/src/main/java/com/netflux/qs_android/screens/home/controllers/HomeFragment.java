package com.netflux.qs_android.screens.home.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.netflux.adp.ui.controller.BaseFragment;
import com.netflux.adp.util.BackgroundThreadPoster;
import com.netflux.adp.util.MainThreadPoster;
import com.netflux.qs_android.App;
import com.netflux.qs_android.R;
import com.netflux.qs_android.data.models.TicketModel;
import com.netflux.qs_android.data.pojos.Ticket;
import com.netflux.qs_android.screens.home.views.HomeView;
import com.netflux.qs_android.screens.home.views.IHomeView;
import com.netflux.qs_android.utils.Constants;
import com.netflux.qs_android.utils.NetworkManager;


public class HomeFragment extends BaseFragment implements IHomeView.HomeViewListener {

	private HomeView _view;

	private SharedPreferences _prefs;
	private NetworkManager _networkManager;
	private TicketModel _ticketModel;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		_view = new HomeView(inflater, container);
		_view.setListener(this);

		getToolbar().setTitle(R.string.app_name);

		_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		long currentTicketID = _prefs.getLong(Constants.Prefs.TICKET_CURRENT_ID, -1);
		long servingTicketID = _prefs.getLong(Constants.Prefs.TICKET_SERVING_ID, -1);

		_view.setTicketNumber(currentTicketID);
		_view.setServingNumber(servingTicketID);
		_view.toggleTicketButtonMode(currentTicketID != -1);

		return _view.getRootView();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		_networkManager = ((App) getActivity().getApplication()).getNetworkManager();
		_ticketModel = ((App) getActivity().getApplication()).getTicketModel();
	}

	@Override
	public void handleTicket() {
		if (_prefs.getLong(Constants.Prefs.TICKET_CURRENT_ID, -1) == -1) {
			// Fetch a new ticket from the server
			BackgroundThreadPoster.getInstance().post(new Runnable() {
				@Override
				public void run() {
					final Ticket ticket = _networkManager.getNewTicket();

					if (ticket != null) {
						SharedPreferences.Editor editor = _prefs.edit();
						editor.putLong(Constants.Prefs.TICKET_CURRENT_ID, ticket.getId());
						editor.apply();

						_ticketModel.addOrUpdateSync(ticket);

						MainThreadPoster.getInstance().post(new Runnable() {
							@Override
							public void run() {
								_view.setTicketNumber(ticket.getId());
								_view.toggleTicketButtonMode(ticket.getId() != -1);
							}
						});
					}
				}
			});
		} else {
			// Cancel the current ticket on the server
			// TODO
		}
	}
}
