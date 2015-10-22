package com.fouxel.task;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class RowListAdapter  extends BaseAdapter {
	int layoutId;
	LayoutInflater inflater;
	ArrayList<TextMessage> smsRows;
	int titleId;
	int descriptionId;
	MainActivity activity;
	
	RowListAdapter(MainActivity activity, int layoutId, ArrayList<TextMessage> smsRows, int titleId, int descriptionId) {
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.smsRows = smsRows;
		this.layoutId = layoutId;
		this.titleId = titleId;
		this.descriptionId = descriptionId;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return smsRows.size();
	}

	@Override
	public Object getItem(int position) {
		return smsRows.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		TextView title;
		TextView description;
		Button buttonAdd;
		TextMessage textMessage;
		
		if(convertView == null) {
			view = inflater.inflate(layoutId, parent, false);
		} else { 
			view = convertView;
		}
		textMessage = (TextMessage) getItem(position);
		title = (TextView)view.findViewById(titleId);
		description = (TextView)view.findViewById(descriptionId);
		buttonAdd = (Button)view.findViewById(R.id.addButton);
		activity.addButtonClicked(buttonAdd, textMessage, position);
		title.setText(textMessage.getMessageBody());
		description.setText(textMessage.getSenderName());
		
		return view;
	} 
}

