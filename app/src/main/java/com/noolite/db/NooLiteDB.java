package com.noolite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.noolite.dbgroup.DBSettingsGroup;

/**
 * Created by urix on 12.04.17.
 */

public class NooLiteDB extends SQLiteOpenHelper {

    public NooLiteDB(Context context) {
        super(context, NooLiteDBSettings.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("create table ").append(NooLiteDBSettings.TABLE_CHANNELS);
        sql.append(" (").append(NooLiteDBSettings.ID).append(" integer primary key autoincrement not null,");
        sql.append("  ").append(NooLiteDBSettings.NAME).append(" text not null,");
        sql.append("  ").append(NooLiteDBSettings.TYPE).append(" integer not null,");
        sql.append("  ").append(NooLiteDBSettings.STATE).append(" integer not null,");
        sql.append("  ").append(NooLiteDBSettings.PREVIOUS_STATE).append(" integer not null);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("create table ").append(NooLiteDBSettings.TABLE_GROUPS);
        sql.append(" (").append(NooLiteDBSettings.ID).append(" integer primary key autoincrement not null,");
        sql.append("  ").append(NooLiteDBSettings.NAME).append(" text not null,");
        sql.append("  ").append(NooLiteDBSettings.VISIBLE).append(" integer not null,");
        sql.append("  ").append(NooLiteDBSettings.ITEM1).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.ITEM2).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.ITEM3).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.ITEM4).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.ITEM5).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.ITEM6).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.ITEM7).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.ITEM8).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.SENSOR1).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.SENSOR2).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.SENSOR3).append(" integer,");
        sql.append("  ").append(NooLiteDBSettings.SENSOR4).append(" integer);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("create table ").append(NooLiteDBSettings.TABLE_TIMER);
        sql.append(" (").append(NooLiteDBSettings.ID).append(" integer key,");
        sql.append("  ").append(NooLiteDBSettings.IS_ON).append(" integer not null,");
        sql.append("  ").append(NooLiteDBSettings.SINGLE_ACTIVATION).append(" integer not null,");
        sql.append("  ").append(NooLiteDBSettings.HOURS).append(" integer not null,");
        sql.append("  ").append(NooLiteDBSettings.MINUTES).append(" integer not null,");
        sql.append("  ").append(NooLiteDBSettings.DAYS_OF_WEEK).append(" integer not null,");
        sql.append("  ").append(NooLiteDBSettings.COMMAND).append(" integer not null);");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
