package com.fouxel.task;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class NotificationResultActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		int notificationId = intent.getIntExtra(ResourcesHelper.NOTIFICATION_ID_NAME, 0);
		NotificationManager notificationManager = 
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notificationId);
		
		boolean isCalendarIntent = intent.getBooleanExtra(ResourcesHelper.FLAG_IS_CALENDAR_INTENT, false);
		TextMessage textMessage = intent.getParcelableExtra(TextMessage.class.toString());
		if (isCalendarIntent) { 
			Intent i = ResourcesHelper.addEventToCalendar(this, textMessage);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		} else { 
			long calendarId = ResourcesHelper.getCalendarId(this);
			ResourcesHelper.insertCalendarEvent(this, calendarId, textMessage);
			Toast.makeText(this, "Event added", Toast.LENGTH_LONG).show();
		}
		
		textMessage.setRead(true);
		moveTaskToBack(true);
		finish();
	}
}
