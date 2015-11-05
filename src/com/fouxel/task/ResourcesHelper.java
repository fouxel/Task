package com.fouxel.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

public class ResourcesHelper {
	public static final String TASK_PREFIX = ".task ";
	public static final String TASK_PREFIX_UPPERCASE = ".Task ";
	public static final String NOTIFICATION_ID_NAME = "NotificationId";
	public static final String FLAG_IS_CALENDAR_INTENT = "CalendarIntent";
	private static final String NOTIFICATION_FORMAT_DATE = "yyyy.MM.dd";
	private static final String NOTIFICATION_FORMAT_TIME = "HH:mm";
	
	public static String getContactName(Context context, String phoneNumber) { 
		ContentResolver cr = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = cr.query(uri, null, null, null, null);
		if (cursor == null) { 
			return null;
		}
		String contactName = null;
		if (cursor.moveToFirst()) { 
			contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			String photoId = cursor.getString(cursor.getColumnIndex(PhoneLookup.PHOTO_ID));
			if(photoId != null)
				Log.i("lol", photoId);
		}
		
		if (cursor != null && !cursor.isClosed()) { 
			cursor.close();
		}
		
		return contactName;

	}
	
	public static String getSenderName(Context context, String phoneNumber) { 
		String contactName = getContactName(context, phoneNumber);
		if (contactName == null) { 
			return phoneNumber;
		}
		return contactName;
	}
	
	public static Intent addEventToCalendar(Context context, TextMessage textMessage) {
		Intent intent = new Intent(Intent.ACTION_INSERT).setData(Events.CONTENT_URI)
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, textMessage.getEventBeginTime().getTime())
				.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, textMessage.getEventEndTime().getTime())
				.putExtra(Events.TITLE, context.getResources().getString(R.string.meeting))
				.putExtra(Events.DESCRIPTION, getEventDescription(context, textMessage)) 
				.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
				.putExtra(CalendarContract.Reminders.MINUTES, 5);
		return intent;
	}
	

	public static long getCalendarId(Context context) { 
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), new String[]{"_id", "calendar_displayName"}, null, null, null);
		cursor.moveToFirst();
		String calendarNames[] = new String[cursor.getCount()];
		int calendarIds[] = new int[cursor.getCount()];
		for (int i = 0; i < calendarNames.length; i++)
		{
		         calendarIds[i] = cursor.getInt(0);
		         calendarNames[i] = cursor.getString(1);
		         cursor.moveToNext();
		}
		
		if (calendarIds.length == 0) { 
			return 1; //Some exception throwing is needed here probably
		} 
		
		return calendarIds[0];
	}
	
	public static void insertCalendarEvent(Context context, long calendarId, TextMessage textMessage) { 
		long startMillis = 0;
		long endMillis = 0;
		startMillis = textMessage.getEventBeginTime().getTime();
		endMillis = textMessage.getEventEndTime().getTime();
		
		ContentResolver cr = context.getContentResolver();
		ContentValues values  = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.TITLE, context.getResources().getString(R.string.meeting));
		values.put(Events.DESCRIPTION, getEventDescription(context, textMessage));
		values.put(Events.CALENDAR_ID, calendarId);
		values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
		Uri uri = cr.insert(Events.CONTENT_URI, values);
	}
	
	private static String getEventDescription(Context context,TextMessage textMessage) { 
		return "\"" + textMessage.getMessageBody() + "\" " + context.getResources().getString(R.string.from) + " " + textMessage.getSenderName();
	}
	
	public static void insertCalendarEventAndNotify(Context context, TextMessage textMessage) {
		long calendarId = ResourcesHelper.getCalendarId(context);
		ResourcesHelper.insertCalendarEvent(context, calendarId, textMessage);
		Toast.makeText(context, R.string.task_added, Toast.LENGTH_LONG).show();
	}
	
	public static boolean isPast(long millis) { 
		long current = new Date().getTime();
		Log.i("LOL", "current: " + current);
		Log.i("LOL", "millis: " + millis);
		return current > millis ? true : false;
	}

	public static String getReadableFormatDate(Context context, Date inputDate) {
		SimpleDateFormat sDate;
		if(DateUtils.isToday(inputDate.getTime())) { 
			sDate = new SimpleDateFormat("'" + context.getResources().getString(R.string.today) + " " + context.getResources().getString(R.string.at) + " '" + NOTIFICATION_FORMAT_TIME);
		} else {
			sDate = new SimpleDateFormat(NOTIFICATION_FORMAT_DATE + "' " + context.getResources().getString(R.string.at) + " '" + NOTIFICATION_FORMAT_TIME);
		}
		return sDate.format(inputDate);
	}
}
