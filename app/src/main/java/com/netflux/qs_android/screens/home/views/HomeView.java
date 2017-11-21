package com.netflux.qs_android.screens.home.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.netflux.qs_android.R;
import com.netflux.qs_android.data.models.TicketModel;
import com.netflux.qs_android.data.pojos.Ticket;


public class HomeView implements IHomeView {

	private View _rootView;
	private ProgressBar _progressBar;
	private LinearLayout _layout_status;
	private LinearLayout _layout_tickets;
	private ImageView _image_status;
	private TextView _text_status;
	private TextView _text_curTicket;
	private TextView _label_serveOrNext;
	private TextView _text_serveOrNext;
	private TextView _text_location;
	private TextView _text_waitTime;
	private TextView _text_remainingTicketCount;
	private TextView _text_averageDuration;
	private Button _button_handleTicket;

	private HomeViewListener _listener;

	public HomeView(LayoutInflater inflater, ViewGroup container) {
		_rootView = inflater.inflate(R.layout.layout_home, container, false);
		_progressBar = (ProgressBar) _rootView.findViewById(R.id.progressBar);
		_layout_status = (LinearLayout) _rootView.findViewById(R.id.layout_status);
		_layout_tickets = (LinearLayout) _rootView.findViewById(R.id.layout_tickets);
		_image_status = (ImageView) _rootView.findViewById(R.id.image_status);
		_text_status = (TextView) _rootView.findViewById(R.id.text_status);
		_text_curTicket = (TextView) _rootView.findViewById(R.id.text_curTicket);
		_label_serveOrNext = (TextView) _rootView.findViewById(R.id.label_serveOrNext);
		_text_serveOrNext = (TextView) _rootView.findViewById(R.id.text_serveOrNext);
		_text_location = (TextView) _rootView.findViewById(R.id.text_location);
		_text_waitTime = (TextView) _rootView.findViewById(R.id.text_waitTime);
		_text_remainingTicketCount = (TextView) _rootView.findViewById(R.id.text_remainingTicketCount);
		_text_averageDuration = (TextView) _rootView.findViewById(R.id.text_averageDuration);
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
	public void displayProgressBar() {
		_progressBar.setVisibility(View.VISIBLE);
		_layout_status.setVisibility(View.GONE);
		_layout_tickets.setVisibility(View.GONE);
		_button_handleTicket.setEnabled(false);
	}

	@Override
	public void bindData(@Nullable Ticket currentTicket, @Nullable Ticket servingTicket, @Nullable Ticket nextTicket, Bundle statistics, String systemLocation, int remainingTickets) {
		_text_curTicket.setText(currentTicket != null ? String.valueOf(currentTicket.getId()) : "-");
		_label_serveOrNext.setText(_rootView.getContext().getString(R.string.label_curServing));
		_text_serveOrNext.setText(servingTicket != null ? String.valueOf(servingTicket.getId()) : "-");
		_text_location.setText(_rootView.getContext().getString(R.string.label_location, systemLocation));
		bindStatistics(statistics, remainingTickets);

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

		_progressBar.setVisibility(View.GONE);
		_layout_status.setVisibility(View.GONE);
		_layout_tickets.setVisibility(View.VISIBLE);
		_button_handleTicket.setEnabled(currentTicket != null || remainingTickets > 0);
	}

	@Override
	public void bindData(boolean systemStatus, Bundle statistics, String systemLocation, int remainingTickets) {
		_text_status.setText(systemStatus ? R.string.label_allTicketsServed : R.string.label_queueClosed);
		_image_status.setImageResource(systemStatus ? R.drawable.ic_task_done_flat : R.drawable.ic_closed_flat);
		_button_handleTicket.setEnabled(systemStatus);
		_text_location.setText(_rootView.getContext().getString(R.string.label_location, systemLocation));
		bindStatistics(statistics, remainingTickets);

		_progressBar.setVisibility(View.GONE);
		_layout_status.setVisibility(View.VISIBLE);
		_layout_tickets.setVisibility(View.GONE);
		_button_handleTicket.setEnabled(remainingTickets > 0);
	}

	private void bindStatistics(Bundle statistics, int remainingTickets) {
		int estimatedWaitTime = (int) Math.ceil((double) statistics.getLong(TicketModel.KEY_WAITING_TIME) / 60000);
		int remainingTicketCount = statistics.getInt(TicketModel.KEY_REMAINING_COUNT);
		int averageDuration = (int) Math.ceil((double) statistics.getLong(TicketModel.KEY_DURATION) / 60000);

		estimatedWaitTime += averageDuration * remainingTicketCount;

		_text_waitTime.setText(getRootView().getContext().getString(R.string.label_estimatedWaitTime, estimatedWaitTime < 1 ? "<1" : "~" + estimatedWaitTime));
		_text_remainingTicketCount.setText(getRootView().getContext().getString(R.string.label_remainingTicketCount, remainingTickets));
		_text_averageDuration.setText(getRootView().getContext().getString(R.string.label_averageDuration, averageDuration < 1 ? "<1" : "~" + averageDuration));
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
