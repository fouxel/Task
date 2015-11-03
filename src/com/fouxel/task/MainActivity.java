package com.fouxel.task;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import com.fouxel.task.R;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Observer {
	private ListView list;
	private RowListAdapter adapter;
	private static final int REQUEST_CODE_CALENDAR = 2;
	private ActionBar actionBar;
	private TextView noMessagesInfo;
	private MessagesManager messagesManager;
	private ArrayList<TextMessage> textMessages;
	private Button loadMoreMessages;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SmsReceiverObserver.getInstance().addObserver(this);
		Locale.setDefault(Locale.ENGLISH);
		
		loadMoreMessages = (Button) findViewById(R.id.loadMoreMessagesBtn);
		list = (ListView) findViewById(R.id.smsList);
		noMessagesInfo = (TextView) findViewById(R.id.noMessages);
		
		loadMoreMessagesClicked();
		messagesManager = MessagesManager.getInstance();
		LoadTextMessagesTask task = new LoadTextMessagesTask(this);
		task.execute(MessagesManager.lastUpdate.name());
		
		actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.messages_week);
	}
	
	public void onLoadingMessagesFinished() { 
		textMessages = messagesManager.getTextMessages();
		adapter = new RowListAdapter(this, R.layout.row, R.id.title, R.id.description);
		list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		hideNoMessagesInfoIfListIsEmpty();
		actionBar.setTitle(messagesManager.lastUpdate.getInfoId());
		
	}
	
	protected void onResume(){
		super.onResume();
		messagesManager.markAllMessagesAsRead();
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
	
	@Override
	public void update(Observable observable, Object data) {
		adapter.notifyDataSetChanged();
		hideNoMessagesInfoIfListIsEmpty();				
	}
	
	public void addButtonClicked(ImageButton addButton, final TextMessage textMessage, final int buttonPosition) { 
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = ResourcesHelper.addEventToCalendar(MainActivity.this, textMessage);
				startActivityForResult(i, REQUEST_CODE_CALENDAR);
			}
		});
	}
	
	public void insertButtonClicked(ImageButton insertButton, final TextMessage textMessage) { 
		insertButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ResourcesHelper.insertCalendarEventAndNotify(MainActivity.this, textMessage);
			}
		});
	}
	
	private void loadMoreMessagesClicked() { 
		loadMoreMessages.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoadTextMessagesTask task = new LoadTextMessagesTask(MainActivity.this);
				if (MessagesManager.lastUpdate == Duration.INIFINITY) { 
					return;
				}
				MessagesManager.lastUpdate = Duration.values()[MessagesManager.lastUpdate.ordinal() +1 ];
				task.execute(MessagesManager.lastUpdate.name());
				
			}
		});
	}
	
	private void hideNoMessagesInfoIfListIsEmpty() { 
		if (!textMessages.isEmpty()) { 
			list.setVisibility(View.VISIBLE);
			noMessagesInfo.setVisibility(View.GONE);
		}
		loadMoreMessages.setVisibility(View.VISIBLE);
	}
}
