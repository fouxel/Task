package com.fouxel.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import com.fouxel.task.R;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements Observer {
	private ListView list;
	private RowListAdapter adapter;
	private ArrayList<TextMessage> smsRows;
	private static final int REQUEST_CODE_CALENDAR = 2;
	private ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SmsReceiverObserver.getInstance().addObserver(this);
		
		list = (ListView) findViewById(R.id.smsList);

		smsRows = new ArrayList<TextMessage>();
		
		adapter = new RowListAdapter(this, R.layout.row, smsRows, R.id.title, R.id.description);
		list.setAdapter(adapter);
		
		smsRows.clear();
		ResourcesHelper.getSMS(this, smsRows);
		adapter.notifyDataSetChanged();
		
		actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.messages);
	}
	
	protected void onResume(){
		super.onResume();
	
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("EJAKBED", "ResultCode: " + resultCode);
	};
	
	@Override
	public void update(Observable observable, Object data) {
		if(data.getClass() == TextMessage.class) {
	        smsRows.add(0, (TextMessage)data);
			adapter.notifyDataSetChanged();
		}
	}
	
	public void addButtonClicked(Button addButton, final TextMessage textMessage, final int buttonPosition) { 
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = ResourcesHelper.addEventToCalendar(MainActivity.this, textMessage);
				startActivityForResult(i, REQUEST_CODE_CALENDAR);
			}
		});
	}
}
