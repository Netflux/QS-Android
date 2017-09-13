package com.netflux.qs_android.screens.settings.controllers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.netflux.adp.ui.controller.BasePreferenceFragment;
import com.netflux.qs_android.R;
import com.netflux.qs_android.screens.common.controllers.MainActivity;


public class SettingsFragment extends BasePreferenceFragment {

	private static final String DEFAULT_NOTIFICATION_URI = "content://settings/system/notification_sound";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((MainActivity) getActivity()).resetToolbar();
		Toolbar toolbar = getToolbar();
		toolbar.setTitle(R.string.label_settings);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.layout_settings, rootKey);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String path = prefs.getString(getString(R.string.prefs_notificationTone), null);

		if (path == null) {
			path = DEFAULT_NOTIFICATION_URI;
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(getString(R.string.prefs_notificationTone), path);
			editor.apply();
		}

		Uri uri = Uri.parse(path);

		if (uri != null) {
			Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
			Preference pref = findPreference(getString(R.string.prefs_notificationTone));
			pref.setSummary(ringtone.getTitle(getActivity()));
		}
	}

	@Override
	public boolean onPreferenceTreeClick(Preference preference) {
		String key = preference.getKey();

		if (key.equals(getString(R.string.prefs_notificationTone))) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String path = prefs.getString(getString(R.string.prefs_notificationTone), DEFAULT_NOTIFICATION_URI);
			Uri uri = Uri.parse(path);

			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

			startActivityForResult(intent, 0);
			return true;
		}

		return super.onPreferenceTreeClick(preference);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK || requestCode != 0) {
			return;
		}

		Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		String path = DEFAULT_NOTIFICATION_URI;

		if (uri != null) {
			path = uri.toString();
			Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
			Preference pref = findPreference(getString(R.string.prefs_notificationTone));
			pref.setSummary(ringtone.getTitle(getActivity()));
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(getString(R.string.prefs_notificationTone), path);
		editor.apply();
	}

}
