package com.fouxel.task;

import java.util.Observable;

public class SmsReceiverObserver extends Observable {
	private static SmsReceiverObserver instance = new SmsReceiverObserver();
	
	public static SmsReceiverObserver getInstance() { 
		return instance;
	}
	
	private SmsReceiverObserver() { 
		
	}
	
	public void updateValue(Object data) { 
		synchronized (this) {
			setChanged();
			notifyObservers(data);
		}
	}

}
