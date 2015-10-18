package com.example.nattyapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import android.annotation.SuppressLint;
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

	private static final String TASK_PREFIX = ".task";
	
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			for (SmsMessage smsMessage : this.getMessagesFromIntent(intent)) { 
				String messageBody = smsMessage.getMessageBody();
				messageBody = messageBody.toLowerCase();
				if(messageBody.contains(TASK_PREFIX)) { 
					Toast.makeText(context, "Zadanie dodane!", Toast.LENGTH_LONG).show();
					messageBody = messageBody.replace(TASK_PREFIX, "");
					addEventToCalendar(context, messageBody);
				}
			}
		}
	}
	
	private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        String format = intent.getStringExtra("format");

        int pduCount = messages.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++) {
            byte[] pdu = (byte[]) messages[i];
            msgs[i] = SmsMessage.createFromPdu(pdu);
        }
        return msgs;
    }
	
	@SuppressLint("NewApi")
	private void addEventToCalendar(Context context, String messageBody) {
		Calendar cal = Calendar.getInstance();
		if(Build.VERSION.SDK_INT >= 14) { 
			Intent intent2 = new Intent(Intent.ACTION_INSERT).setData(Events.CONTENT_URI)
					.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, cal.getTimeInMillis())
					.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getBeginTime(messageBody))
					.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getBeginTime(messageBody) + 60*1000)
					.putExtra(Events.TITLE, "Spotkanie")
					.putExtra(Events.DESCRIPTION, "Spotkanie")
					.putExtra(Events.EVENT_LOCATION, "Moczydla")
					.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
					.putExtra(CalendarContract.Reminders.MINUTES, 5);
		    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
		}
	}
	
	private long getBeginTime(String input) { 
		List<Date> dateList = new ArrayList<Date>();

		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input);
		for (DateGroup group : groups) {
			List<Date> dates = group.getDates();
			int line = group.getLine();
			int column = group.getPosition();
			String matchingValue = group.getText();
			String syntaxTree = group.getSyntaxTree().toStringTree();
			Map parseMap = group.getParseLocations();
			boolean isRecurreing = group.isRecurring();
			Date recursUntil = group.getRecursUntil();

			/* if any Dates are present in current group then add them to dateList */
			if (group.getDates() != null) {
				dateList.addAll(group.getDates());
			}
		}
		Calendar cal = Calendar.getInstance();
		if(dateList.size() > 0) { 
			Date date = dateList.get(0);
			return date.getTime();
		}
		return cal.getTimeInMillis();
	}
}
