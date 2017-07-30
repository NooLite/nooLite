package com.noolite.parsers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.noolite.AppException;
import com.noolite.ResultType;
import com.noolite.NooLiteDefs;
import com.noolite.domain.ChannelElement;
import com.noolite.db.ds.ChannelsDataSource;
import com.noolite.db.ds.DataSourceManager;
import com.noolite.db.ds.GroupDataSource;
import com.noolite.db.ds.TimerDataSource;
import com.noolite.groups.GroupElement;
import com.noolite.groups.SensorElement;
import com.noolite.timers.TimerElement;

public class BinParser {
	// значения сдвигов, числа считываемых элементов в соответствии с бинарным файлом
	private static final int START_OFFSET = 6;
	private static final int OFFSET = 24;
	private static final int GROUP_OFFSET = 32;
	private static final int CHANNEL_OFFSET = 25;
    private static final int TIMER_OFFSET = 7;

	private static final int NUMBER_OF_GROUPS = 16;
	private static final int NUMBER_OF_CHANNELS_IN_GROUP = 8;
	private static final int NUMBER_OF_SENSORS = 4;
	private static final int NUMBER_OF_CHANNELS = 32;
    private static final int NUMBER_OF_TIMERS = 7;

	private static final int FIRST_SIX_BYTES = 0x3F;
	private static final int FULL_BYTE = 0xFF;

    private static String TAG = BinParser.class.getSimpleName();

	private BinParser() {
	}

	public static void parseData(byte[] data, Context context) throws AppException {
        SQLiteDatabase database = null;

        try {
            GroupDataSource groupDS = DataSourceManager.getInstance().getGroupDS(context);
            ChannelsDataSource channelDS = DataSourceManager.getInstance().getChannelsDS(context);
            TimerDataSource tds = DataSourceManager.getInstance().getTimerDS(context);
            database = groupDS.getDatabase();
            database.beginTransaction();

            List<SensorElement> sensorElements = parseSensor(data);
            channelDS.deleteAll();
            groupDS.deleteAll();
            tds.deleteAll();

            int currentPosition = 0;

            for (SensorElement element: sensorElements) {
                groupDS.insertSensor(element);
            }

            //считывание информации о группах
            for (int groupId = 1; groupId <= NUMBER_OF_GROUPS; groupId++) {
                //получение имени группы
                String groupName = getName(data, currentPosition + START_OFFSET);
                //получение параметра видимости
                boolean groupVisibility = getVisibilityOfGroup(data[START_OFFSET + currentPosition + OFFSET]);

                if (groupVisibility) {
                    //добавление полученной группы в БД групп
                    GroupElement groupElement = new GroupElement(groupId, groupName, groupVisibility);
                    groupDS.add(groupElement);

                    //парсинг каналов группы и добавление индексов каналов в ArrayList
                    for (int j = 0; j < NUMBER_OF_CHANNELS_IN_GROUP; j++) {
                        int val = getChannelInfo(data[currentPosition+START_OFFSET + OFFSET + j]);

                        if (val != 0) {
                            channelDS.boundChannel(groupId, val);
                        }
                    }

                    //парсинг подключенных датчиков и добавление индексов сенсоров в ArrayList
                    for (int j = 1; j <= NUMBER_OF_SENSORS; j++) {
                        int val = getSensorVisibility(data[START_OFFSET + currentPosition + OFFSET + j]);

                        if (val != 0) {
                            for (SensorElement sensorElement : sensorElements) {
                                if (sensorElement.getId().intValue() == j) {
                                    groupDS.boundSensor(groupElement, sensorElement);
                                    break;
                                }
                            }
                        }
                    }
                }
                //изменение сурсора текущей позиции
                currentPosition += GROUP_OFFSET;
            }

            //парсинг информации о каналах
            for (int i = 1; i <= NUMBER_OF_CHANNELS; i++) {
                //получение имени канала
                String name = getName(data, currentPosition + START_OFFSET);
                //получение типа канада
                int type = data[START_OFFSET + currentPosition + OFFSET];

                //создание нового объекта канала и добавление его в БД
                ChannelElement newElement = new ChannelElement(i, name, type,0, 0);
                channelDS.add(newElement);

                currentPosition += CHANNEL_OFFSET;
            }

            //сдвиг на позицию файла, где начинается информация о таймерах
            currentPosition = 1313;

            //парсинг информации о таймерах
            for(int i=0; i < NUMBER_OF_TIMERS; i++){

                boolean isOn = true;
                boolean singleActivation = true;

                //получение информации об активности таймера
                if(data[currentPosition]==0){
                    isOn = false;
                }

                //получение информации об однократной активации таймера
                if(data[currentPosition+1]==0){
                    singleActivation = false;
                }

                //получение информации о часах, минутах
                int hour = data[currentPosition+2];
                int minute = data[currentPosition+3];

                //получение информации об активных днях недели
                boolean[] days = getBooleanArray(data[currentPosition+4]);
                int id = data[currentPosition+5];
                //получение информации о типе команды, которая срабатывает при активации таймера
                int command = data[currentPosition+6];

                //создание и добавление в БД нового объекта таймера
                TimerElement newTimer = new TimerElement(id, isOn, singleActivation, days, hour, minute, command);
                tds.add(newTimer);
                currentPosition += TIMER_OFFSET;
            }

            database.setTransactionSuccessful();

        } catch (Exception ex) {
            Log.e(TAG, "parseData", ex);
            throw new AppException(ResultType.PARSE_ERROR);

        } finally {
            database.endTransaction();
        }
    }

	private static List<SensorElement> parseSensor(byte[] data) {
        List<SensorElement> ret = new ArrayList<SensorElement>();
        int m = 1456;

		for (int i = 1; i <= NUMBER_OF_SENSORS; i++) {
			String name = getName(data, m);
			SensorElement element = new SensorElement(name, i);
            ret.add(element);
			Log.d(NooLiteDefs.NOO_LOG, element.toString());
			m += 24;
		}
		return ret;
	}



	//получение имени из массива байт
	private static String getName(byte[] data, int currentPosition) {
		String result = null;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(data, currentPosition, 24);
			result = os.toString("cp1251");
            os.close();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		return (result != null) ? result.trim(): "no name";
	}

	private static int getCode(byte b) {
		return b & FULL_BYTE;
	}

	private  static Integer getChannelInfo(byte b) {
		return b & FIRST_SIX_BYTES;
	}

	private static int getSensorVisibility(byte b) {
		return b >> 6;
	}

	private static boolean getVisibilityOfGroup(byte b) {
		int res = b >> 6;
        return (res == Integer.parseInt(BigInteger.ZERO.toString()));
	}

    private static boolean[] getBooleanArray(byte value) {
        boolean[] result = new boolean[7];
        int power = 1;
        for(int i=0; i<7; i++){

            if(value % 2 ==0){
                result[i] = false;
            }else{
                result[i] = true;
            }

            value /= 2;
            power *=2;
        }
        return result;
    }

}
