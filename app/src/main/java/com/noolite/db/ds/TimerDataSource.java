package com.noolite.db.ds;

import android.content.ContentValues;
import android.database.Cursor;

import com.noolite.timers.TimerElement;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by urix on 12.04.17.
 */

public class TimerDataSource extends BasicDataSource {

    public TimerDataSource() {
    }

    //добавление в БД объекта класса TimerElement
    public void add(TimerElement newTimer) {

        //инициализация contentValues
        ContentValues cv;
        cv = new ContentValues();
        cv.put(ID, newTimer.getId());
        if(newTimer.isOn())
            cv.put(IS_ON, 1);
        else
            cv.put(IS_ON, 0);

        if(newTimer.isSingleActivation())
            cv.put(SINGLE_ACTIVATION, 1);
        else
            cv.put(SINGLE_ACTIVATION, 0);

        cv.put(HOURS, newTimer.getHour());
        cv.put(MINUTES, newTimer.getMinute());

        cv.put(DAYS_OF_WEEK, newTimer.getActiveDays());
        cv.put(COMMAND, newTimer.getCommand());

        //проведение транзакции на запись данных
        try {
            getDatabase().beginTransaction();
            getDatabase().insert(TABLE_TIMER, null, cv);
            getDatabase().setTransactionSuccessful();
            getDatabase().endTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //очистка записанных значений
        cv.clear();
    }

    //удаление таймера по ID
    public void delete(TimerElement notification) {
        getDatabase().delete(TABLE_TIMER,
                "id = " + String.valueOf(notification.getId()), null);
    }

    //удаление всех записей в БД
    public void deleteAll() {
        getDatabase().delete(TABLE_TIMER, null, null);
    }

    //получение всех записей из БД
    public ArrayList<TimerElement> getAll()
            throws ParseException {
        ArrayList<TimerElement> listOfAllTimers = new ArrayList<TimerElement>();
        Cursor c = getDatabase().query(TABLE_TIMER, null, null, null, null,
                null, null);

        if (c.moveToFirst()) {
            //инициализация номеров колонок в БД
            int idColumn = c.getColumnIndex(ID);
            int isOnColumn = c.getColumnIndex(IS_ON);
            int singleActivationColumn = c.getColumnIndex(SINGLE_ACTIVATION);
            int hourColumn = c.getColumnIndex(HOURS);
            int minuteColumn = c.getColumnIndex(MINUTES);
            int daysColumn = c.getColumnIndex(DAYS_OF_WEEK);
            int commandColumn = c.getColumnIndex(COMMAND);

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
