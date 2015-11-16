package com.fouxel.task;

import java.util.Date;
import com.fouxel.task.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat.Builder;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsReceiver extends BroadcastReceiver {

	private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static int notificationId = 0;
	NotificationCompat.Builder builder;
	
	
	public SmsReceiver() { 
		super();
		builder = null;
	}
	
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			
			for (SmsMessage smsMessage : this.getMessagesFromIntent(intent)) {
				String messageBody = smsMessage.getMessageBody();
				Date receiveDate = new Date(smsMessage.getTimestampMillis());
				TextMessage textMessage = TextMessage.createIfDateProvided(
						ResourcesHelper.getSenderName(context, smsMessage.getOriginatingAddress()), messageBody, receiveDate);
				if (textMessage == null) { 
					return;
				}
				MessagesManager.getInstance().addAtBeginning(textMessage);
				SmsReceiverObserver.getInstance().updateValue(true); //true only in order to not to pass null object
				ResourcesHelper.addEventToCalendar(context, textMessage);
				addNotification(context, textMessage);
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
	
	private void addNotification(Context context, TextMessage textMessage) {
		int numberOfUnreadMessages = MessagesManager.getInstance().getUnreadMessagesSize(); 
		if(numberOfUnreadMessages == 0) {
			return;
		}
		
		Intent mainActivity = new Intent(context, MainActivity.class);
		mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingMainActivity = PendingIntent.getActivity(context, (int)System.currentTimeMillis() + 3, mainActivity, 0);
		
		Intent textMessageActivity = new Intent(context, TextMessageActivity.class);
		textMessageActivity.putExtra(ResourcesHelper.MESSAGE_POSITION, 0);
		textMessageActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(TextMessageActivity.class);
		stackBuilder.addNextIntent(textMessageActivity);
		PendingIntent pendingTextMessageActivity =
				stackBuilder.getPendingIntent((int)System.currentTimeMillis() + 4, PendingIntent.FLAG_UPDATE_CURRENT);
		
		if (numberOfUnreadMessages == 1) {
			builder = getNotificationBuilderForSingleMessage(context, textMessage, pendingTextMessageActivity);
		} else { 
			builder = getNotificationBuilderForMultipleMessages(context, pendingMainActivity, numberOfUnreadMessages);
		}
		
		NotificationManager notificationManager = 
			(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.notify(notificationId, builder.build());
	}
	
	private Builder getNotificationBuilderForSingleMessage(Context context, TextMessage textMessage, PendingIntent pendingTextMessageActivityIntent) { 
	    Intent calendarIntent = new Intent(context, NotificationResultActivity.class);
	    calendarIntent.putExtra(ResourcesHelper.NOTIFICATION_ID_NAME, notificationId);
	    calendarIntent.putExtra(ResourcesHelper.FLAG_IS_CALENDAR_INTENT, true);
	    calendarIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    calendarIntent.putExtra(TextMessage.class.toString(), textMessage);
	    
	    Intent addEventIntent = new Intent(context, NotificationResultActivity.class);
	    addEventIntent.putExtra(ResourcesHelper.NOTIFICATION_ID_NAME, notificationId);
	    addEventIntent.putExtra(ResourcesHelper.FLAG_IS_CALENDAR_INTENT, false);
	    addEventIntent.putExtra(TextMessage.class.toString(), textMessage);
		PendingIntent pendingCalendarIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), calendarIntent, 0);
		PendingIntent pendingAddEventIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis() + 1, addEventIntent, 0);
		builder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(context.getResources().getString(R.string.new_task))
				.addAction(R.drawable.ic_check, context.getResources().getString(R.string.add), pendingAddEventIntent)
				.addAction(R.drawable.ic_calendar, context.getResources().getString(R.string.view), pendingCalendarIntent)
				.setWhen(0)
				.setAutoCancel(true)
				.setContentIntent(pendingTextMessageActivityIntent)
				.setContentText(ResourcesHelper.getReadableFormatDate(context, textMessage.getEventBeginTime()));
		return builder;
	}
	
	private Builder getNotificationBuilderForMultipleMessages(Context context, PendingIntent pendingMainActivityIntent, int numberOfUnreadMessages) { 
		builder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(context.getResources().getString(R.string.new_task))
				.setWhen(0)
				.setAutoCancel(true)
				.setContentIntent(pendingMainActivityIntent)
				.setContentText(numberOfUnreadMessages + " " + context.getResources().getString(R.string.unread_messages));
		return builder;
	}
	
}
