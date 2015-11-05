package com.fouxel.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class LoadTextMessagesTask extends AsyncTask<String, Void, Boolean> {
	private ProgressDialog dialog;
	private MainActivity activity;
	
	public LoadTextMessagesTask(MainActivity activity) {
		this.activity = activity;
		this.dialog = new ProgressDialog(activity);
	}
	
	protected void onPreExecute() {
		this.dialog.setMessage(activity.getResources().getString(R.string.loading_messages));
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
		messagesManager.getTextMessages().clear();
		messagesManager.retrieveTextMessagesFromInbox(activity, Duration.valueOf(params[0]).getDurationInMs(),
				Duration.valueOf(params[0]) == Duration.INIFINITY ? false : true);
		return true;
	}

}
