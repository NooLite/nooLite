package com.noolite.db.ds;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by urix on 7/10/2017.
 */

public class BasicDataSource {
    public static final String DB_NAME = "noolite.db";

    public static final String TABLE_CHANNEL = "t_channel";
    public static final String TABLE_GROUP= "t_group";
    public static final String TABLE_SENSOR = "t_sensor";
    public static final String TABLE_TIMER = "t_timer";
    public static final String TABLE_GROUP_SENSOR = "t_group_sensor";
    public static final String TABLE_GROUP_CHANNEL = "t_group_channel";

    public static final String GROUP_ID = "groupId";
    public static final String CHANNEL_ID = "channelId";
    public static final String SENSOR_ID = "sensorId";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String STATE = "state";
    public static final String PREVIOUS_STATE = "previousstate";

    public static final String VISIBLE = "visible";

    public static final String IS_ON = "isOn";
    public static final String SINGLE_ACTIVATION = "singleactivation";
    public static final String HOURS = "hours";
    public static final String MINUTES = "minutes";
    public static final String DAYS_OF_WEEK = "days";
    public static final String COMMAND = "command";

    private SQLiteDatabase database;

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
