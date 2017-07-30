package com.noolite.db.ds;

import android.content.ContentValues;
import android.database.Cursor;

import com.noolite.domain.ChannelElement;
import com.noolite.domain.GroupElement;

import java.util.ArrayList;

/**
 * Created by urix on 12.04.17.
 */

public class ChannelsDataSource extends BasicDataSource {

    public ChannelsDataSource() {
    }

    //добавление нового канала в БД
    public synchronized void add(ChannelElement channel) {
        ContentValues cv = new ContentValues();
        cv.put(ID, channel.getId());
        cv.put(NAME, channel.getName());
        cv.put(TYPE, channel.getType());
        cv.put(STATE, channel.getState());
        cv.put(PREVIOUS_STATE, channel.getPreviousState());
        getDatabase().insert(TABLE_CHANNEL, null, cv);
        cv.clear();
    }

    //обновление значений записи в БД с индексом newChannel.getId()
    public synchronized void update(ChannelElement newChannel) {
        ContentValues cv = new ContentValues();
        cv.put(ID, newChannel.getId());
        cv.put(NAME, newChannel.getName());
        cv.put(TYPE, newChannel.getType());
        cv.put(STATE, newChannel.getState());
        cv.put(PREVIOUS_STATE, newChannel.getPreviousState());
        getDatabase().update(TABLE_CHANNEL, cv,
                "id = '" + newChannel.getId() + "'", null);
    }

    //удаление записи из БД по id элемента
    public synchronized void delete(GroupElement notification) {
        getDatabase().delete(TABLE_CHANNEL, "id = " + String.valueOf(notification.getId()), null);
    }

    //удаление всех записей в БД
    public synchronized void deleteAll() {
        getDatabase().beginTransaction();
        getDatabase().delete(TABLE_GROUP_CHANNEL,null, null);
        getDatabase().delete(TABLE_CHANNEL,null, null);
        getDatabase().setTransactionSuccessful();
        getDatabase().endTransaction();
    }

    //чтение всех записей из БД
    public ArrayList<ChannelElement> getAll() {
        ArrayList<ChannelElement> listOfAllChannels = new ArrayList<ChannelElement>();
        Cursor c = getDatabase().query(TABLE_CHANNEL, null, null, null,
                null, null, null);

        if (c.moveToFirst()) {
            int idColumn = c.getColumnIndex(ID);
            int nameColumn = c.getColumnIndex(NAME);
            int typeColumn = c.getColumnIndex(TYPE);
            int stateColumn = c.getColumnIndex(STATE);
            int previousStateColumn = c
                    .getColumnIndex(PREVIOUS_STATE);

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

    public synchronized void boundChannel(GroupElement group, ChannelElement channel) {
        boundChannel(group.getId(), channel.getId());
    }

    public synchronized void boundChannel(int groupId, int channelId) {
        ContentValues cv = new ContentValues();
        cv.put(GROUP_ID, groupId);
        cv.put(CHANNEL_ID, channelId);
        getDatabase().insert(TABLE_GROUP_CHANNEL, null, cv);
    }
}
