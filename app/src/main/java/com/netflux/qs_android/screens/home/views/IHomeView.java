package com.netflux.qs_android.screens.home.views;

import com.netflux.adp.ui.view.IBaseView;


public interface IHomeView extends IBaseView {

	interface HomeViewListener {

		/**
		 * Callback when the ticket button is pressed.
		 */
		void handleTicket();

	}

	/**
	 * Set the number for the currently active ticket.
	 * @param number The number (as string).
	 */
	void setTicketNumber(long number);

	/**
	 * Set the number for the ticket being served.
	 * @param number The number (as string).
	 */
	void setServingNumber(long number);

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
