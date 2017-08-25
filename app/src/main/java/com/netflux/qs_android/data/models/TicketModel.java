package com.netflux.qs_android.data.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.netflux.adp.data.BaseDBModel;
import com.netflux.adp.data.BaseDBOpenHelper;
import com.netflux.adp.util.BackgroundThreadPoster;
import com.netflux.qs_android.data.DBContracts.TicketContract;
import com.netflux.qs_android.data.pojos.Ticket;

import java.util.ArrayList;
import java.util.List;


public class TicketModel extends BaseDBModel<Ticket> {

	private static final String[] DEFAULT_COL_PROJECTION = new String[] {
			TicketContract._ID,
			TicketContract.COL_KEY,
			TicketContract.COL_TIME_CREATED,
			TicketContract.COL_TIME_SERVED,
			TicketContract.COL_DURATION,
			TicketContract.COL_CANCELLED
	};

	private static final String DEFAULT_SORT_ORDER = TicketContract._ID;

	public TicketModel(BaseDBOpenHelper openHelper) {
		super(openHelper);
	}

	public void addOrUpdateSync(Ticket ticket) {
		SQLiteDatabase db = getDBOpenHelper().getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put(TicketContract._ID, ticket.getId());
		contentValues.put(TicketContract.COL_KEY, ticket.getKey());
		contentValues.put(TicketContract.COL_TIME_CREATED, ticket.getTimeCreated());
		contentValues.put(TicketContract.COL_TIME_SERVED, ticket.getTimeServed());
		contentValues.put(TicketContract.COL_DURATION, ticket.getDuration());
		contentValues.put(TicketContract.COL_CANCELLED, ticket.getCancelled());

		db.insertWithOnConflict(TicketContract.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

		notifyUpdated(ticket);
	}

	public void addOrUpdate(final Ticket ticket) {
		BackgroundThreadPoster.getInstance().post(new Runnable() {
			@Override
			public void run() {
				addOrUpdateSync(ticket);
			}
		});
	}

	public void addOrUpdateSync(List<Ticket> tickets) {
		for (Ticket ticket : tickets) {
			addOrUpdateSync(ticket);
		}
	}

	public void addOrUpdate(final List<Ticket> tickets) {
		BackgroundThreadPoster.getInstance().post(new Runnable() {
			@Override
			public void run() {
				addOrUpdateSync(tickets);
			}
		});
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

	public void getAll() {
		BackgroundThreadPoster.getInstance().post(new Runnable() {
			@Override
			public void run() {
				notifyFetched(getAllSync());
			}
		});
	}

	@Nullable
	public Ticket getSync(int id) {
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

	public List<Ticket> extractFromCursor(Cursor c) {
		if (c != null && c.moveToFirst()) {
			List<Ticket> result = new ArrayList<>(c.getCount());

			do {
				result.add(
						new Ticket(
								c.getLong(c.getColumnIndexOrThrow(TicketContract._ID)),
								c.getString(c.getColumnIndexOrThrow(TicketContract.COL_KEY)),
								c.getLong(c.getColumnIndexOrThrow(TicketContract.COL_TIME_CREATED)),
								c.getLong(c.getColumnIndexOrThrow(TicketContract.COL_TIME_SERVED)),
								c.getLong(c.getColumnIndexOrThrow(TicketContract.COL_DURATION)),
								c.getInt(c.getColumnIndexOrThrow(TicketContract.COL_CANCELLED)) == 1
						)
				);
			} while (c.moveToNext());

			return result;
		} else {
			return new ArrayList<>(0);
		}
	}

}
