package com.netflux.qs_android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.util.JsonReader;
import android.util.JsonToken;

import com.netflux.qs_android.data.pojos.Ticket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public final class Utils {

	public static String getUUID(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String uuid;

		// If a unique UUID has not been stored, generate it
		if (!prefs.contains(Constants.Prefs.UUID)) {
			uuid = UUID.randomUUID().toString();

			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(Constants.Prefs.UUID, uuid);
			editor.apply();
		} else {
			uuid = prefs.getString(Constants.Prefs.UUID, "");
		}

		return uuid;
	}

	public static final class Json {

		public static Pair<Long, List<Ticket>> readJsonTickets(InputStream in) throws IOException {
			long lastID = -1;
			List<Ticket> tickets = new ArrayList<>();

			try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();

					if (name.equals("lastID")) {
						lastID = reader.nextLong();
					} else if (name.equals("data")) {
						reader.beginArray();
						while (reader.hasNext()) {
							tickets.add(readJsonTicket(reader));
						}
						reader.endArray();
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();

				return new Pair<>(lastID, tickets);
			}
		}

		public static Ticket readJsonNewTicket(InputStream in) throws IOException {
			try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();

					if (name.equals("data")) {
						return readJsonTicket(reader);
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();

				return null;
			}
		}

		private static Ticket readJsonTicket(JsonReader reader) throws IOException {
			long id = -1;
			String key = null;
			long time_created = -1;
			long time_served = -1;
			long duration = -1;
			boolean cancelled = false;

			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();

				if (name.equals("id")) {
					id = reader.nextLong();
				} else if (name.equals("key")) {
					key = reader.nextString();
				} else if (name.equals("time_created")) {
					time_created = reader.nextLong();
				} else if (name.equals("time_served") && reader.peek() != JsonToken.NULL) {
					time_served = reader.nextLong();
				} else if (name.equals("duration") && reader.peek() != JsonToken.NULL) {
					duration = reader.nextLong();
				} else if (name.equals("cancelled")) {
					cancelled = reader.nextBoolean();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();

			return new Ticket(id, key, time_created, time_served, duration, cancelled);
		}

	}

}
