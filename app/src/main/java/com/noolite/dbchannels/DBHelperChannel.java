package com.noolite.dbchannels;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//класс, отвечающий за создание БД каналов
public class DBHelperChannel extends SQLiteOpenHelper {

	public DBHelperChannel(Context context) {
		super(context, DBSettingsChannel.DB_NAME, null, 1);
	}

	//создание БД
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table " + "channels" + "("
				+ DBSettingsChannel.ID + " " + DBSettingsChannel.ID_SETTINGS + ", "
				+ DBSettingsChannel.NAME + " " + DBSettingsChannel.NAME_SETTINGS + ", "
				+ DBSettingsChannel.TYPE + " " + DBSettingsChannel.TYPE_SETTINGS + ", "
				+ DBSettingsChannel.STATE + " " + DBSettingsChannel.STATE_SETTINGS +", "
				+ DBSettingsChannel.PREVIOUS_STATE + " " + DBSettingsChannel.PREVIOUS_STATE_SETTINGS
				+ ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}