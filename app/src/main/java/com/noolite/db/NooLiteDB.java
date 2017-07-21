package com.noolite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.noolite.db.ds.BasicDataSource;

/**
 * Created by urix on 12.04.17.
 */

public class NooLiteDB extends SQLiteOpenHelper {

    public NooLiteDB(Context context) {
        super(context, BasicDataSource.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("create table ").append(BasicDataSource.TABLE_CHANNEL);
        sql.append(" (").append(BasicDataSource.ID).append(" integer primary key not null,");
        sql.append("  ").append(BasicDataSource.NAME).append(" text not null,");
        sql.append("  ").append(BasicDataSource.TYPE).append(" integer not null,");
        sql.append("  ").append(BasicDataSource.STATE).append(" integer not null,");
        sql.append("  ").append(BasicDataSource.PREVIOUS_STATE).append(" integer not null);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("create table ").append(BasicDataSource.TABLE_GROUP);
        sql.append(" (").append(BasicDataSource.ID).append(" integer primary key not null,");
        sql.append("  ").append(BasicDataSource.NAME).append(" text not null,");
        sql.append("  ").append(BasicDataSource.VISIBLE).append(" integer not null);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("create table ").append(BasicDataSource.TABLE_GROUP_CHANNEL);
        sql.append(" (").append(BasicDataSource.GROUP_ID).append(" integer not null,");
        sql.append("  ").append(BasicDataSource.CHANNEL_ID).append(" integer not null);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("create table ").append(BasicDataSource.TABLE_SENSOR);
        sql.append(" (").append(BasicDataSource.ID).append(" integer primary key not null,");
        sql.append("  ").append(BasicDataSource.NAME).append(" text not null);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("create table ").append(BasicDataSource.TABLE_GROUP_SENSOR);
        sql.append(" (").append("groupId").append(" integer not null,");
        sql.append("  ").append("sensorId").append(" integer not null);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("create table ").append(BasicDataSource.TABLE_TIMER);
        sql.append(" (").append(BasicDataSource.ID).append(" integer key,");
        sql.append("  ").append(BasicDataSource.IS_ON).append(" integer not null,");
        sql.append("  ").append(BasicDataSource.SINGLE_ACTIVATION).append(" integer not null,");
        sql.append("  ").append(BasicDataSource.HOURS).append(" integer not null,");
        sql.append("  ").append(BasicDataSource.MINUTES).append(" integer not null,");
        sql.append("  ").append(BasicDataSource.DAYS_OF_WEEK).append(" integer not null,");
        sql.append("  ").append(BasicDataSource.COMMAND).append(" integer not null);");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
