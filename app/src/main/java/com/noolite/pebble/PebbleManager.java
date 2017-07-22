package com.noolite.pebble;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import receiver.DataReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.noolite.asynctask.RequestInterface;
import com.noolite.asynctask.RequestTask;
import com.noolite.channels.ChannelElement;
import com.noolite.db.ds.ChannelsDataSource;
import com.noolite.db.ds.DataSourceManager;
import com.noolite.db.ds.GroupDataSource;
import com.noolite.groups.GroupElement;
import com.noolite.groups.SensorElement;
import com.noolite.settings.SettingsValues;

public class PebbleManager extends BroadcastReceiver {

	// UUID приложения
	private final static UUID APP_UUID = UUID.fromString("1151b807-682b-46c2-a945-1707516fce6f");

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

	private GroupDataSource groupDS;
	private ChannelsDataSource channelDS;

	public PebbleManager(Context context) {
		this.context = context;
		groupDS = DataSourceManager.getInstance().getGroupDS(context);
		channelDS = DataSourceManager.getInstance().getChannelsDS(context);
	}

	public boolean isConnected() {
		return PebbleKit.isWatchConnected(context);
	}

	//отправка сообщения, генерация ответа в зависимости от типа команды commandCode
	public void sendMessage(String commandCode) throws ParseException,
			UnsupportedEncodingException {

		PebbleDictionary data = new PebbleDictionary();

		ArrayList<GroupElement> groups = new ArrayList<GroupElement>();
		ArrayList<ChannelElement> channels = new ArrayList<ChannelElement>();

		//получение информации о типе команды
		if (commandCode.equals(CHN_INFO)) {

			//команда получения информации о следующем канале в группе
//			DBManagerGroup dbGroup = DBManagerGroup.getInstance(context);
//			dbGroup.connect(context);


			groups = groupDS.getAll();
			//получение комеров каналов из текущей группы
			ArrayList<Integer> indexesOfChannelsInGroup = new ArrayList<Integer>();
			for (Integer i : groups.get(Values.indexOfCurrentGroup - 1).getChannels()) {
				indexesOfChannelsInGroup.add(i);
			}

			Values.totalCountOfChannels = groups.get(Values.indexOfCurrentGroup - 1).getChannels().size();

			List<Integer> sensorsToView = new ArrayList<Integer>();
            List<SensorElement> sensors = groups.get(Values.indexOfCurrentGroup - 1).getSensorElements();
			for (int i = 0; i < sensors.size(); i++) {
				if (sensors.get(i).getId() != 0) {
					sensorsToView.add(sensors.get(i).getId());
				}
			}
			Values.totalCountOfDetectors = sensorsToView.size();

			//обработка текущего индекса
			Values.indexOfCurrentChannel = (Values.indexOfCurrentChannel + 1)
					% (Values.totalCountOfChannels + Values.totalCountOfDetectors);
			if (Values.indexOfCurrentChannel == 0)
				Values.indexOfCurrentChannel = Values.totalCountOfChannels
						+ Values.totalCountOfDetectors;


			//проверка  того, показывать канал или датчик
			if (Values.indexOfCurrentChannel > Values.totalCountOfChannels) {
				int numberOfCurrentSensor = Values.indexOfCurrentChannel
						- Values.totalCountOfChannels - 1;
				ArrayList<String> valuesOfSensors = SettingsValues.sensorValues;
				//добавление информации о датчике
				data.addInt32(CHANNEL_TYPE_KEY, DETECTOR_TYPE_KEY);
				data.addString(
						SENSOR_KEY,
						valuesOfSensors.get(sensorsToView
								.get(numberOfCurrentSensor) * 3)
								+ "\u00B0C "
								+ valuesOfSensors.get(sensorsToView
										.get(numberOfCurrentSensor) * 3 + 1)
								+ "% RH");

			} else {

				//передача информации о канале
//				DBManagerChannel dbChannels = DBManagerChannel
//						.getInstance(context);
//				dbChannels.connect(context);

				channels = channelDS.getAll();

				//добавление информации о имени группы
				data.addString(GROUPE_NAME_KEY,	groups.get(Values.indexOfCurrentGroup - 1).getName());
				//добавление информации о имени канала
				data.addString(
				        CHANNEL_NAME_KEY,
						channels.get(
								indexesOfChannelsInGroup.get(Values.indexOfCurrentChannel - 1) - 1)
								.getName());

				//довавление информации о типе канала
				switch (channels.get(
						indexesOfChannelsInGroup
								.get(Values.indexOfCurrentChannel - 1) - 1)
						.getType()) {
				case 0:
					data.addInt32(CHANNEL_TYPE_KEY, 0);
					break;
				case 1:
					data.addInt32(CHANNEL_TYPE_KEY, 1);
					break;
				case 2:
					data.addInt32(CHANNEL_TYPE_KEY, 0);
					break;
				case 3:
					data.addInt32(CHANNEL_TYPE_KEY, 3);
					break;
				}
			}
		}

		if (commandCode.equals(GRP_COUNT) || commandCode.equals(GRP_INFO)) {
			//передача информации о следующей группе

			//получение данных из БД
//			DBManagerGroup dbGroup = DBManagerGroup.getInstance(context);
//			dbGroup.connect(context);

			groups = groupDS.getAll();

			//обработка текущих индексов
			Values.totalCountOfGroups = groups.size();
			Values.indexOfCurrentGroup = 1;

			ArrayList<Integer> indexesOfChannelsInGroup = new ArrayList<Integer>();
			for (Integer i : groups.get(0).getChannels()) {
				indexesOfChannelsInGroup.add(i);
			}

			Values.totalCountOfChannels = indexesOfChannelsInGroup.size();
			Values.indexOfCurrentChannel = 1;

			List<SensorElement> sensors = groups.get(0).getSensorElements();

			Values.totalCountOfDetectors = sensors.size();

//			DBManagerChannel dbChannels = DBManagerChannel.getInstance(context);
//			dbChannels.connect(context);

			channels = channelDS.getAll();

			//передача информации о имени группы
			data.addString(GROUPE_NAME_KEY, groups.get(0).getName());

			//передача информации о первом канале группы
			data.addString(CHANNEL_NAME_KEY,
					channels.get(indexesOfChannelsInGroup.get(0) - 1).getName());
			switch (channels.get(indexesOfChannelsInGroup.get(0) - 1).getType()) {
			case 0:
				data.addInt32(CHANNEL_TYPE_KEY, 0);
				break;
			case 1:
				data.addInt32(CHANNEL_TYPE_KEY, 1);
				break;
			case 2:
				data.addInt32(CHANNEL_TYPE_KEY, 0);
				break;
			case 3:
				data.addInt32(CHANNEL_TYPE_KEY, 3);
				break;

			}
		}

		if (commandCode.equals(ON_OFF)) {
			//включение-выключение канала

			//создание Asynctask для передачи команды
			RequestTask requestTask = new RequestTask(context);

//			DBManagerGroup dmGroup = DBManagerGroup.getInstance(context);
//			dmGroup.connect(context);

			groups = groupDS.getAll();
			//получение информации о текущем канале
			GroupElement currentGroup = groups
					.get(Values.indexOfCurrentGroup - 1);
			if (Values.indexOfCurrentChannel <= Values.totalCountOfChannels) {

//				DBManagerChannel dmChannel = DBManagerChannel
//						.getInstance(context);
//				dmChannel.connect(context);

				channels = channelDS.getAll();
				ChannelElement current = channels
						.get(currentGroup.getChannels().get(
								Values.indexOfCurrentChannel - 1) - 1);

				String url = new String();

				//обработка каманд для различных типов каналов
				if (current.getType() != 2) {
					if (current.getState() == 0) {
						url = "http://" + SettingsValues.getIP()
								+ "/api.htm?ch=" + (current.getId() - 1)
								+ "&cmd=2";
						if (current.getType() == 1) {
							current.setState(current.getPreviousState());
						} else
							current.setState(255);
//						DBManagerChannel.update(current);
						channelDS.update(current);

					} else {
						url = "http://" + SettingsValues.getIP()
								+ "/api.htm?ch=" + (current.getId() - 1)
								+ "&cmd=0";

						if (current.getType() == 1) {
							current.setPreviousState(current.getState());
						}
						current.setState(0);

//						DBManagerChannel.update(current);
						channelDS.update(current);
					}
				} else {

					url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
							+ (current.getId() - 1) + "&cmd=7";
					current.setState(100);
					current.setPreviousState(0);
//					DBManagerChannel.update(current);
					channelDS.update(current);

				}

				requestTask.execute(url);
			}
		}

		if (commandCode.equals(DIMMED)) {
			//переключение диммированного канала

			RequestTask requestTask = new RequestTask(context);

//			DBManagerGroup dmGroup = DBManagerGroup.getInstance(context);
//			dmGroup.connect(context);

			//получение информации о канале
			groups = groupDS.getAll();
			GroupElement currentGroup = groups
					.get(Values.indexOfCurrentGroup - 1);
			if (Values.indexOfCurrentChannel <= Values.totalCountOfChannels) {

//				DBManagerChannel dmChannel = DBManagerChannel
//						.getInstance(context);
//				dmChannel.connect(context);

				channels = channelDS.getAll();
				ChannelElement current = channels
						.get(currentGroup.getChannels().get(
								Values.indexOfCurrentChannel - 1) - 1);

				String url = new String();

				if (current.getType() != 2) {
					int newValue = getNextBrightnessLevel(current.getState());
					current.setPreviousState(current.getState());
					current.setState(newValue);

					url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
							+ (current.getId() - 1) + "&cmd=6&fm=3&br="
							+ newValue;
//					DBManagerChannel.update(current);
					channelDS.update(current);

					requestTask.execute(url);
				}
			}

		}

		//перелив цвета диодов
		if (commandCode.equals(START_OVERFLOW)) {
			RequestTask requestTask = new RequestTask(context);
			groups = groupDS.getAll();
			GroupElement currentGroup = groups
					.get(Values.indexOfCurrentGroup - 1);

			channels = channelDS.getAll();
			ChannelElement current = channels.get(currentGroup.getChannels()
					.get(Values.indexOfCurrentChannel - 1) - 1);

			if (current.getType() == 3) {
                String url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
						+ (current.getId() - 1) + "&cmd=16";
				requestTask.execute(url);
			}
		}

		//окончание перелива цвета диодов
		if (commandCode.equals(STOP_OVERFLOW)) {
			RequestTask requestTask = new RequestTask(context);
			groups = groupDS.getAll();
			GroupElement currentGroup = groups
					.get(Values.indexOfCurrentGroup - 1);

			channels = channelDS.getAll();
			ChannelElement current = channels.get(currentGroup.getChannels()
					.get(Values.indexOfCurrentChannel - 1) - 1);

			if (current.getType() == 3) {
				String url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
						+ (current.getId() - 1) + "&cmd=10";
				requestTask.execute(url);
			}

		}
		//передача собранной информации в словаре на часы
		PebbleKit.sendDataToPebble(context, APP_UUID, data);
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

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Values.count == 0) {
			PebbleKit.registerReceivedDataHandler(context, new DataReceiver());
			Values.count++;
		}
	}


}
