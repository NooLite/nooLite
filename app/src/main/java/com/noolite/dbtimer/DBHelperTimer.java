package com.noolite.dbtimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.noolite.dbgroup.DBSettingsGroup;

//класс для создания или обновления БД
public class DBHelperTimer extends SQLiteOpenHelper {

    public DBHelperTimer(Context context) {
        super(context, DBSettingsTimer.DB_NAME, null, 1);
    }

    //создание БД
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DBSettingsTimer.TABLE_NAME + "("
                + DBSettingsTimer.ID + " " + DBSettingsTimer.ID_SETTINGS + ", "
                + DBSettingsTimer.IS_ON + " " + DBSettingsTimer.IS_ON_SETTINGS + ", "
                + DBSettingsTimer.SINGLE_ACTIVATION + " " + DBSettingsTimer.SINGLE_ACTIVATION_SETTINGS + ", "
                + DBSettingsTimer.HOURS + " " + DBSettingsTimer.HOURS_SETTINGS + ", "
                + DBSettingsTimer.MINUTES + " " + DBSettingsTimer.MINUTES_SETTINGS + ", "
                + DBSettingsTimer.DAYS_OF_WEEK + " " + DBSettingsTimer.DAYS_OF_WEEK_SETTINGS + ", "
                + DBSettingsTimer.COMMAND + " " + DBSettingsTimer.COMMAND_SETTINGS
                + ")");
    }

    //обновление структуры БД
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
