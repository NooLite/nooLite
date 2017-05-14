package com.noolite.parsers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.noolite.channels.ChannelElement;
import com.noolite.db.ds.ChannelsDataSource;
import com.noolite.db.ds.GroupDataSource;
import com.noolite.db.ds.TimerDataSource;
import com.noolite.groups.GroupElement;
import com.noolite.timers.TimerElement;

public class BinParser {

    private byte[] data;  //информация для парсинга
	//значения сдвигов, числа считываемых элементов в соответствии с бинарным файлом
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

	private ChannelsDataSource channelsDS;
    private GroupDataSource groupDS;
    private TimerDataSource timerDS;

	public BinParser(byte[] data, Context context) {
		this.data = data;

        channelsDS = new ChannelsDataSource(context);
        channelsDS.open();

        groupDS = new GroupDataSource(context);
        groupDS.open();

        timerDS = new TimerDataSource(context);
        timerDS.open();
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void parceData() throws UnsupportedEncodingException {

		int currentPosition = 0;
        channelsDS.deleteAll();
        groupDS.deleteAll();

		//считывание информации о группах
		for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
			ArrayList<Integer> channels = new ArrayList<Integer>();
			ArrayList<Integer> sensors = new ArrayList<Integer>();
			boolean visibility = true;

			//получение имени группы
			String name = this.getName(currentPosition+START_OFFSET);
			//получение параметра видимости
			visibility = this.getVisibilityOfGroup(data[START_OFFSET+currentPosition
					+ OFFSET]);

			//парсинг подключенных датчиков и добавление индексов сенсоров в ArrayList
			for (int j = 1; j <= NUMBER_OF_SENSORS; j++) {
				sensors.add(this.getSensorVisibility(data[START_OFFSET+currentPosition
						+ OFFSET + j]));
			}


			//парсинг каналов группы и добавление индексов каналов в ArrayList
			for (int j = 0; j < NUMBER_OF_CHANNELS_IN_GROUP; j++) {
				channels.add(this.getChannelInfo(data[currentPosition+START_OFFSET + OFFSET
						+ j]));
			}

			//добавление полученной группы в БД групп
			GroupElement newElement = new GroupElement(i, name, channels,
					sensors, visibility);
            groupDS.add(newElement);
			//изменение сурсора текущей позиции
			currentPosition += GROUP_OFFSET;
		}


		//парсинг информации о каналах
		for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
			int type = 0;
			//получение имени канала
            String name = this.getName(currentPosition+START_OFFSET);
			//получение типа канада
			type = data[START_OFFSET+currentPosition + OFFSET];

			//создание нового объекта канала и добавление его в БД
			ChannelElement newElement = new ChannelElement(i+1, name, type,
					0, 0);
            channelsDS.add(newElement);
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
            timerDS.add(newTimer);
            currentPosition +=TIMER_OFFSET;
        }
	}

	//получение имени из массива байт
	private String getName(int currentPosition) throws UnsupportedEncodingException {
		String result = null;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(data, currentPosition, 24);
			result = os.toString("cp1251");
            os.close();
		} catch (IOException e) {
			Log.d("getName", e.toString());
		}
		return (result != null) ? result.trim(): "no name";
	}

	private int getCode(byte b) {
		return b & FULL_BYTE;
	}

	private Integer getChannelInfo(byte b) {
		return b & FIRST_SIX_BYTES;
	}

	private int getSensorVisibility(byte b) {
		return b >> 6;
	}

	private boolean getVisibilityOfGroup(byte b) {
		int res = b >> 6;
        return (res == Integer.parseInt(BigInteger.ZERO.toString()));
	}

    private boolean[] getBooleanArray(byte value) {
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
