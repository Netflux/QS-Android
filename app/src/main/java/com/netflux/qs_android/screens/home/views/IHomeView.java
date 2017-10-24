package com.netflux.qs_android.screens.home.views;

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
	 * Bind the ticket data to the UI.
	 * @param currentTicket - The current ticket.
	 * @param servingTicket - The serving ticket.
	 * @param nextTicket - The next ticket.
	 */
	void bindData(@Nullable Ticket currentTicket, @Nullable Ticket servingTicket, @Nullable Ticket nextTicket);

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
