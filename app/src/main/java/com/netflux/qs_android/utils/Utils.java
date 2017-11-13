package com.netflux.qs_android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.JsonToken;

import com.netflux.qs_android.data.pojos.Ticket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

	public static String getRandomString() {
		byte[] salt = new byte[16];
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			random = new SecureRandom();
		}
		random.nextBytes(salt);

		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-_";
		StringBuilder builder = new StringBuilder();

		for (byte s : salt) {
			Double index = Math.floor((double) (s + 128) / 255 * (characters.length() - 1));
			builder.append(characters.charAt(index.intValue()));
		}
		builder.append(String.valueOf(System.currentTimeMillis()));

		return builder.toString();
	}

	public static String makeQueryPlaceholders(int length) {
		if (length < 1) {
			throw new RuntimeException("Placeholder length must be 1 or greater");
		} else {
			StringBuilder builder = new StringBuilder((length * 2) - 1);
			builder.append("?");
			for (int i = 1; i < length; ++i) {
				builder.append(",?");
			}
			return builder.toString();
		}
	}

	public static final class Json {

		public static List<Ticket> readJsonTickets(InputStream in) throws IOException {
			List<Ticket> tickets = new ArrayList<>();

			try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();

					if (name.equals("data")) {
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

				return tickets;
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
			String secret = null;
			long time_created = -1;
			long time_served = -1;
			long duration = 0;
			int status = Ticket.STATUS_CANCELLED;

			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();

				if (name.equals("id")) {
					id = reader.nextLong();
				} else if (name.equals("key")) {
					key = reader.nextString();
				} else if (name.equals("secret")) {
					secret = reader.nextString();
				} else if (name.equals("time_created")) {
					time_created = reader.nextLong();
				} else if (name.equals("time_served") && reader.peek() != JsonToken.NULL) {
					time_served = reader.nextLong();
				} else if (name.equals("duration") && reader.peek() != JsonToken.NULL) {
					duration = reader.nextLong();
				} else if (name.equals("status")) {
					status = reader.nextInt();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();

			return new Ticket(id, key, secret, time_created, time_served, duration, status);
		}

	}

}
