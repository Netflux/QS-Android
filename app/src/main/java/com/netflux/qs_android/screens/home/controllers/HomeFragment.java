package com.netflux.qs_android.screens.home.controllers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.netflux.adp.ui.controller.BaseFragment;
import com.netflux.qs_android.R;
import com.netflux.qs_android.screens.home.views.HomeView;
import com.netflux.qs_android.screens.home.views.IHomeView;


public class HomeFragment extends BaseFragment implements IHomeView.HomeViewListener {

	private HomeView _view;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		_view = new HomeView(inflater, container);
		_view.setListener(this);

		getToolbar().setTitle(R.string.app_name);

		return _view.getRootView();
	}

	@Override
	public void handleTicket() {
		Toast.makeText(getActivity(), "Handle Ticket", Toast.LENGTH_LONG).show();
	}
}
