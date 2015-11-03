package com.fouxel.task;

public enum Duration {
	DAY (60 * 60 * 24 * 1000, R.string.messages_day),
	WEEK (Duration.DAY.getDurationInMs() * 7, R.string.messages_week),
	MONTH (Duration.DAY.getDurationInMs() * 31, R.string.messages_month),
	YEAR (Duration.DAY.getDurationInMs() * 365, R.string.messages_year),
	INIFINITY (0, R.string.messages_all);
	
	private final long durationInMs;
	private final int infoId;
	
	private Duration(long durationInMs, int infoId) { 
		this.durationInMs = durationInMs;
		this.infoId = infoId;
	}
	
	public long getDurationInMs() { 
		return this.durationInMs;
	}
	
	public int getInfoId() { 
		return this.infoId;
	}
}
