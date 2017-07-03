package com.noolite.db.ds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.noolite.channels.ChannelElement;
import com.noolite.db.NooLiteDB;
import com.noolite.db.NooLiteDBSettings;
import com.noolite.groups.GroupElement;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by urix on 12.04.17.
 */

public class ChannelsDataSource {

    private SQLiteDatabase database;
    private NooLiteDB dbHelper;

    public ChannelsDataSource(Context context) {
        dbHelper = new NooLiteDB(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database.close();
    }

    //добавление нового канала в БД
    public void add(ChannelElement newChannel) {
        ContentValues cv;
        cv = new ContentValues();
        cv.put(NooLiteDBSettings.ID, newChannel.getId());
        cv.put(NooLiteDBSettings.NAME, newChannel.getName());
        cv.put(NooLiteDBSettings.TYPE, newChannel.getType());
        cv.put(NooLiteDBSettings.STATE, newChannel.getState());
        cv.put(NooLiteDBSettings.PREVIOUS_STATE, newChannel.getPreviousState());

        //проведение транзакции добавления значений
        database.beginTransaction();
        database.insert("channels", null, cv);
        database.setTransactionSuccessful();
        database.endTransaction();
        cv.clear();
    }

    //обновление значений записи в БД с индексом newChannel.getId()
    public void update(ChannelElement newChannel) {
        ContentValues cv = new ContentValues();
        cv.put(NooLiteDBSettings.ID, newChannel.getId());
        cv.put(NooLiteDBSettings.NAME, newChannel.getName());
        cv.put(NooLiteDBSettings.TYPE, newChannel.getType());
        cv.put(NooLiteDBSettings.STATE, newChannel.getState());
        cv.put(NooLiteDBSettings.PREVIOUS_STATE, newChannel.getPreviousState());
        database.update(NooLiteDBSettings.TABLE_CHANNELS, cv,
                "id = '" + newChannel.getId() + "'", null);
    }

    //удаление записи из БД по id элемента
    public void delete(GroupElement notification) {
        database.delete(NooLiteDBSettings.TABLE_CHANNELS,
                "id = " + String.valueOf(notification.getId()), null);
    }

    //удаление всех записей в БД
    public void deleteAll() {
        database.delete(NooLiteDBSettings.TABLE_CHANNELS,null, null);
    }

    //чтение всех записей из БД
    public ArrayList<ChannelElement> getAll()
            throws ParseException {
        ArrayList<ChannelElement> listOfAllChannels = new ArrayList<ChannelElement>();
        Cursor c = database.query(NooLiteDBSettings.TABLE_CHANNELS, null, null, null,
                null, null, null);

        if (c.moveToFirst()) {
            int idColumn = c.getColumnIndex(NooLiteDBSettings.ID);
            int nameColumn = c.getColumnIndex(NooLiteDBSettings.NAME);
            int typeColumn = c.getColumnIndex(NooLiteDBSettings.TYPE);
            int stateColumn = c.getColumnIndex(NooLiteDBSettings.STATE);
            int previousStateColumn = c
                    .getColumnIndex(NooLiteDBSettings.PREVIOUS_STATE);

            do {
                int currentId = c.getInt(idColumn);
                String currentName = c.getString(nameColumn);
                int currentType = c.getInt(typeColumn);
                int currentState = c.getInt(stateColumn);
                int previousState = c.getInt(previousStateColumn);
                ChannelElement newChannelItem = new ChannelElement(currentId,
                        currentName, currentType, currentState, previousState);
                listOfAllChannels.add(newChannelItem);

            } while (c.moveToNext());
        }
        c.close();

        return listOfAllChannels;
    }
}
