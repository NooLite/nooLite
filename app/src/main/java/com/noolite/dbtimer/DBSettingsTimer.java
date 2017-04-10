package com.noolite.dbtimer;

//настройки БД таймеров
public class DBSettingsTimer {

	public static final String DB_NAME = "timers";
	public static final String TABLE_NAME = "timer";
	
	public static final String ID = "id";
	public static final String ID_SETTINGS = "integer key";
	public static final String IS_ON = "isOn";
	public static final String IS_ON_SETTINGS = "integer not null";
	public static final String SINGLE_ACTIVATION = "singleactivation";
	public static final String SINGLE_ACTIVATION_SETTINGS = "integer not null";
	public static final String HOURS = "hours";
	public static final String HOURS_SETTINGS = "integer not null";
	public static final String MINUTES = "minutes";
	public static final String MINUTES_SETTINGS = "integer not null";
	public static final String DAYS_OF_WEEK = "days";
	public static final String DAYS_OF_WEEK_SETTINGS = "integer not null";
    public static final String COMMAND = "command";
    public static final String COMMAND_SETTINGS = "integer not null";
	
}
