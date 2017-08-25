package com.netflux.qs_android.data.pojos;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * POJO class representing a Ticket.
 */
public class Ticket implements Parcelable {

	private long _id;
	private String _key;
	private int _number;
	private long _timeCreated;
	private long _timeServed;
	private boolean _cancelled;

	public Ticket(long id, String key, int number, long timeCreated, long timeServed, boolean cancelled) {
		_id = id;
		_key = key;
		_number = number;
		_timeCreated = timeCreated;
		_timeServed = timeServed;
		_cancelled = cancelled;
	}

	protected Ticket(Parcel in) {
		_id = in.readLong();
		_key = in.readString();
		_number = in.readInt();
		_timeCreated = in.readLong();
		_timeServed = in.readLong();
		_cancelled = in.readByte() != 0;
	}

	public long getId() {
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

	public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
		@Override
		public Ticket createFromParcel(Parcel in) {
			return new Ticket(in);
		}

		@Override
		public Ticket[] newArray(int size) {
			return new Ticket[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeString(_key);
		dest.writeInt(_number);
		dest.writeLong(_timeCreated);
		dest.writeLong(_timeServed);
		dest.writeByte((byte) (_cancelled ? 1 : 0));
	}
}
