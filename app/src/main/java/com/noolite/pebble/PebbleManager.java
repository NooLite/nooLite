package com.noolite.pebble;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.UUID;

import android.content.Context;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.noolite.NooLiteDefs;
import com.noolite.domain.SensorData;
import com.noolite.util.SensorUtils;
import com.noolite.util.UrlUtils;
import com.noolite.asynctask.RequestTask;
import com.noolite.domain.ChannelElement;
import com.noolite.db.ds.ChannelsDataSource;
import com.noolite.db.ds.DataSourceManager;
import com.noolite.db.ds.GroupDataSource;
import com.noolite.domain.GroupElement;

public class PebbleManager {
	// ключи для словаря
	private final int GROUPE_NAME_KEY = 34;
	private final int CHANNEL_NAME_KEY = 36;
	private final int CHANNEL_TYPE_KEY = 39;
	private final int SENSOR_KEY = 40;

	// ключи для типа команды
	private final int DETECTOR_TYPE_KEY = 4;

	// названия команд, передаваемых на телефон
	private final String RUNNED = "runned";
	private final String GRP_COUNT = "count_grp";
	private final String GRP_INFO = "grpInfo";
	private final String CHN_INFO = "chnlInfo";
	private final String ON_OFF = "on/off";
	private final String DIMMED = "brigth";
	private final String START_OVERFLOW = "startOvr";
	private final String STOP_OVERFLOW = "stopOvr";

	private Context context;



	public PebbleManager(Context context) {
		this.context = context;

	}

	public boolean isConnected() {
		return PebbleKit.isWatchConnected(context);
	}

	//отправка сообщения, генерация ответа в зависимости от типа команды commandCode
	public void sendMessage(String commandCode, UUID univId) throws ParseException,
			UnsupportedEncodingException {
		PebbleDictionary data = null;

		//получение информации о типе команды
		if (commandCode.equals(CHN_INFO)) {
            data = processChannelInfo();
		}

		if (commandCode.equals(GRP_COUNT) || commandCode.equals(GRP_INFO)) {
            data = processGroupInfo();
		}

		if (commandCode.equals(ON_OFF)) {
            processOnOff();
		}

		if (commandCode.equals(DIMMED)) {
			//переключение диммированного канала
            ChannelsDataSource channelDS = DataSourceManager.getInstance().getChannelsDS(context);
			RequestTask requestTask = new RequestTask(context);
            ChannelElement current = Values.getChannel();

            if (current.getType() !=  NooLiteDefs.CHANNEL_TYPE_SCENARIO) {
                int newValue = getNextBrightnessLevel(current.getState());
                current.setPreviousState(current.getState());
                current.setState(newValue);
                String url = UrlUtils.getFmBarUrl(current.getId() - 1, 6, 3, newValue);
                channelDS.update(current);
                requestTask.execute(url);
            }
		}

		//перелив цвета диодов
		if (commandCode.equals(START_OVERFLOW)) {
			RequestTask requestTask = new RequestTask(context);
            ChannelElement current = Values.getChannel();

			if (current.getType() == NooLiteDefs.CHANNEL_TYPE_LED) {
                String url = UrlUtils.getCmdUrl(current.getId() - 1, 16);
				requestTask.execute(url);
			}
		}

		//окончание перелива цвета диодов
		if (commandCode.equals(STOP_OVERFLOW)) {
			RequestTask requestTask = new RequestTask(context);
            ChannelElement current = Values.getChannel();

			if (current.getType() == NooLiteDefs.CHANNEL_TYPE_LED) {
                String url = UrlUtils.getCmdUrl(current.getId() - 1, 10);
				requestTask.execute(url);
			}
		}
		if (data != null) {
            //передача собранной информации в словаре на часы
            PebbleKit.sendDataToPebble(context, univId, data);
        }
	}

	//вычисление следующего уровня яркости
	private int getNextBrightnessLevel(int state) {
		int current = 0;
		if (state < 15 || state == 100)
			current = 15;
		else if (state < 25)
			current = 25;
		else if (state < 40)
			current = 40;
		else
			current = 100;

		return current;
	}

