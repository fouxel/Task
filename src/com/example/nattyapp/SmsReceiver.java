package com.example.nattyapp;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class SmsReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		Log.i("EJAKBED", "1");
		Toast.makeText(context, "Zadanie dodane!", Toast.LENGTH_LONG).show();
		if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			Log.i("EJAKBED", "2");
			Bundle bundle = intent.getExtras();
			int[] pdus = (int[])bundle.get("pdus");
			for (Object pdu : pdus) { 
				
			}
			
			/*for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) { 
				Log.i("EJAKBED", "3");
				String messageBody = smsMessage.getMessageBody();
				if(messageBody.contains(".Task")) { 
					Toast.makeText(context, "Zadanie dodane!", Toast.LENGTH_LONG).show();
					Calendar cal = Calendar.getInstance();
					if(Build.VERSION.SDK_INT >= 14) { 
						Intent intent2 = new Intent(Intent.ACTION_INSERT).setData(Events.CONTENT_URI)
								.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, cal.getTimeInMillis())
								.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis() + 60*1000)
								.putExtra(Events.TITLE, "Yoga")
								.putExtra(Events.DESCRIPTION, "Group class")
								.putExtra(Events.EVENT_LOCATION, "The gym")
								.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
								.putExtra(Intent.EXTRA_EMAIL, "rowan@example.com")
								.putExtra(CalendarContract.Reminders.MINUTES, 5);
					    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent2);
					}
				} else { 
					Toast.makeText(context, "Nie ma zadania!", Toast.LENGTH_LONG).show();
				}
			}*/
		}
	}

}
