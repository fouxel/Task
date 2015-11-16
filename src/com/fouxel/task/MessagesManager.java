package com.fouxel.task;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MessagesManager {
	private static final String INBOX_URI = "content://sms/inbox";
	private static final String ADDRESS = "address";
	private static final String BODY = "body";
	
	private static MessagesManager instance = null;
	private ArrayList<TextMessage> textMessages;
	public static Duration lastUpdate = Duration.WEEK;
	
	public ArrayList<TextMessage> getTextMessages() {
		return textMessages;
	}

	private MessagesManager() {
		textMessages = new ArrayList<TextMessage>();

	}

	public static MessagesManager getInstance() {
		if (instance == null) {
			instance = new MessagesManager();
		}
		return instance;
	}
	
	public void add(TextMessage textMessage) {
		textMessages.add(textMessage);
	}

	public void add(TextMessage textMessage, int index) { 
		textMessages.add(index, textMessage);
	}
	
	public void addAtBeginning(TextMessage textMessage) {
		add(textMessage, 0);
	}

	public int getUnreadMessagesSize() { 
		int count = 0;
		for (TextMessage textMessage : textMessages) {
			if (!textMessage.isRead()) {
				count++;
			}
		}
		return count;
	}
	
	public int getMessagesSize() { 
		return textMessages.size();
	}
	
	public void markAllMessagesAsRead() { 
		for (TextMessage textMessage : textMessages) {
			textMessage.setRead(true);
		}
	}
	
	/**
	 * Retrieves text messages from inbox and adds it
	 * into textMessages.
	 * 
	 * @param context Context
	 */
	public void retrieveTextMessagesFromInbox(Context context, long duration, boolean checkDuration) { 
		Uri SmsUri = Uri.parse(INBOX_URI);
		Cursor cursor = context.getContentResolver().query(SmsUri, new String[] {"address", "date", "body"}, "", null, null);
		Date current = new Date();
		long currentMillis = current.getTime();
		while (cursor.moveToNext()) { 
			long millis = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
			if (checkDuration && millis < currentMillis - duration) { 
				return;
			}
			String address = cursor.getString(cursor.getColumnIndex(ADDRESS));
			String body = cursor.getString(cursor.getColumnIndexOrThrow(BODY));
			String senderName = ""; //senderName will be passed only if it is message with date
			TextMessage tm = TextMessage.createIfDateProvided(senderName, body, new Date(millis));
			if (tm != null) {
				tm.setSenderName(ResourcesHelper.getSenderName(context, address));
				textMessages.add(tm);
			}
		}
	}

}
