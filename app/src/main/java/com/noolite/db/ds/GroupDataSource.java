package com.noolite.db.ds;

import android.content.ContentValues;
import android.database.Cursor;

import com.noolite.domain.ChannelElement;
import com.noolite.domain.GroupElement;
import com.noolite.domain.SensorElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by urix on 12.04.17.
 */

public class GroupDataSource extends BasicDataSource {

    String LIST_SENSOR_PER_GROUP = "SELECT s.id, s.name " +
            "FROM " + TABLE_GROUP_SENSOR + " as gs inner join " +
            TABLE_SENSOR + " as s on gs.sensorId = s.id WHERE gs.groupId = ?";

    String LIST_CHANNEL_PER_GROUP = "SELECT ch.id, ch.name, ch.type, ch.state, ch.previousState " +
            "FROM " + TABLE_GROUP_CHANNEL + " as gch inner join " +
            TABLE_CHANNEL + " as ch on gch.channelId = ch.id WHERE gch.groupId = ?";

    public GroupDataSource() {
    }

    //добавление группы в БД
    public synchronized void add(GroupElement newGroup) {
        ContentValues cv = new ContentValues();
        cv.put(ID, newGroup.getId());
        cv.put(NAME, newGroup.getName());
        cv.put(VISIBLE, newGroup.getVisibility());
        getDatabase().insert(TABLE_GROUP, null, cv);
        cv.clear();
    }

    //удаление группы по id
    public synchronized void delete(GroupElement notification) {
        getDatabase().delete(TABLE_GROUP,
                "id = " + String.valueOf(notification.getId()), null);
    }

    //удаление всех записей БД
    public synchronized void deleteAll() {
        getDatabase().beginTransaction();
        getDatabase().delete(TABLE_GROUP_SENSOR, null, null);
        getDatabase().delete(TABLE_SENSOR, null, null);
        getDatabase().delete(TABLE_GROUP, null, null);
        getDatabase().setTransactionSuccessful();
        getDatabase().endTransaction();

    }

    public GroupElement get(int id) {
        GroupElement groupElement = new GroupElement();

        String sqlQuery = "SELECT gr.id, gr.name, gr.visible "
                + "FROM " + TABLE_GROUP + " as gr "
                + "WHERE gr.id = ?";
        Cursor grCursor = getDatabase().rawQuery(sqlQuery, new String[] {String.valueOf(id)});

        if (grCursor.moveToFirst()) {
            int currentId = grCursor.getInt(grCursor.getColumnIndex(ID));
            String currentName = grCursor.getString(grCursor.getColumnIndex(NAME));
            grCursor.close();

            groupElement = new GroupElement(currentId, currentName, true);

            Cursor sensCursor = getDatabase().rawQuery(
                    LIST_SENSOR_PER_GROUP, new String[] {String.valueOf(groupElement.getId())});
            List<SensorElement> sensorElements = new ArrayList<SensorElement>();

            if (sensCursor.moveToFirst()) {
                int columnId = sensCursor.getColumnIndex(ID);
                int columnName = sensCursor.getColumnIndex(NAME);

                do {
                    int sensorId = sensCursor.getInt(columnId);
                    String sensorName = sensCursor.getString(columnName);
                    SensorElement element = new SensorElement(sensorName, sensorId);
                    sensorElements.add(element);
                } while (sensCursor.moveToNext());
            }
            sensCursor.close();
            groupElement.setSensorElements(sensorElements);

            Cursor chanCursor = getDatabase().rawQuery(
                    LIST_CHANNEL_PER_GROUP, new String[] {String.valueOf(groupElement.getId())});
            List<ChannelElement> channelElements = new ArrayList<ChannelElement>();

            if (chanCursor.moveToFirst()) {
                do {
                    ChannelElement channelItem =
                            new ChannelElement(chanCursor.getInt(chanCursor.getColumnIndex(ID)));
                    channelItem.setName(chanCursor.getString(chanCursor.getColumnIndex(NAME)));
                    channelItem.setType(chanCursor.getInt(chanCursor.getColumnIndex(TYPE)));
                    channelItem.setState(chanCursor.getInt(chanCursor.getColumnIndex(STATE)));
                    channelItem.setPreviousState(chanCursor.getInt(chanCursor.getColumnIndex(PREVIOUS_STATE)));
                    channelElements.add(channelItem);

                } while (chanCursor.moveToNext());
            }
            chanCursor.close();
            groupElement.setChannelElements(channelElements);
        }
        return groupElement;
    }

    //чтение всех записей из БД
    public ArrayList<GroupElement> getAll() {
        ArrayList<GroupElement> groupList = new ArrayList<GroupElement>();
        String sqlQuery = "SELECT gr.id, gr.name, gr.visible "
                + "FROM " + TABLE_GROUP + " as gr "
                + "WHERE gr.visible = ?";
        Cursor c = getDatabase().rawQuery(sqlQuery, new String[] {"1"});

        if (c.moveToFirst()) {
            //инициализация номеров колонок БД
            int idColumn = c.getColumnIndex(ID);
            int nameColumn = c.getColumnIndex(NAME);

            //чтение всех записей БД
            do {
                //получение и обработка данных текущей записи
                int currentId = c.getInt(idColumn);
                String currentName = c.getString(nameColumn);
                GroupElement groupElement = new GroupElement(currentId, currentName, true);
                groupList.add(groupElement);

            } while (c.moveToNext());
        }
        c.close();
        return groupList;
    }

    public synchronized void insertSensor(SensorElement sensorElement) {
        ContentValues cv;
        cv = new ContentValues();
        cv.put(ID, sensorElement.getId());
        cv.put(NAME, sensorElement.getName());
        getDatabase().insert(TABLE_SENSOR, null, cv);
    }

    public synchronized void deleteSensor(Integer index) {

        if (index != null) {
            getDatabase().beginTransaction();
            getDatabase().delete(TABLE_SENSOR, "id = ?", new String[]{index.toString()});
            getDatabase().delete(TABLE_GROUP_SENSOR, "sensorId = ?", new String[]{index.toString()});
            getDatabase().setTransactionSuccessful();
            getDatabase().endTransaction();
        }
    }

    public synchronized void deleteAllSensor() {
        getDatabase().beginTransaction();
        getDatabase().delete(TABLE_GROUP_SENSOR, null, null);
        getDatabase().delete(TABLE_SENSOR, null, null);
        getDatabase().setTransactionSuccessful();
        getDatabase().endTransaction();
    }

    public synchronized void boundSensor(GroupElement group, SensorElement sensor) {
        ContentValues cv;
        cv = new ContentValues();
        cv.put(GROUP_ID, group.getId());
        cv.put(SENSOR_ID, sensor.getId());
        getDatabase().insert(TABLE_GROUP_SENSOR, null, cv);
    }
}
