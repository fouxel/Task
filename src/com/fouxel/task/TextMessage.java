package com.fouxel.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class TextMessage {
	private String senderName;
	private String messageBody;
	private Date eventBeginTime;
	
	public TextMessage(String senderName, String messageBody) {
		super();
		this.senderName = senderName;
		this.messageBody = removeTaskPrefix(messageBody);
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
}