package com.fouxel.task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class TextMessageActivity extends AppCompatActivity {
	TextView message;
	TextView decodedDate;
	TextView receiveDate;
	MessagesManager messagesManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_message);
		message = (TextView) findViewById(R.id.message);
		decodedDate = (TextView) findViewById(R.id.decodedDate);
		receiveDate = (TextView) findViewById(R.id.receiveDate);
		messagesManager = MessagesManager.getInstance();
		int index = getIntent().getIntExtra(ResourcesHelper.MESSAGE_POSITION, 0);
		TextMessage textMessage = messagesManager.getTextMessages().get(index);
		if (textMessage == null) { 
			finish(); //TODO: Better error handling is needed
		}
		message.setText(textMessage.getMessageBody());
		decodedDate.setText(ResourcesHelper.getReadableFormatDate(this, textMessage.getEventBeginTime()));
		receiveDate.setText(ResourcesHelper.getReadableFormatDate(this, textMessage.getReceiveDate()));

	}

}
