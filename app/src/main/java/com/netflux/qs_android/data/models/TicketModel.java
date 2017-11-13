package com.netflux.qs_android.data.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.netflux.adp.data.BaseDBModel;
import com.netflux.adp.data.BaseDBOpenHelper;
import com.netflux.qs_android.data.DBContracts.TicketContract;
import com.netflux.qs_android.data.pojos.Ticket;
import com.netflux.qs_android.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TicketModel extends BaseDBModel<Ticket> {

	public static final String KEY_WAITING_TIME = "KEY_WAITING_TIME";
	public static final String KEY_REMAINING_COUNT = "KEY_REMAINING_COUNT";
	public static final String KEY_DURATION = "KEY_DURATION";

	private static final String[] DEFAULT_COL_PROJECTION = new String[] {
			TicketContract._ID,
			TicketContract.COL_KEY,
			TicketContract.COL_SECRET,
			TicketContract.COL_TIME_CREATED,
			TicketContract.COL_TIME_SERVED,
			TicketContract.COL_DURATION,
			TicketContract.COL_STATUS
	};

	private static final String DEFAULT_SORT_ORDER = TicketContract._ID;

	private Context _context;

	public TicketModel(Context context, BaseDBOpenHelper openHelper) {
		super(openHelper);

		_context = context;
	}

	public void addOrUpdateSync(Ticket ticket) {
		SQLiteDatabase db = getDBOpenHelper().getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put(TicketContract._ID, ticket.getId());
		contentValues.put(TicketContract.COL_KEY, ticket.getKey());
		if (ticket.getSecret() != null) {
			contentValues.put(TicketContract.COL_SECRET, ticket.getSecret());
		}
		contentValues.put(TicketContract.COL_TIME_CREATED, ticket.getTimeCreated());
		if (ticket.getTimeServed() != -1) {
			contentValues.put(TicketContract.COL_TIME_SERVED, ticket.getTimeServed());
		}
		contentValues.put(TicketContract.COL_DURATION, ticket.getDuration());
		contentValues.put(TicketContract.COL_STATUS, ticket.getStatus());

		String whereClause = TicketContract._ID + " = ?";
		String[] whereArgs = { String.valueOf(ticket.getId()) };

		db.updateWithOnConflict(TicketContract.TABLE, contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_IGNORE);
		db.insertWithOnConflict(TicketContract.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

		notifyUpdated(ticket);
	}

	public void addOrUpdateSync(List<Ticket> tickets) {
		for (Ticket ticket : tickets) {
			addOrUpdateSync(ticket);
		}
	}

	public List<Ticket> getAllSync() {
		SQLiteDatabase db = getDBOpenHelper().getReadableDatabase();

		Cursor c = db.query(
				TicketContract.TABLE,
				DEFAULT_COL_PROJECTION,
				null,
				null,
				null,
				null,
				DEFAULT_SORT_ORDER
		);

		List<Ticket> result = extractFromCursor(c);

		if (c != null) c.close();

		return result;
	}

	@Nullable
	public Ticket getSync(long id) {
		SQLiteDatabase db = getDBOpenHelper().getReadableDatabase();

		String whereClause = TicketContract._ID + " = ?";
		String[] whereArgs = { String.valueOf(id) };

		Cursor c = db.query(
				TicketContract.TABLE,
				DEFAULT_COL_PROJECTION,
				whereClause,
				whereArgs,
				null,
				null,
				DEFAULT_SORT_ORDER
		);

		List<Ticket> result = extractFromCursor(c);

		if (c != null) c.close();

		return result.size() == 0 ? null : result.get(0);
	}

	@Nullable
	public Ticket getCurrentSync() {
		SQLiteDatabase db = getDBOpenHelper().getReadableDatabase();

		String whereClause = TicketContract.COL_KEY + " = ? AND " +
				TicketContract.COL_STATUS + " IN (?, ?)";
		String[] whereArgs = {
				Utils.getUUID(_context),
				String.valueOf(Ticket.STATUS_PENDING),
				String.valueOf(Ticket.STATUS_SERVING)
		};

		Cursor c = db.query(
				TicketContract.TABLE,
				DEFAULT_COL_PROJECTION,
				whereClause,
				whereArgs,
				null,
				null,
				DEFAULT_SORT_ORDER,
				"1"
		);

		List<Ticket> result = extractFromCursor(c);

		if (c != null) c.close();

		return result.size() == 0 ? null : result.get(0);
	}

	@Nullable
	public Ticket getServingSync() {
		SQLiteDatabase db = getDBOpenHelper().getReadableDatabase();

		String whereClause = TicketContract.COL_STATUS + " = ?";
		String[] whereArgs = { String.valueOf(Ticket.STATUS_SERVING) };

		Cursor c = db.query(
				TicketContract.TABLE,
				DEFAULT_COL_PROJECTION,
				whereClause,
				whereArgs,
				null,
				null,
				DEFAULT_SORT_ORDER,
				"1"
		);

		List<Ticket> result = extractFromCursor(c);

		if (c != null) c.close();

		return result.size() == 0 ? null : result.get(0);
	}

	@Nullable
	public Ticket getNextSync() {
		SQLiteDatabase db = getDBOpenHelper().getReadableDatabase();

		String whereClause = TicketContract.COL_STATUS + " IN (?, ?)";
		String[] whereArgs = {
				String.valueOf(Ticket.STATUS_PENDING),
				String.valueOf(Ticket.STATUS_SERVING)
		};

		Cursor c = db.query(
				TicketContract.TABLE,
				DEFAULT_COL_PROJECTION,
				whereClause,
				whereArgs,
				null,
				null,
				DEFAULT_SORT_ORDER,
				"1"
		);

		List<Ticket> result = extractFromCursor(c);

		if (c != null) c.close();

		return result.size() == 0 ? null : result.get(0);
	}

	public List<Ticket> getRemainingSync() {
		SQLiteDatabase db = getDBOpenHelper().getReadableDatabase();

		String whereClause = TicketContract.COL_STATUS + " IN (?, ?)";
		String[] whereArgs = {
				String.valueOf(Ticket.STATUS_PENDING),
				String.valueOf(Ticket.STATUS_SERVING)
		};

		Cursor c = db.query(
				TicketContract.TABLE,
				DEFAULT_COL_PROJECTION,
				whereClause,
				whereArgs,
				null,
				null,
				DEFAULT_SORT_ORDER
		);

		List<Ticket> result = extractFromCursor(c);

		if (c != null) c.close();

		return result;
	}

	public Bundle getStatistics(@Nullable Ticket servingTicket, List<Ticket> remainingTickets) {
		SQLiteDatabase db = getDBOpenHelper().getReadableDatabase();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		String query = "SELECT count(*), avg(" + TicketContract.COL_TIME_SERVED + "-" + TicketContract.COL_TIME_CREATED + "), (SELECT count(*) FROM " +
				TicketContract.TABLE + " WHERE " + TicketContract.COL_STATUS + " IN (?,?) AND " + TicketContract.COL_KEY + " != ?), avg(" +
				TicketContract.COL_DURATION + ") FROM " + TicketContract.TABLE + " WHERE " + TicketContract.COL_TIME_SERVED + " IS NOT NULL";
		String[] whereArgs = {
				String.valueOf(Ticket.STATUS_PENDING),
				String.valueOf(Ticket.STATUS_SERVING),
				Utils.getUUID(_context)
		};

		long waitingTimeTotal = 0;
		int remainingCount = 0;
		long durationTotal = 0;
		long elapsedTime = 0;
		int divisor = 0;

		// Statistics for all time
		try (Cursor c = db.rawQuery(query, whereArgs)) {
			if (c.moveToFirst() && c.getLong(0) > 0) {
				waitingTimeTotal = c.getLong(1);
				remainingCount = c.getInt(2);
				durationTotal = c.getLong(3);
				divisor = 1;
			}
		}

		// Statistics for current day of week
		try (Cursor c = db.rawQuery(query + " AND strftime('%w', " + TicketContract.COL_TIME_CREATED + " / 1000, 'unixepoch') = ?", new String[] {
				String.valueOf(Ticket.STATUS_PENDING),
				String.valueOf(Ticket.STATUS_SERVING),
				Utils.getUUID(_context),
				String.valueOf(calendar.get(Calendar.DAY_OF_WEEK) - 1)
		})) {
			if (c.moveToFirst() && c.getLong(0) > 0) {
				waitingTimeTotal += c.getLong(1) * 4;
				durationTotal += c.getLong(3) * 4;
				divisor += 4;
			}
		}

		if (remainingTickets.size() > 0) {
			// Parameters for users ahead in the queue query
			List<String> params = new ArrayList<>();
			params.add(String.valueOf(Ticket.STATUS_PENDING));
			params.add(String.valueOf(Ticket.STATUS_SERVING));
			params.add(Utils.getUUID(_context));
			for (Ticket ticket : remainingTickets) {
				params.add(ticket.getKey());
			}

			// Statistics for users ahead in the queue
			try (Cursor c = db.rawQuery(
					query + " AND " + TicketContract.COL_KEY + " IN (" + Utils.makeQueryPlaceholders(remainingTickets.size()) + ")",
					params.toArray(new String[params.size()])
			)) {
				if (c.moveToFirst() && c.getLong(0) > 0) {
					waitingTimeTotal += c.getLong(1) * 6;
					durationTotal += c.getLong(3) * 6;
					divisor += 6;
				}
			}
		}

		// Statistics for current day
		try (Cursor c = db.rawQuery(query + " AND " + TicketContract.COL_TIME_CREATED + " > ?", new String[] {
				String.valueOf(Ticket.STATUS_PENDING),
				String.valueOf(Ticket.STATUS_SERVING),
				Utils.getUUID(_context),
				String.valueOf(calendar.getTimeInMillis())
		})) {
			if (c.moveToFirst() && c.getLong(0) > 0) {
				waitingTimeTotal += c.getLong(1) * 8;
				durationTotal += c.getLong(3) * 8;
				divisor += 8;
			}
		}

		if (servingTicket != null) { elapsedTime = System.currentTimeMillis() - servingTicket.getTimeServed(); }

		Bundle bundle = new Bundle();
		bundle.putLong(KEY_WAITING_TIME, Math.max(0, (waitingTimeTotal / Math.max(1, divisor) * Math.max(1, remainingCount)) - elapsedTime));
		bundle.putInt(KEY_REMAINING_COUNT, remainingCount);
		bundle.putLong(KEY_DURATION, durationTotal / Math.max(1, divisor));
		return bundle;
	}

	private List<Ticket> extractFromCursor(Cursor c) {
		if (c != null && c.moveToFirst()) {
			List<Ticket> result = new ArrayList<>(c.getCount());

			do {
				result.add(
						new Ticket(
								c.getLong(c.getColumnIndexOrThrow(TicketContract._ID)),
								c.getString(c.getColumnIndexOrThrow(TicketContract.COL_KEY)),
								c.getString(c.getColumnIndexOrThrow(TicketContract.COL_SECRET)),
								c.getLong(c.getColumnIndexOrThrow(TicketContract.COL_TIME_CREATED)),
								c.getLong(c.getColumnIndexOrThrow(TicketContract.COL_TIME_SERVED)),
								c.getLong(c.getColumnIndexOrThrow(TicketContract.COL_DURATION)),
								c.getInt(c.getColumnIndexOrThrow(TicketContract.COL_STATUS))
						)
				);
			} while (c.moveToNext());

			return result;
		} else {
			return new ArrayList<>(0);
		}
	}

}
