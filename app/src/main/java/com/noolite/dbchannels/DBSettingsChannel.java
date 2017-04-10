package com.noolite.dbchannels;

//настройки БД каналов
public class DBSettingsChannel {
	static final String DB_NAME = "NOOLITE_CHANNELS";
	static final String TABLE_NAME = "channels";
	static final String ID = "id";
	static final String ID_SETTINGS = "integer primary key autoincrement not null";
	static final String NAME = "name";
	static final String NAME_SETTINGS = "text not null";
	static final String TYPE = "type";
	static final String TYPE_SETTINGS = "integer not null";
	static final String STATE = "state";
	static final String STATE_SETTINGS = "integer not null";
	static final String PREVIOUS_STATE = "previousstate";
	static final String PREVIOUS_STATE_SETTINGS = "integer not null";
}