	private PebbleDictionary processGroupInfo() {
        //передача информации о следующей группе
        GroupDataSource groupDS = DataSourceManager.getInstance().getGroupDS(context);
        GroupElement gr = Values.nextGroup(groupDS.getAll());

        if (gr == null) {
            return null;
        }
        gr = groupDS.get(gr.getId());
        Values.setChannelsToView(gr);
        ChannelElement ch = Values.nextChannel();

        PebbleDictionary data = new PebbleDictionary();
        //передача информации об имени группы
        data.addString(GROUPE_NAME_KEY, gr.getName());
        //передача информации о первом канале группы
        data.addString(CHANNEL_NAME_KEY, ch.getName());
        data.addInt32(CHANNEL_TYPE_KEY, typeConverter(ch.getType()));

        if (ch.getType() == NooLiteDefs.CHANNEL_TYPE_SENSOR) {
            String sensorValue = SensorUtils.getSensorValue(ch.getId());
            data.addString(SENSOR_KEY, ch.getName() + ": " + sensorValue);
        }
        return data;
    }

    private PebbleDictionary processChannelInfo() {
        PebbleDictionary data = new PebbleDictionary();
        //команда получения информации о следующем канале в группе
        ChannelElement ch = Values.nextChannel();
        //проверка  того, показывать канал или датчик
        if (ch.getType() == NooLiteDefs.CHANNEL_TYPE_SENSOR) {
            //добавление информации о датчике
            data.addInt32(CHANNEL_TYPE_KEY, ch.getType());
            //список текущих значений показаний датчиков
            String sensorValue = SensorUtils.getSensorValue(ch.getId());
            data.addString(SENSOR_KEY, ch.getName() + ": " + sensorValue);

        } else {
            //добавление информации о имени группы
            data.addString(GROUPE_NAME_KEY,	Values.getGroup().getName());
            //добавление информации о имени канала
            data.addString(CHANNEL_NAME_KEY, ch.getName());
            //довавление информации о типе канала
            data.addInt32(CHANNEL_TYPE_KEY, typeConverter(ch.getType()));
        }
        return data;
    }

    private void processOnOff() {
        //включение-выключение канала
        ChannelElement currentCh = Values.getChannel();

        if (currentCh == null) {
            return;
        }
        ChannelsDataSource channelDS = DataSourceManager.getInstance().getChannelsDS(context);
        RequestTask requestTask = new RequestTask(context);

        String url;

        //обработка каманд для различных типов каналов
        if (currentCh.getType() != NooLiteDefs.CHANNEL_TYPE_SCENARIO) {
            if (currentCh.getState() == 0) {
                url = UrlUtils.getCmdUrl(currentCh.getId() - 1, 2);

                if (currentCh.getType() == NooLiteDefs.CHANNEL_TYPE_DIMMED) {
                    currentCh.setState(currentCh.getPreviousState());
                } else {
                    currentCh.setState(255);
                }
                channelDS.update(currentCh);

            } else if (currentCh.getType() == NooLiteDefs.CHANNEL_TYPE_OPEN_CLOSE) {
                url = UrlUtils.getBarUrl(currentCh.getId() - 1, 8, 0);

            } else {
                url = UrlUtils.getCmdUrl(currentCh.getId() - 1, 0);

                if (currentCh.getType() == NooLiteDefs.CHANNEL_TYPE_DIMMED) {
                    currentCh.setPreviousState(currentCh.getState());
                }
                currentCh.setState(0);
                channelDS.update(currentCh);
            }
        } else {
            url = UrlUtils.getCmdUrl(currentCh.getId() - 1, 7);
            currentCh.setState(100);
            currentCh.setPreviousState(0);
            channelDS.update(currentCh);
        }
        requestTask.execute(url);

    }

    private int typeConverter(int channelType) {
        if (channelType == NooLiteDefs.CHANNEL_TYPE_SCENARIO ||
                channelType == NooLiteDefs.CHANNEL_TYPE_OPEN_CLOSE) {
            return NooLiteDefs.CHANNEL_TYPE_PURE;
        }
        return channelType;
    }

}
