package com.noolite.db.ds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.noolite.db.NooLiteDB;
import com.noolite.db.NooLiteDBSettings;
import com.noolite.groups.GroupElement;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by urix on 12.04.17.
 */

public class GroupDataSource {
    private SQLiteDatabase database;
    private NooLiteDB dbHelper;

    public GroupDataSource(Context context) {
        dbHelper = new NooLiteDB(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //добавление группы в БД
    public void add(GroupElement newGroup) {
        ContentValues cv;
        cv = new ContentValues();
        cv.put(NooLiteDBSettings.ID, newGroup.getId());
        cv.put(NooLiteDBSettings.NAME, newGroup.getName());
        cv.put(NooLiteDBSettings.VISIBLE, newGroup.getVisibility());

        ArrayList<Integer> channels = newGroup.getChannels();
        int count = 1;
        String dbField = new String("item");
        for (int i : channels) {
            cv.put(dbField + String.valueOf(count), i);
            count++;
        }

        ArrayList<Integer> sensors = newGroup.getSensors();
        count = 1;
        dbField = "sensor";
        for (int i : sensors) {
            cv.put(dbField + String.valueOf(count), i);
            count++;
        }

        try {
            database.beginTransaction();
            database.insert(NooLiteDBSettings.TABLE_GROUPS, null, cv);
            database.setTransactionSuccessful();
            database.endTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }

        cv.clear();
    }

    //удаление группы по id
    public void delete(GroupElement notification) {
        database.delete(NooLiteDBSettings.TABLE_GROUPS,
                "id = " + String.valueOf(notification.getId()), null);
    }

    //удаление всех записей БД
    public void deleteAll() {
        database.delete(NooLiteDBSettings.TABLE_GROUPS,
                null, null);
    }

    //чтение всех записей из БД
    public ArrayList<GroupElement> getAll()
            throws ParseException {
        ArrayList<GroupElement> listOfAllGroups = new ArrayList<GroupElement>();
        Cursor c = database.query(NooLiteDBSettings.TABLE_GROUPS, null, null, null, null,
                null, null);

        if (c.moveToFirst()) {
            //инициализация номеров колонок БД
            int idColumn = c.getColumnIndex(NooLiteDBSettings.ID);
            int nameColumn = c.getColumnIndex(NooLiteDBSettings.NAME);
            int visibleColumn = c.getColumnIndex(NooLiteDBSettings.VISIBLE);
            int[] itemColumn = new int[8];
            itemColumn[0] = c.getColumnIndex(NooLiteDBSettings.ITEM1);
            itemColumn[1] = c.getColumnIndex(NooLiteDBSettings.ITEM2);
            itemColumn[2] = c.getColumnIndex(NooLiteDBSettings.ITEM3);
            itemColumn[3] = c.getColumnIndex(NooLiteDBSettings.ITEM4);
            itemColumn[4] = c.getColumnIndex(NooLiteDBSettings.ITEM5);
            itemColumn[5] = c.getColumnIndex(NooLiteDBSettings.ITEM6);
            itemColumn[6] = c.getColumnIndex(NooLiteDBSettings.ITEM7);
            itemColumn[7] = c.getColumnIndex(NooLiteDBSettings.ITEM8);
            int[] sensorColumn = new int[4];
            sensorColumn[0] = c.getColumnIndex(NooLiteDBSettings.SENSOR1);
            sensorColumn[1] = c.getColumnIndex(NooLiteDBSettings.SENSOR2);
            sensorColumn[2] = c.getColumnIndex(NooLiteDBSettings.SENSOR3);
            sensorColumn[3] = c.getColumnIndex(NooLiteDBSettings.SENSOR4);

            //чтение всех записей БД
            do {
                //получение и обработка данных текущей записи
                int visible = c.getInt(visibleColumn);
                int currentId = c.getInt(idColumn);
                String currentName = c.getString(nameColumn);
                ArrayList<Integer> channels = new ArrayList<Integer>();
                for (int i = 0; i < 8; i++) {
                    int currentItem = c.getInt(itemColumn[i]);
                    if (currentItem != 0) {
                        channels.add(currentItem);
                    }
                }
                ArrayList<Integer> sensors = new ArrayList<Integer>();
                for (int i = 0; i < 4; i++) {
                    int currentSensor = c.getInt(sensorColumn[i]);
                    sensors.add(currentSensor);
                }

                GroupElement newGroupItem = new GroupElement(currentId,
                        currentName, channels, sensors, true);

                //добавление объекта класса группы в список осуществляется, если в БД установлено
                //что этот элемент является видимым
                if (visible != 1) {
                    newGroupItem.setVisibility(false);
                } else {
                    listOfAllGroups.add(newGroupItem);
                }
            } while (c.moveToNext());
        }
        c.close();
        return listOfAllGroups;
    }
}
