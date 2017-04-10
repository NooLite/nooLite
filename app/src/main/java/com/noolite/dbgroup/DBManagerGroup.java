package com.noolite.dbgroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.widget.Toast;

import com.noolite.groups.GroupElement;

//класс-синглтон для работы с БД групп
public class DBManagerGroup {

	private static DBManagerGroup instance;
	private static SQLiteDatabase db;
	private static Context context;

	public DBManagerGroup(Context context) {
		connect(context);
	}

	private static synchronized DBManagerGroup createInstance(Context context) {
		instance = new DBManagerGroup(context);
		return instance;
	}

	public static DBManagerGroup getInstance(Context context) {
		DBManagerGroup localInstance = instance;
		if (localInstance == null) {
			synchronized (DBManagerGroup.class) {
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
			db = new DBHelperGroup(context).getWritableDatabase();
		} catch (Exception ex) {
			ex.printStackTrace();
			db = null;
		}

		if (db != null) {
			return true;
		}

		return false;
	}


	//добавление группы в БД
	public static synchronized void add(GroupElement newGroup) {
		ContentValues cv;
		cv = new ContentValues();
		cv.put(DBSettingsGroup.ID, newGroup.getId());
		cv.put(DBSettingsGroup.NAME, newGroup.getName());
		cv.put(DBSettingsGroup.VISIBLE, newGroup.getVisibility());

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
			db.beginTransaction();
			db.insert(DBSettingsGroup.TABLE_NAME, null, cv);
			db.setTransactionSuccessful();
			db.endTransaction();

		} catch (Exception e) {
			e.printStackTrace();
		}

		cv.clear();
	}

	//удаление группы по id
	public static synchronized void delete(GroupElement notification) {
		db.delete(DBSettingsGroup.TABLE_NAME,
				"id = " + String.valueOf(notification.getId()), null);
	}

	//удаление всех записей БД
	public static synchronized void deleteAll() {
		db.delete(DBSettingsGroup.TABLE_NAME,
				null, null);
	}

	//чтение всех записей из БД
	public static synchronized ArrayList<GroupElement> getAll()
			throws ParseException {
		ArrayList<GroupElement> listOfAllGroups = new ArrayList<GroupElement>();
		Cursor c = db.query(DBSettingsGroup.TABLE_NAME, null, null, null, null,
				null, null);

		if (c.moveToFirst()) {
			//инициализация номеров колонок БД
			int idColumn = c.getColumnIndex(DBSettingsGroup.ID);
			int nameColumn = c.getColumnIndex(DBSettingsGroup.NAME);
			int visibleColumn = c.getColumnIndex(DBSettingsGroup.VISIBLE);
			int[] itemColumn = new int[8];
			itemColumn[0] = c.getColumnIndex(DBSettingsGroup.ITEM1);
			itemColumn[1] = c.getColumnIndex(DBSettingsGroup.ITEM2);
			itemColumn[2] = c.getColumnIndex(DBSettingsGroup.ITEM3);
			itemColumn[3] = c.getColumnIndex(DBSettingsGroup.ITEM4);
			itemColumn[4] = c.getColumnIndex(DBSettingsGroup.ITEM5);
			itemColumn[5] = c.getColumnIndex(DBSettingsGroup.ITEM6);
			itemColumn[6] = c.getColumnIndex(DBSettingsGroup.ITEM7);
			itemColumn[7] = c.getColumnIndex(DBSettingsGroup.ITEM8);
			int[] sensorColumn = new int[4];
			sensorColumn[0] = c.getColumnIndex(DBSettingsGroup.SENSOR1);
			sensorColumn[1] = c.getColumnIndex(DBSettingsGroup.SENSOR2);
			sensorColumn[2] = c.getColumnIndex(DBSettingsGroup.SENSOR3);
			sensorColumn[3] = c.getColumnIndex(DBSettingsGroup.SENSOR4);

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
