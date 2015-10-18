package com.fouxel.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fouxel.task.R;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView lv = (ListView) findViewById(R.id.lv);

		/* data source for ListView */
		List<Date> dateList = new ArrayList<Date>();
		/* simplest possible data adapter for ListView (using only built-in item layout) */
		ListAdapter listAdapter = new ArrayAdapter<Date>(this, android.R.layout.simple_list_item_1, dateList);
		lv.setAdapter(listAdapter);

		Log.i("NATTY-APP", "Test app is complete");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
