package com.noolite.dbtimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.noolite.timers.TimerElement;

import java.text.ParseException;
import java.util.ArrayList;

//класс-синглтон для работы с БД таймеров
public class DBManagerTimer {

    private static DBManagerTimer instance;  //текущая сущность класса
    private static SQLiteDatabase db; //база данных
    private static Context context; //Context приложения, от которого запускается БД

    public DBManagerTimer(Context context) {
        connect(context);
    }

    private static synchronized DBManagerTimer createInstance(Context context) {
        instance = new DBManagerTimer(context);
        return instance;
    }

    public static DBManagerTimer getInstance(Context context) {
        DBManagerTimer localInstance = instance;
        if (localInstance == null) {
            synchronized (DBManagerTimer.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = createInstance(context);
                }
            }
        }

        return instance;
    }

    public synchronized boolean connect(Context context) {
        if (db != null) {
            return true;
        }

        try {
            db = new DBHelperTimer(context).getWritableDatabase();
        } catch (Exception ex) {
            ex.printStackTrace();
            db = null;
        }

        if (db != null) {
            return true;
        }

        return false;
    }

    //добавление в БД объекта класса TimerElement
    public static synchronized void add(TimerElement newTimer) {

        //инициализация contentValues
        ContentValues cv;
        cv = new ContentValues();
        cv.put(DBSettingsTimer.ID, newTimer.getId());
        if(newTimer.isOn())
            cv.put(DBSettingsTimer.IS_ON, 1);
        else
            cv.put(DBSettingsTimer.IS_ON, 0);

        if(newTimer.isSingleActivation())
            cv.put(DBSettingsTimer.SINGLE_ACTIVATION, 1);
        else
            cv.put(DBSettingsTimer.SINGLE_ACTIVATION, 0);

        cv.put(DBSettingsTimer.HOURS, newTimer.getHour());
        cv.put(DBSettingsTimer.MINUTES, newTimer.getMinute());

        cv.put(DBSettingsTimer.DAYS_OF_WEEK, newTimer.getActiveDays());
        cv.put(DBSettingsTimer.COMMAND, newTimer.getCommand());

        //проведение транзакции на запись данных
        try {
            db.beginTransaction();
            db.insert(DBSettingsTimer.TABLE_NAME, null, cv);
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //очистка записанных значений
        cv.clear();
    }

    //удаление таймера по ID
    public static synchronized void delete(TimerElement notification) {
        db.delete(DBSettingsTimer.TABLE_NAME,
                "id = " + String.valueOf(notification.getId()), null);
    }

    //удаление всех записей в БД
    public static synchronized void deleteAll() {
        db.delete(DBSettingsTimer.TABLE_NAME,
                null, null);
    }

    //получение всех записей из БД
    public static synchronized ArrayList<TimerElement> getAll()
            throws ParseException {
        ArrayList<TimerElement> listOfAllTimers = new ArrayList<TimerElement>();
        Cursor c = db.query(DBSettingsTimer.TABLE_NAME, null, null, null, null,
                null, null);

        if (c.moveToFirst()) {
            //инициализация номеров колонок в БД
            int idColumn = c.getColumnIndex(DBSettingsTimer.ID);
            int isOnColumn = c.getColumnIndex(DBSettingsTimer.IS_ON);
            int singleActivationColumn = c.getColumnIndex(DBSettingsTimer.SINGLE_ACTIVATION);
            int hourColumn = c.getColumnIndex(DBSettingsTimer.HOURS);
            int minuteColumn = c.getColumnIndex(DBSettingsTimer.MINUTES);
            int daysColumn = c.getColumnIndex(DBSettingsTimer.DAYS_OF_WEEK);
            int commandColumn = c.getColumnIndex(DBSettingsTimer.COMMAND);

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