package com.noolite.db.ds;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.noolite.db.NooLiteDB;

/**
 * Created by urix on 13.04.17.
 */

public final class DataSourceManager {
    private final static DataSourceManager dsManager = new DataSourceManager();
    private NooLiteDB dbHelper;
    private SQLiteDatabase database;
    private GroupDataSource group;
    private ChannelsDataSource channel;
    private TimerDataSource timer;


    private DataSourceManager() {}

    public static DataSourceManager getInstance() {
        return dsManager;
    }

    private NooLiteDB getHelper(Context context) {

        if (dbHelper == null) {
            dbHelper = new NooLiteDB(context);
        }
        return dbHelper;
    }

    private SQLiteDatabase getDatabase(Context context) {

        if (database == null) {
            database = getHelper(context).getWritableDatabase();
        }
        return database;
    }

    private void open(Context context) {

        if (!getDatabase(context).isOpen()) {
            dbHelper = new NooLiteDB(context);
            database = dbHelper.getWritableDatabase();
        }
    }

    public GroupDataSource getGroupDS(Context context) {
        open(context);

        if (group == null) {
            group = new GroupDataSource();
        }
        group.setDatabase(database);
        return group;
    }

    public ChannelsDataSource getChannelsDS(Context context) {
        open(context);

        if (channel == null) {
            channel = new ChannelsDataSource();
        }
        channel.setDatabase(database);
        return channel;
    }

    public TimerDataSource getTimerDS(Context context) {
        open(context);

        if (timer == null) {
            timer = new TimerDataSource();
        }
        timer.setDatabase(database);
        return timer;
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

}
