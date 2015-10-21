package com.fouxel.task;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class ResourcesHelper {
	private static final String INBOX_URI = "content://sms/inbox";
	private static final String ADDRESS = "address";
	private static final String BODY = "body";
	public static final String TASK_PREFIX = ".task ";
	public static final String TASK_PREFIX_UPPERCASE = ".Task ";

	/**
	 * Retrieves text messages from inbox and adds it
	 * into smsRows parameter.
	 * 
	 * @param context Context
	 * @param smsRows retrieved text messages will be put into this list.
	 */
	public static void getSMS(Context context, ArrayList<Row> smsRows) { 
		Uri uriSMSURI = Uri.parse(INBOX_URI);
		Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);
		
		while (cur.moveToNext()) { 
			String address = cur.getString(cur.getColumnIndex(ADDRESS));
			String body = cur.getString(cur.getColumnIndexOrThrow(BODY));
			String contactName = getContactNameOrPhoneNumber(context, address);
			if(body.contains(TASK_PREFIX) || body.contains(TASK_PREFIX_UPPERCASE)) {
				smsRows.add(new Row(body, contactName));
			}
		}
	}

	/**
	 * Removes .Task prefix from message
	 * 
	 * @param input
	 * @return parsed String
	 */
	
	public static String removeTaskPrefix(String input) { 
		return input.replace(ResourcesHelper.TASK_PREFIX, "");
	}
	
	public static String getContactName(Context context, String phoneNumber) { 
		ContentResolver cr = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		if (cursor == null) { 
			return null;
		}
		String contactName = null;
		if (cursor.moveToFirst()) { 
			contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}
		
		if (cursor != null && !cursor.isClosed()) { 
			cursor.close();
		}
		
		return contactName;

	}
	
	public static String getContactNameOrPhoneNumber(Context context, String phoneNumber) { 
		String contactName = getContactName(context, phoneNumber);
		if (contactName == null) { 
			return phoneNumber;
		}
		return contactName;
	}

}
