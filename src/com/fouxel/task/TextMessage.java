package com.fouxel.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import android.os.Parcel;
import android.os.Parcelable;

public class TextMessage implements Parcelable{
	private String senderName;
	private String messageBody;
	private Date eventBeginTime;
	private boolean isRead;
	
	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public TextMessage(String senderName, String messageBody) {
		this(senderName, messageBody, false);
	}
	
	public TextMessage(String senderName, String messageBody, boolean isRead) { 
		super();
		this.senderName = senderName;
		this.messageBody = removeTaskPrefix(messageBody);
		this.isRead = isRead;
	}
	
	
	public TextMessage(Parcel in) {
		this.messageBody = in.readString();
		this.senderName = in.readString();
		this.eventBeginTime = (Date) in.readSerializable();
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
			eventBeginTime = resolveBeginTimeFromMessageBody();
		}
		return eventBeginTime;
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
	
	private Date resolveBeginTimeFromMessageBody() { 
		List<Date> dateList = new ArrayList<Date>();

		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(messageBody);
		for (DateGroup group : groups) {
			if (group.getDates() != null) {
				dateList.addAll(group.getDates());
			}
		}
		if(dateList.size() > 0) { 
			Date date = dateList.get(0);
			return date;
		}
		return new Date();
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