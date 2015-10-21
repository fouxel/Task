package com.fouxel.task;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RowListAdapter  extends BaseAdapter {
	int layoutId;
	LayoutInflater inflater;
	ArrayList<Row> smsRows;
	int titleId;
	int descriptionId;
	
	RowListAdapter(Context context, int layoutId, ArrayList<Row> smsRows, int titleId, int descriptionId) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.smsRows = smsRows;
		this.layoutId = layoutId;
		this.titleId = titleId;
		this.descriptionId = descriptionId;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		TextView title;
		TextView description;
		Row row;
		
		if(convertView == null) {
			view = inflater.inflate(layoutId, parent, false);
		} else { 
			view = convertView;
		}
		
		row = (Row) getItem(position);
		title = (TextView)view.findViewById(titleId);
		description = (TextView)view.findViewById(descriptionId);
		
		title.setText(row.getTitle());
		description.setText(row.getDescription());
		
		return view;
	} 
}

