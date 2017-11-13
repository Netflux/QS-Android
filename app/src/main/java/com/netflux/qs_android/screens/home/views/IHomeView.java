package com.netflux.qs_android.screens.home.views;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.netflux.adp.ui.view.IBaseView;
import com.netflux.qs_android.data.pojos.Ticket;


public interface IHomeView extends IBaseView {

	interface HomeViewListener {

		/**
		 * Callback when the ticket button is pressed.
		 */
		void handleTicket();

	}

	/**
	 * Display the progress bar.
	 */
	void displayProgressBar();

	/**
	 * Bind the ticket data to the UI.
	 * @param currentTicket - The current ticket.
	 * @param servingTicket - The serving ticket.
	 * @param nextTicket - The next ticket.
	 * @param statistics - The ticket statistics.
	 */
	void bindData(@Nullable Ticket currentTicket, @Nullable Ticket servingTicket, @Nullable Ticket nextTicket, Bundle statistics);

	/**
	 * Bind the system data to the UI.
	 * @param systemStatus - The system status.
	 * @param statistics - The ticket statistics.
	 */
	void bindData(boolean systemStatus, Bundle statistics);

	/**
	 * Toggle the "Handle Ticket" button mode.
	 * @param mode Whether the user already has a ticket.
	 */
	void toggleTicketButtonMode(boolean mode);

	/**
	 * Set a listener for the Home View.
	 * @param listener - The listener.
	 */
	void setListener(HomeViewListener listener);

}
