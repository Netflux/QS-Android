package com.netflux.qs_android.screens.home.views;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.netflux.qs_android.R;
import com.netflux.qs_android.data.pojos.Ticket;


public class HomeView implements IHomeView {

	private View _rootView;
	private TextView _text_curTicket;
	private TextView _label_serveOrNext;
	private TextView _text_serveOrNext;
	private Button _button_handleTicket;

	private HomeViewListener _listener;

	public HomeView(LayoutInflater inflater, ViewGroup container) {
		_rootView = inflater.inflate(R.layout.layout_home, container, false);
		_text_curTicket = (TextView) _rootView.findViewById(R.id.text_curTicket);
		_label_serveOrNext = (TextView) _rootView.findViewById(R.id.label_serveOrNext);
		_text_serveOrNext = (TextView) _rootView.findViewById(R.id.text_serveOrNext);
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
	public void bindData(@Nullable Ticket currentTicket, @Nullable Ticket servingTicket, @Nullable Ticket nextTicket) {
		_text_curTicket.setText(currentTicket != null ? String.valueOf(currentTicket.getId()) : "-");
		_label_serveOrNext.setText(_rootView.getContext().getString(R.string.label_curServing));
		_text_serveOrNext.setText(servingTicket != null ? String.valueOf(servingTicket.getId()) : "-");

		if (servingTicket == null) {
			if (nextTicket != null) {
				_label_serveOrNext.setVisibility(View.VISIBLE);
				_text_serveOrNext.setVisibility(View.VISIBLE);

				_label_serveOrNext.setText(_rootView.getContext().getString(R.string.label_nextTicket));
				_text_serveOrNext.setText(String.valueOf(nextTicket.getId()));
			} else {
				_label_serveOrNext.setVisibility(View.GONE);
				_text_serveOrNext.setVisibility(View.GONE);
			}
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
