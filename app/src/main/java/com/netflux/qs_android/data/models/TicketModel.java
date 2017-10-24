package com.netflux.qs_android.data.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.netflux.adp.data.BaseDBModel;
import com.netflux.adp.data.BaseDBOpenHelper;
import com.netflux.qs_android.data.DBContracts.TicketContract;
import com.netflux.qs_android.data.pojos.Ticket;
import com.netflux.qs_android.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class TicketModel extends BaseDBModel<Ticket> {

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
		if (ticket.getDuration() != -1) {
			contentValues.put(TicketContract.COL_DURATION, ticket.getDuration());
		}
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

	public int getRemainingCountSync(long currentId) {
		SQLiteDatabase db = getDBOpenHelper().getReadableDatabase();

		String whereClause = TicketContract._ID + " < ? AND " +
				TicketContract.COL_STATUS + " IN (?, ?)";
		String[] whereArgs = {
				String.valueOf(currentId),
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

		return result.size();
	}

	public List<Ticket> extractFromCursor(Cursor c) {
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
