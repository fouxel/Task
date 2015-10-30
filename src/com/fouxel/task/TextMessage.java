package com.fouxel.task;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.joestelmach.natty.CalendarSource;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class TextMessage implements Parcelable{
	private String senderName;
	private String messageBody;
	private Date eventBeginTime;
	private Date eventEndTime;
	private Date receiveDate;
	private boolean isRead;
	
	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public TextMessage(String senderName, String messageBody, Date receiveDate) {
		this(senderName, messageBody, receiveDate, false);
	}
	
	public TextMessage(String senderName, String messageBody, Date receiveDate, boolean isRead) { 
		super();
		this.senderName = senderName;
		this.messageBody = removeTaskPrefix(messageBody);
		this.isRead = isRead;
		this.receiveDate = receiveDate;
	}
	
	public TextMessage(Parcel in) {
		this.messageBody = in.readString();
		this.senderName = in.readString();
		this.eventBeginTime = (Date) in.readSerializable();
		this.eventEndTime = (Date) in.readSerializable();
		this.receiveDate = (Date) in.readSerializable();
		this.isRead = in.readByte() != 0;
	}

	public String getSenderName() {
		return senderName;
	}
	
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	
	public String toString() { 
		return new String(messageBody + messageBody);
	}
	
	public static boolean isTaskMessage(String input) { 
		if (input.contains(ResourcesHelper.TASK_PREFIX) || input.contains(ResourcesHelper.TASK_PREFIX_UPPERCASE)) { 
			return true;
		}
		return false;
	}
	
	public Date getEventBeginTime() { 
		if (eventBeginTime == null) { 
			resolveTimesFromMessageBody();
		}
		return eventBeginTime;
	}
	
	public Date getEventEndTime() { 
		if (eventEndTime == null) { 
			resolveTimesFromMessageBody();
		}
		return eventEndTime;
	}
	
	/**
	 * Removes .Task prefix from message
	 * 
	 * @param input
	 * @return parsed String
	 */
	public static String removeTaskPrefix(String input) { 
		if (input.contains(ResourcesHelper.TASK_PREFIX)) { 
			return input.replace(ResourcesHelper.TASK_PREFIX, "");
		} else if (input.contains(ResourcesHelper.TASK_PREFIX_UPPERCASE)) {
			return input.replace(ResourcesHelper.TASK_PREFIX_UPPERCASE, "");
		}
		return input;
	}
	
	private void resolveTimesFromMessageBody() { 
	    CalendarSource.setBaseDate(receiveDate);
		
		List<Date> dateList = new ArrayList<Date>();
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(messageBody);
		for (DateGroup group : groups) {
			if (group.getDates() != null) {
				dateList.addAll(group.getDates());
			}
		}
		
		switch (dateList.size()) {
			case 0:
				eventBeginTime = new Date(receiveDate.getTime() + 60*60*1000);
				eventEndTime = eventBeginTime;
				break;
			case 1:
				eventBeginTime = dateList.get(0);
				eventEndTime = dateList.get(0);
				break;
			default:
				eventBeginTime = dateList.get(0);
				eventEndTime = dateList.get(1);
				break;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(messageBody);
		dest.writeString(senderName);
		dest.writeSerializable(eventBeginTime);
		dest.writeSerializable(eventEndTime);
		dest.writeSerializable(receiveDate);
		dest.writeByte((byte) (isRead ? 1 : 0));
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { 
		public TextMessage createFromParcel(Parcel in) { 
			return new TextMessage(in);
		}
		
		public TextMessage[] newArray(int size) { 
			return new TextMessage[size];
		}
		
	};
}