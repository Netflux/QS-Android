package com.netflux.qs_android.screens.home.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.netflux.qs_android.R;


public class HomeView implements IHomeView {

	private View _rootView;
	private TextView _text_curTicket;
	private TextView _text_curServing;
	private Button _button_handleTicket;

	private HomeViewListener _listener;

	public HomeView(LayoutInflater inflater, ViewGroup container) {
		_rootView = inflater.inflate(R.layout.layout_home, container, false);
		_text_curTicket = (TextView) _rootView.findViewById(R.id.text_curTicket);
		_text_curServing = (TextView) _rootView.findViewById(R.id.text_curServing);
		_button_handleTicket = (Button) _rootView.findViewById(R.id.button_handleTicket);

		_button_handleTicket.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_listener != null) {
					_listener.handleTicket();
				}
			}
		});
	}

	@Override
	public void setTicketNumber(long number) {
		if (number == -1) {
			_text_curTicket.setText("-");
		} else {
			_text_curTicket.setText(String.valueOf(number));
		}
	}

	@Override
	public void setServingNumber(long number) {
		if (number == -1) {
			_text_curServing.setText("-");
		} else {
			_text_curServing.setText(String.valueOf(number));
		}
	}

	@Override
	public void toggleTicketButtonMode(boolean mode) {
		if (mode) {
			_button_handleTicket.setText(R.string.label_cancelTicket);
		} else {
			_button_handleTicket.setText(R.string.label_getTicket);
		}
	}

	@Override
	public void setListener(HomeViewListener listener) {
		_listener = listener;
	}

	@Override
	public View getRootView() {
		return _rootView;
	}
}
