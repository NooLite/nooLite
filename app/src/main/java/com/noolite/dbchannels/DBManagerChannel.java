package com.noolite.dbchannels;

import java.text.ParseException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.noolite.channels.ChannelElement;
import com.noolite.groups.GroupElement;

//класс-синглтон для работы с БД каналов
public class DBManagerChannel {

	private static DBManagerChannel instance;
	private static SQLiteDatabase db;
	private static Context context;

	public DBManagerChannel(Context context) {
		connect(context);
	}

	private static synchronized DBManagerChannel createInstance(Context context) {
		instance = new DBManagerChannel(context);
		return instance;
	}

	public static DBManagerChannel getInstance(Context context) {
		DBManagerChannel localInstance = instance;
		if (localInstance == null) {
			synchronized (DBManagerChannel.class) {
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
			db = new DBHelperChannel(context).getWritableDatabase();
		} catch (Exception ex) {
			ex.printStackTrace();
			db = null;
		}

		if (db != null) {
			return true;
		}

		return false;
	}

	//добавление нового канала в БД
	public static synchronized void add(ChannelElement newChannel) {
		ContentValues cv;
		cv = new ContentValues();
		cv.put(DBSettingsChannel.ID, newChannel.getId());
		cv.put(DBSettingsChannel.NAME, newChannel.getName());
		cv.put(DBSettingsChannel.TYPE, newChannel.getType());
		cv.put(DBSettingsChannel.STATE, newChannel.getState());
		cv.put(DBSettingsChannel.PREVIOUS_STATE, newChannel.getPreviousState());

		//проведение транзакции добавления значений
		db.beginTransaction();
		db.insert("channels", null, cv);
		db.setTransactionSuccessful();
		db.endTransaction();
		cv.clear();
	}

	//обновление значений записи в БД с индексом newChannel.getId()
	public static synchronized void update(ChannelElement newChannel) {
		ContentValues cv = new ContentValues();
		cv.put(DBSettingsChannel.ID, newChannel.getId());
		cv.put(DBSettingsChannel.NAME, newChannel.getName());
		cv.put(DBSettingsChannel.TYPE, newChannel.getType());
		cv.put(DBSettingsChannel.STATE, newChannel.getState());
		cv.put(DBSettingsChannel.PREVIOUS_STATE, newChannel.getPreviousState());
		db.update(DBSettingsChannel.TABLE_NAME, cv,
				"id = '" + newChannel.getId() + "'", null);
	}

	//удаление записи из БД по id элемента
	public static synchronized void delete(GroupElement notification) {
		db.delete(DBSettingsChannel.TABLE_NAME,
				"id = " + String.valueOf(notification.getId()), null);
	}

	//удаление всех записей в БД
	public static synchronized void deleteAll() {
		db.delete(DBSettingsChannel.TABLE_NAME,null, null);
	}

	//чтение всех записей из БД
	public static synchronized ArrayList<ChannelElement> getAll()
			throws ParseException {
		ArrayList<ChannelElement> listOfAllChannels = new ArrayList<ChannelElement>();
		Cursor c = db.query(DBSettingsChannel.TABLE_NAME, null, null, null,
				null, null, null);

		if (c.moveToFirst()) {
			int idColumn = c.getColumnIndex(DBSettingsChannel.ID);
			int nameColumn = c.getColumnIndex(DBSettingsChannel.NAME);
			int typeColumn = c.getColumnIndex(DBSettingsChannel.TYPE);
			int stateColumn = c.getColumnIndex(DBSettingsChannel.STATE);
			int previousStateColumn = c
					.getColumnIndex(DBSettingsChannel.PREVIOUS_STATE);

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
