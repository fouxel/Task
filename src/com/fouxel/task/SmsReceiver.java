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


public class SmsReceiver extends BroadcastReceiver {

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
				if(TextMessage.isTaskMessage(messageBody)) {
					TextMessage textMessage = new TextMessage(
							ResourcesHelper.getContactNameOrPhoneNumber(context, smsMessage.getOriginatingAddress()),
							messageBody);
					SmsReceiverObserver.getInstance().updateValue(textMessage);
					Intent resultIntent = ResourcesHelper.addEventToCalendar(context, textMessage);
					if(resultIntent != null) {
						addNotification(context, resultIntent, textMessage.getEventBeginTime());
					}
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
