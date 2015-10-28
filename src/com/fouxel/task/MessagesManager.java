package com.fouxel.task;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class MessagesManager {
	private static final String INBOX_URI = "content://sms/inbox";
	private static final String ADDRESS = "address";
	private static final String BODY = "body";
	
	private static MessagesManager instance = null;
	private ArrayList<TextMessage> textMessages;

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
	
	/**
	 * Retrieves text messages from inbox and adds it
	 * into textMessages.
	 * 
	 * @param context Context
	 */
	public void retrieveTextMessagesFromInbox(Context context) { 
		Uri SmsUri = Uri.parse(INBOX_URI);
		Cursor cursor = context.getContentResolver().query(SmsUri, null, null, null, null);
		
		while (cursor.moveToNext()) { 
			String address = cursor.getString(cursor.getColumnIndex(ADDRESS));
			String body = cursor.getString(cursor.getColumnIndexOrThrow(BODY));
			String senderName = ResourcesHelper.getSenderName(context, address);
			if(TextMessage.isTaskMessage(body)) {
				textMessages.add(new TextMessage(body, senderName));
			}
		}
	}

}
