package com.fouxel.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.fouxel.task.R;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsReceiver extends BroadcastReceiver {

	private static final String TASK_PREFIX = ".task ";
	private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String NOTIFICATION_FORMAT = "yyyy.MM.dd 'at' HH:mm";
	private static final String NOTIFICATION_FORMAT_TODAY = "'Today at' HH:mm";
	private static int notificationIdBase = 0;
	NotificationCompat.Builder mBuilder;
	
	
	public SmsReceiver() { 
		super();
		mBuilder = null;
	}
	
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			for (SmsMessage smsMessage : this.getMessagesFromIntent(intent)) { 
				String messageBody = smsMessage.getMessageBody();
				String address = smsMessage.getOriginatingAddress();
				messageBody = messageBody.toLowerCase(Locale.ENGLISH);
				if(messageBody.contains(TASK_PREFIX)) {
					Date beginTime = getBeginTime(messageBody);
					Intent resultIntent = addEventToCalendar(context, beginTime, messageBody, address);
					addNotification(context, resultIntent, beginTime);
				}
			}
		}
	}
	
	private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

        int pduCount = messages.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++) {
            byte[] pdu = (byte[]) messages[i];
            msgs[i] = SmsMessage.createFromPdu(pdu);
        }
        return msgs;
    }
	
	@SuppressLint("NewApi")
	private Intent addEventToCalendar(Context context, Date beginTime, String messageBody, String address) {
		Calendar cal = Calendar.getInstance();
		if(Build.VERSION.SDK_INT >= 14) { 
			Intent intent = new Intent(Intent.ACTION_INSERT).setData(Events.CONTENT_URI)
					.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
					.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, beginTime.getTime() + 60*1000)
					.putExtra(Events.TITLE, "Spotkanie")
					.putExtra(Events.DESCRIPTION, "\"" + removeTaskPrefix(messageBody) + "\" From: " + address)
					.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
					.putExtra(CalendarContract.Reminders.MINUTES, 5);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			return intent;
		}
		return null;
	}
	
	private void addNotification(Context context, Intent resultIntent, Date beginTime) { 
	    mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(true)
				.setContentTitle("Nowe zadanie")
				.setContentText(getNotificationFormatDate(beginTime));
	    
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = 
				stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = 
			(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	
		mNotificationManager.notify(notificationIdBase, mBuilder.build());
		notificationIdBase++;
	}
	
	private Date getBeginTime(String input) { 
		input = removeTaskPrefix(input);
		List<Date> dateList = new ArrayList<Date>();

		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input);
		for (DateGroup group : groups) {
//			int line = group.getLine();
//			int column = group.getPosition();
//			String matchingValue = group.getText();
//			String syntaxTree = group.getSyntaxTree().toStringTree();
//			Map parseMap = group.getParseLocations();
//			boolean isRecurring = group.isRecurring();
//			Date recursUntil = group.getRecursUntil();

			/* if any Dates are present in current group then add them to dateList */
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
	
	private String removeTaskPrefix(String input) { 
		return input.replace(TASK_PREFIX, "");
	}
	
	private String getNotificationFormatDate(Date inputDate) {
		SimpleDateFormat sDate;
		if(isToday(new Date(), inputDate)) { 
			sDate = new SimpleDateFormat(NOTIFICATION_FORMAT_TODAY);
		} else {
			sDate = new SimpleDateFormat(NOTIFICATION_FORMAT);
		}
		return sDate.format(inputDate);
	}
	
	private boolean isToday(Date date1, Date date2) { 
		return date1.getDay() == date2.getDay()
				&& date1.getMonth() == date2.getMonth()
				&& date1.getYear() == date2.getYear();
	}
}
