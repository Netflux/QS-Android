package com.netflux.qs_android.utils;


public final class Constants {

	/**
	 * Private Constructor. This class should never be instantiated.
	 */
	private Constants() {
		throw new AssertionError();
	}

	public static final String SERVER_URL = "http://192.168.0.123:3000";
	public static final String SERVER_URL_WS = "ws://192.168.0.123:3000";

	public static final class Prefs {

		public static final String UUID = "PREFS_UUID";
		public static final String LAST_ID = "PREFS_LAST_ID";
		public static final String TICKET_CURRENT_ID = "PREFS_TICKET_CURRENT_ID";
		public static final String TICKET_SERVING_ID = "PREFS_TICKET_SERVING_ID";

	}

	public static final class WebSocket {

		public static final String MSG_TICKETS_CREATED = "MSG_TICKETS_CREATED";
		public static final String MSG_TICKETS_UPDATED = "MSG_TICKETS_UPDATED";

	}

}
