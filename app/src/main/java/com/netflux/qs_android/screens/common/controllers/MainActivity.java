package com.netflux.qs_android.screens.common.controllers;

import android.os.Bundle;

import com.netflux.adp.ui.controller.BaseActivity;
import com.netflux.qs_android.screens.home.controllers.HomeFragment;


public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
			replaceFragment(HomeFragment.class, false, null);
		}
	}

}
