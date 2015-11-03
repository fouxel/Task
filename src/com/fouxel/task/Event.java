package com.fouxel.task;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable{ 
	public Date beginTime;
	public Date endTime;

	public Event() {
		beginTime = endTime = null;
	} 

	public Event(Parcel in) {
		this.beginTime = (Date) in.readSerializable();
		this.endTime = (Date) in.readSerializable();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(beginTime);
		dest.writeSerializable(endTime);
	}
	
	

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { 
		public Event createFromParcel(Parcel in) { 
			return new Event(in);
		}
		
		public Event[] newArray(int size) { 
			return new Event[size];
		}
		
	};
	
}		