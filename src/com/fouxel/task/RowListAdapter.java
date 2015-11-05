package com.fouxel.task;

import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class RowListAdapter  extends BaseAdapter {
	int layoutId;
	LayoutInflater inflater;
	int titleId;
	int descriptionId;
	MainActivity activity;
	
	RowListAdapter(MainActivity activity, int layoutId, int titleId, int descriptionId) {
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
		this.titleId = titleId;
		this.descriptionId = descriptionId;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return MessagesManager.getInstance().getTextMessages().size();
	}

	@Override
	public Object getItem(int position) {
		return MessagesManager.getInstance().getTextMessages().get(position);
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
		TextView timeDate;
		ImageButton buttonAdd;
		ImageButton buttonInsert;
		TextMessage textMessage;
		
		if(convertView == null) {
			view = inflater.inflate(layoutId, parent, false);
		} else { 
			view = convertView;
		}
		textMessage = (TextMessage) getItem(position);
		title = (TextView)view.findViewById(titleId);
		description = (TextView)view.findViewById(descriptionId);
		timeDate = (TextView)view.findViewById(R.id.timeDate);
		buttonAdd = (ImageButton) view.findViewById(R.id.addButton);
		buttonInsert = (ImageButton) view.findViewById(R.id.insertButton);
		activity.addButtonClicked(buttonAdd, textMessage, position);
		activity.insertButtonClicked(buttonInsert, textMessage);
		//Log.i("LOL", "body: " + textMessage.getMessageBody());
		//Log.i("LOL", "event: " + ResourcesHelper.getReadableFormatDate(activity, textMessage.getEventBeginTime()));
//		Log.i("LOL", "current: " + ResourcesHelper.getReadableFormatDate(activity, new Date()));
		title.setText(textMessage.getMessageBody());
		description.setText(textMessage.getSenderName());
		timeDate.setText(ResourcesHelper.getReadableFormatDate(activity, textMessage.getEventBeginTime()));
		
		return view;
	} 
}

