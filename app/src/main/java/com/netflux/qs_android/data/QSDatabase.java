package com.netflux.qs_android.data;

import com.netflux.adp.data.BaseDB;
import com.netflux.qs_android.data.DBContracts.TicketContract;

import java.util.ArrayList;
import java.util.Collections;


public class QSDatabase extends BaseDB {

	@Override
	public String getDatabaseName() {
		return "QSDatabase.db";
	}

	@Override
	public int getDatabaseVersion() {
		return 1;
	}

	@Override
	public String[] getCreateStatements() {
		return new String[] {
				"CREATE TABLE IF NOT EXISTS " + TicketContract.TABLE + " (\n" +
						"\t" + TicketContract._ID + " INTEGER PRIMARY KEY ,\n" +
						"\t" + TicketContract.COL_KEY + " TEXT NOT NULL,\n" +
						"\t" + TicketContract.COL_TIME_CREATED + " UNSIGNED INTEGER NOT NULL,\n" +
						"\t" + TicketContract.COL_TIME_SERVED + " UNSIGNED INTEGER,\n" +
						"\t" + TicketContract.COL_DURATION + " UNSIGNED INTEGER,\n" +
						"\t" + TicketContract.COL_CANCELLED + " BOOLEAN NOT NULL\n" +
						")"
		};
	}

	@Override
	public String[] getUpdateStatements() {
		ArrayList<String> statements = new ArrayList<>();
		statements.add("DROP TABLE IF EXISTS " + TicketContract.TABLE);
		Collections.addAll(statements, getCreateStatements());

		return statements.toArray(new String[statements.size()]);
	}

	@Override
	public String[] getDowngradeStatements() {
		return getUpdateStatements();
	}
}
