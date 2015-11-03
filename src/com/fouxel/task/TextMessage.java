package com.fouxel.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.joestelmach.natty.CalendarSource;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import android.os.Parcel;
import android.os.Parcelable;

public class TextMessage implements Parcelable{
	private String senderName;
	private String messageBody;
	private Event event;
	private Date receiveDate;
	private boolean isRead;
	private boolean isTaskMessage;
	
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

	public TextMessage(String senderName, String messageBody, Date receiveDate, Event event) {
		this(senderName, messageBody, receiveDate, event, false);
	}
	
	public TextMessage(String senderName, String messageBody, Date receiveDate, Event event, boolean isRead) { 
		super(); 
		this.senderName = senderName;
		this.messageBody = messageBody;
		this.isRead = isRead;
		this.receiveDate = receiveDate;
		this.event = event;
	}

	public TextMessage(Parcel in) {
		this.messageBody = in.readString();
		this.senderName = in.readString();
		this.event = (Event) in.readParcelable(Event.class.getClassLoader());
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
		return event.beginTime;
	}
	
	public Date getEventEndTime() { 
		return event.endTime;
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
	
	private static Event resolveTimesFromMessageBody(Date receiveDate, String messageBody) {
		Event eventTime = new Event();
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
				eventTime.beginTime = null;
				eventTime.endTime = null;
				break;
			case 1:
				eventTime.beginTime = dateList.get(0);
				eventTime.endTime = dateList.get(0);
				break;
			default:
				eventTime.beginTime = dateList.get(0);
				eventTime.endTime = dateList.get(1);
				break;
		}
		
		return eventTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(messageBody);
		dest.writeString(senderName);
		dest.writeParcelable(event, 0);
		dest.writeSerializable(receiveDate);
		dest.writeByte((byte) (isRead ? 1 : 0));
	}
	
	public static TextMessage createIfDateProvided(String senderName, String messageBody, Date receiveDate) { 
		TextMessage textMessage = null;
		Event event = resolveTimesFromMessageBody(receiveDate, messageBody);
		if (event.beginTime != null) { 
			textMessage = new TextMessage(senderName, messageBody, receiveDate, event);
		}
		return textMessage;
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