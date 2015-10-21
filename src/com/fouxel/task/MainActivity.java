package com.fouxel.task;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.fouxel.task.R;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements Observer {
	private ListView list;
	private RowListAdapter adapter;
	private ArrayList<Row> smsRows;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SmsReceiverObserver.getInstance().addObserver(this);
		
		list = (ListView) findViewById(R.id.smsList);
				
		smsRows = new ArrayList<Row>();
		
		adapter = new RowListAdapter(this, R.layout.row, smsRows, R.id.title, R.id.description);
		list.setAdapter(adapter);
		
		smsRows.clear();
		ResourcesHelper.getSMS(this, smsRows);
		adapter.notifyDataSetChanged();
	}
	
	protected void onResume(){
		super.onResume();
	
	}
	
	@Override
	public void update(Observable observable, Object data) {
		if(data.getClass() == Row.class) {
	        smsRows.add(0, (Row)data);
			adapter.notifyDataSetChanged();
		}
	}
	
}
