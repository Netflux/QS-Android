package com.netflux.qs_android.pojos;


/**
 * POJO class representing a Ticket.
 */
public class Ticket {

	private int _id;
	private String _key;
	private int _number;
	private long _timeCreated;
	private long _timeServed;
	private boolean _cancelled;

	public Ticket(int id, String key, int number, long timeCreated, long timeServed, boolean cancelled) {
		_id = id;
		_key = key;
		_number = number;
		_timeCreated = timeCreated;
		_timeServed = timeServed;
		_cancelled = cancelled;
	}

	public int getId() {
		return _id;
	}

	public String getKey() {
		return _key;
	}

	public int getNumber() {
		return _number;
	}

	public long getTimeCreated() {
		return _timeCreated;
	}

	public long getTimeServed() {
		return _timeServed;
	}

	public boolean getCancelled() {
		return _cancelled;
	}

}
