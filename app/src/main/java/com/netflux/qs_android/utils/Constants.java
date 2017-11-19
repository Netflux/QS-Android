package com.netflux.qs_android.utils;


public final class Constants {

	/**
	 * Private Constructor. This class should never be instantiated.
	 */
	private Constants() {
		throw new AssertionError();
	}

	public static final String SERVER_URL = "http://127.0.0.1";
	public static final String SERVER_URL_WS = "ws://127.0.0.1";

	public static final String DEFAULT_NOTIFICATION_URI = "content://settings/system/notification_sound";

	public static final class Prefs {

		public static final String UUID = "PREFS_UUID";
		public static final String LAST_FETCH = "PREFS_LAST_FETCH";
		public static final String SYSTEM_STATUS = "PREFS_SYSTEM_STATUS";
		public static final String SYSTEM_LOCATION = "PREFS_SYSTEM_LOCATION";

	}

	public static final class WebSocket {

		public static final String MSG_TICKETS_CREATED = "MSG_TICKETS_CREATED";
		public static final String MSG_TICKETS_UPDATED = "MSG_TICKETS_UPDATED";
		public static final String MSG_SYSTEM_ENABLED = "MSG_SYSTEM_ENABLED";
		public static final String MSG_SYSTEM_DISABLED = "MSG_SYSTEM_DISABLED";

	}

}
