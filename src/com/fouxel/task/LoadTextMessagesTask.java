package com.fouxel.task;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LoadTextMessagesTask extends AsyncTask<String, Void, Boolean> {
	private ProgressDialog dialog;
	private MainActivity activity;
	
	public LoadTextMessagesTask(MainActivity activity) {
		this.activity = activity;
		this.dialog = new ProgressDialog(activity);
	}
	
	protected void onPreExecute() {
		this.dialog.setMessage("Loading text messages");
		this.dialog.show();
	}
	
	protected void onPostExecute(final Boolean success) { 
		if (dialog.isShowing()) { 
			dialog.dismiss();
		}
		
		activity.onLoadingMessagesFinished();
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		MessagesManager messagesManager = MessagesManager.getInstance();
		messagesManager.retrieveTextMessagesFromInbox(activity);
		return true;
	}

}
