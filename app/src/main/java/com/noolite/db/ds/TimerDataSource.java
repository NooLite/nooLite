package com.noolite.db.ds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.noolite.db.NooLiteDB;
import com.noolite.db.NooLiteDBSettings;
import com.noolite.timers.TimerElement;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by urix on 12.04.17.
 */

public class TimerDataSource {

    private SQLiteDatabase database;
    private NooLiteDB dbHelper;

    public TimerDataSource(Context context) {
        dbHelper = new NooLiteDB(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //добавление в БД объекта класса TimerElement
    public void add(TimerElement newTimer) {

        //инициализация contentValues
        ContentValues cv;
        cv = new ContentValues();
        cv.put(NooLiteDBSettings.ID, newTimer.getId());
        if(newTimer.isOn())
            cv.put(NooLiteDBSettings.IS_ON, 1);
        else
            cv.put(NooLiteDBSettings.IS_ON, 0);

        if(newTimer.isSingleActivation())
            cv.put(NooLiteDBSettings.SINGLE_ACTIVATION, 1);
        else
            cv.put(NooLiteDBSettings.SINGLE_ACTIVATION, 0);

        cv.put(NooLiteDBSettings.HOURS, newTimer.getHour());
        cv.put(NooLiteDBSettings.MINUTES, newTimer.getMinute());

        cv.put(NooLiteDBSettings.DAYS_OF_WEEK, newTimer.getActiveDays());
        cv.put(NooLiteDBSettings.COMMAND, newTimer.getCommand());

        //проведение транзакции на запись данных
        try {
            database.beginTransaction();
            database.insert(NooLiteDBSettings.TABLE_TIMER, null, cv);
            database.setTransactionSuccessful();
            database.endTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //очистка записанных значений
        cv.clear();
    }

    //удаление таймера по ID
    public void delete(TimerElement notification) {
        database.delete(NooLiteDBSettings.TABLE_TIMER,
                "id = " + String.valueOf(notification.getId()), null);
    }

    //удаление всех записей в БД
    public void deleteAll() {
        database.delete(NooLiteDBSettings.TABLE_TIMER,
                null, null);
    }

    //получение всех записей из БД
    public ArrayList<TimerElement> getAll()
            throws ParseException {
        ArrayList<TimerElement> listOfAllTimers = new ArrayList<TimerElement>();
        Cursor c = database.query(NooLiteDBSettings.TABLE_TIMER, null, null, null, null,
                null, null);

        if (c.moveToFirst()) {
            //инициализация номеров колонок в БД
            int idColumn = c.getColumnIndex(NooLiteDBSettings.ID);
            int isOnColumn = c.getColumnIndex(NooLiteDBSettings.IS_ON);
            int singleActivationColumn = c.getColumnIndex(NooLiteDBSettings.SINGLE_ACTIVATION);
            int hourColumn = c.getColumnIndex(NooLiteDBSettings.HOURS);
            int minuteColumn = c.getColumnIndex(NooLiteDBSettings.MINUTES);
            int daysColumn = c.getColumnIndex(NooLiteDBSettings.DAYS_OF_WEEK);
            int commandColumn = c.getColumnIndex(NooLiteDBSettings.COMMAND);

            //чтение всех записей БД, удовлетворяющих выполненному запросу
            do {
                //получение и обработка данных каждой записи в БД
                int id = c.getInt(idColumn);
                boolean isOn = false;
                if(c.getInt(isOnColumn)==1){
                    isOn = true;
                }
                boolean singleActivation = false;
                if(c.getInt(singleActivationColumn)==1){
                    isOn = true;
                }
                int hour = c.getInt(hourColumn);
                int minute = c.getInt(minuteColumn);

                boolean[] daysOfWeek = new boolean[7];
                int days = c.getInt(daysColumn);
                for(int i=0; i<7; i++){
                    if(days % 2 ==0){
                        daysOfWeek[i] = false;
                    }else{
                        daysOfWeek[i] = true;
                    }
                    days /= 2;
                }

                int command = c.getInt(commandColumn);

                TimerElement newTimerItem = new TimerElement(id,
                        isOn, singleActivation, daysOfWeek, hour, minute, command);
                listOfAllTimers.add(newTimerItem);

            } while (c.moveToNext());
        }
        c.close();
        return listOfAllTimers;
    }
}
