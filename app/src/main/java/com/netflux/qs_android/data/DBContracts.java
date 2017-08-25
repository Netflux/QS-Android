package com.netflux.qs_android.data;

import android.provider.BaseColumns;


public final class DBContracts {

	/**
	 * Private Constructor. This class should never be instantiated.
	 */
	private DBContracts() {
		throw new AssertionError();
	}

	public static class TicketContract implements BaseColumns {

		public static final String TABLE = "Ticket";
		public static final String COL_KEY = "key";
		public static final String COL_NUMBER = "number";
		public static final String COL_TIME_CREATED = "time_created";
		public static final String COL_TIME_SERVED = "time_served";
		public static final String COL_CANCELLED = "cancelled";

	}

}
