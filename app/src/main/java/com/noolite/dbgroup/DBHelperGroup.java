package com.noolite.dbgroup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//класс, отвечающий за создание БД групп
public class DBHelperGroup extends SQLiteOpenHelper {

	public DBHelperGroup(Context context) {
		super(context, DBSettingsGroup.DB_NAME, null, 1);
	}

	//создание БД
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table " + DBSettingsGroup.TABLE_NAME + "("
				+ DBSettingsGroup.ID + " " + DBSettingsGroup.ID_SETTINGS + ", "
				+ DBSettingsGroup.NAME + " " + DBSettingsGroup.NAME_SETTINGS + ", "
				+ DBSettingsGroup.VISIBLE + " " + DBSettingsGroup.VISIBLE_SETTINGS + ", "
				+ DBSettingsGroup.ITEM1 + " " + DBSettingsGroup.ITEM_SETTINGS + ", "
				+ DBSettingsGroup.ITEM2 + " " + DBSettingsGroup.ITEM_SETTINGS + ", "
				+ DBSettingsGroup.ITEM3 + " " + DBSettingsGroup.ITEM_SETTINGS + ", "
				+ DBSettingsGroup.ITEM4 + " " + DBSettingsGroup.ITEM_SETTINGS + ", "
				+ DBSettingsGroup.ITEM5 + " " + DBSettingsGroup.ITEM_SETTINGS + ", "
				+ DBSettingsGroup.ITEM6 + " " + DBSettingsGroup.ITEM_SETTINGS + ", "
				+ DBSettingsGroup.ITEM7 + " " + DBSettingsGroup.ITEM_SETTINGS + ", "
				+ DBSettingsGroup.ITEM8 + " " + DBSettingsGroup.ITEM_SETTINGS + ", "
				+ DBSettingsGroup.SENSOR1 + " " + DBSettingsGroup.SENSOR_SETTINGS + ", "
				+ DBSettingsGroup.SENSOR2 + " " + DBSettingsGroup.SENSOR_SETTINGS + ", "
				+ DBSettingsGroup.SENSOR3 + " " + DBSettingsGroup.SENSOR_SETTINGS + ", "
				+ DBSettingsGroup.SENSOR4 + " " + DBSettingsGroup.SENSOR_SETTINGS 
				+ ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}