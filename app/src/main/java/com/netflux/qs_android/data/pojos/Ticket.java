package com.netflux.qs_android.data.pojos;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * POJO class representing a Ticket.
 */
public class Ticket implements Parcelable {

	public static final int STATUS_CANCELLED = 0;
	public static final int STATUS_PENDING = 1;
	public static final int STATUS_SERVING = 2;
	public static final int STATUS_SERVED = 3;

	private long _id;
	private String _key;
	private String _secret;
	private long _timeCreated;
	private long _timeServed;
	private long _duration;
	private int _status;

	public Ticket(long id, String key, String secret, long timeCreated, long timeServed, long duration, int status) {
		_id = id;
		_key = key;
		_secret = secret;
		_timeCreated = timeCreated;
		_timeServed = timeServed;
		_duration = duration;
		_status = status;
	}

	protected Ticket(Parcel in) {
		_id = in.readLong();
		_key = in.readString();
		_secret = in.readString();
		_timeCreated = in.readLong();
		_timeServed = in.readLong();
		_duration = in.readLong();
		_status = in.readInt();
	}

	public long getId() {
		return _id;
	}

	public String getKey() {
		return _key;
	}

	public String getSecret() {
		return _secret;
	}

	public long getTimeCreated() {
		return _timeCreated;
	}

	public long getTimeServed() {
		return _timeServed;
	}

	public long getDuration() {
		return _duration;
	}

	public int getStatus() {
		return _status;
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
		dest.writeString(_secret);
		dest.writeLong(_timeCreated);
		dest.writeLong(_timeServed);
		dest.writeLong(_duration);
		dest.writeInt(_status);
	}
}
