package com.noolite.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.noolite.R;
import com.noolite.asynctask.RequestInterface;
import com.noolite.asynctask.RequestTask;
import com.noolite.channels.ChannelElement;
import com.noolite.dbchannels.DBManagerChannel;
import com.noolite.settings.SettingsValues;

//адаптер для конфигурации UI списка каналов
public class ChannelListAdapter extends BaseAdapter implements
		OnSeekBarChangeListener, OnCheckedChangeListener, OnClickListener {

	private Context context;  //текущий Context приложения
	private ArrayList<ChannelElement> list;  //список каналов, на основе которого строится UI
	private LayoutInflater inflater;  //LayoutInflater для подгрузки View
	private RelativeLayout rel1;
	private static boolean[] checked;
	private SeekBar seekBar;
	private LinearLayout layout;
	private TextView title, sensorTitle, sensorTemperature, sensorHumidity;

	private Button btnStartScenario, btnSave,
			btnLEDStartScenario, btnLEDSave, btnLEDChangeColor;
	private ImageButton onBtn, btnLEDStart;

	public ChannelListAdapter(Context context, ArrayList<ChannelElement> list) {
		this.context = context;
		this.list = list;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		checked = new boolean[list.size()];
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	//инициализация UI для текущего элемента списка (с номером position)
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View customView;

		//подгрузка View, если он не сохранился для данного элемента списка в кэше приложения
		if (convertView == null) {
			customView = inflater.inflate(R.layout.channel_item, null);
		} else {
			customView = convertView;
		}

		ChannelElement newChannel = (ChannelElement) this.getItem(position);
		String channelTitle = newChannel.getName();

		customView.findViewById(R.id.simpleChannel).setVisibility(View.GONE);
		customView.findViewById(R.id.dimmedChannel).setVisibility(View.GONE);
		customView.findViewById(R.id.LEDChannel).setVisibility(View.GONE);
		customView.findViewById(R.id.scenarioChannel).setVisibility(View.GONE);
		customView.findViewById(R.id.openCloseChannel).setVisibility(View.GONE);
//		customView.findViewById(R.id.sensorChannel).setVisibility(View.INVISIBLE);


		//инициализация элемента списка в соответствии с типом канала
		switch (newChannel.getType()) {

		case 0:
			//обычный канал

			customView.findViewById(R.id.simpleChannel).setVisibility(
					View.VISIBLE);
			//отображение названия
			title = (TextView) customView.findViewById(R.id.groupTitle);
			//инициализация кнопки вкл/выкл в соответствии с текущим состоянием канала
			onBtn = (ImageButton) customView
					.findViewById(R.id.simpleChannelSwitch);
			if (newChannel.getState() == 0) {
				onBtn.setImageResource(R.drawable.unselected);
				checked[position] = false;
			} else {
				onBtn.setImageResource(R.drawable.selected);
				checked[position] = true;
			}

			onBtn.setTag(new Integer(position));
			onBtn.setOnClickListener(this);
			title.setText(channelTitle);
			break;
		case 1:
			//канал с диммированием
			customView.findViewById(R.id.dimmedChannel).setVisibility(
					View.VISIBLE);

			layout = (LinearLayout) customView.findViewById(R.id.dimmedChannel);
			//установка названия канала
			title = (TextView) customView.findViewById(R.id.dimmedChannelTitle);

			//установка отображения кнопки включения в соответствии с текущим состоянием
			onBtn = (ImageButton) customView
					.findViewById(R.id.dimmedChannelSwitch);
			onBtn.setTag(new Integer(position));
			onBtn.setOnClickListener(this);

			//инициализация seekBar
			seekBar = (SeekBar) customView
					.findViewById(R.id.dimmedChannelSeekBar);
			seekBar.setOnSeekBarChangeListener(this);
			seekBar.setTag(new Integer(position));
			seekBar.setMax(100);
			seekBar.getProgressDrawable().setColorFilter(0xff00B89C,
					Mode.SRC_IN);

			//отображение элемента списка в соответствии с текущим состоянием канала
			if (newChannel.getState() == 0) {
				onBtn.setImageResource(R.drawable.unselected);
				seekBar.setProgress(newChannel.getState());
				checked[position] = false;
			} else {
				onBtn.setImageResource(R.drawable.selected);
				checked[position] = true;
				seekBar.setProgress(newChannel.getState());
			}
			title.setText(channelTitle);
			break;
		case 2:
			//сценарии

			customView.findViewById(R.id.scenarioChannel).setVisibility(
					View.VISIBLE);
			title = (TextView) customView
					.findViewById(R.id.scenarioChannelTitle);

			//инициализация кнопок запуска и остановки сценария
			btnStartScenario = (Button) customView
					.findViewById(R.id.scenarioStart);
			btnStartScenario.setOnClickListener(this);
			btnStartScenario.setTag(new Integer(position));

			btnSave = (Button) customView.findViewById(R.id.scenarioSave);
			btnSave.setOnClickListener(this);
			btnSave.setTag(new Integer(position));

			//отображение названия канала
			title.setText("Сценарий: " + channelTitle);
			break;
		case 3:
			//канал с диодами

			customView.findViewById(R.id.LEDChannel)
					.setVisibility(View.VISIBLE);
			title = (TextView) customView.findViewById(R.id.LEDChannelTitle);

			//кнопка включения-выключения канала
			btnLEDStart = (ImageButton) customView
					.findViewById(R.id.LEDChannelSwitchButton);
			btnLEDStart.setOnClickListener(this);
			btnLEDStart.setTag(new Integer(position));

			//seekBar контролирующий яркость подсветки
			seekBar = (SeekBar) customView.findViewById(R.id.LEDSeekBar);
			seekBar.setOnSeekBarChangeListener(this);
			seekBar.setTag(new Integer(position));
			seekBar.setMax(100);
			seekBar.getProgressDrawable().setColorFilter(0xff00B89C,
					Mode.SRC_IN);

			//кнопки запуска и сохранения сценария
			btnLEDStartScenario = (Button) customView
					.findViewById(R.id.ledChannelStart);
			btnLEDStartScenario.setOnClickListener(this);
			btnLEDStartScenario.setTag(new Integer(position));

			btnLEDSave = (Button) customView.findViewById(R.id.LEDChannelSave);
			btnLEDSave.setOnClickListener(this);
			btnLEDSave.setTag(new Integer(position));

			//кнопка перелива диодов
			btnLEDChangeColor = (Button) customView
					.findViewById(R.id.LEDChannelColor);
			btnLEDChangeColor.setOnClickListener(this);
			btnLEDChangeColor.setTag(new Integer(position));

			//отображение имени канала
			title.setText(channelTitle);

			//инициализация элементов UI в соответствии с текущим состоянием канала
			if (newChannel.getState() == 0) {
				btnLEDStart.setImageResource(R.drawable.unselected);
				seekBar.setProgress(0);
				checked[position] = false;
			} else {
				btnLEDStart.setImageResource(R.drawable.selected);
				checked[position] = true;
				seekBar.setProgress(newChannel.getState());
			}
			break;
		case 4:
            // Open/Close
            customView.findViewById(R.id.openCloseChannel).setVisibility(View.VISIBLE);
            TextView title = (TextView) customView.findViewById(R.id.openCloseTitle);
            title.setText(channelTitle);
            Button btnSave = (Button) customView.findViewById(R.id.openCloseSave);
            btnSave.setOnClickListener(this);
            btnSave.setTag(new Integer(position));
            break;

		case 44:
/*
			//датчики

			//список текущих значений показаний датчиков
			ArrayList<String> values = SettingsValues.sensorValues;

			customView.findViewById(R.id.sensorChannel).setVisibility(
					View.VISIBLE);

			//название датчика
			sensorTitle = (TextView) customView.findViewById(R.id.sensorTitle);
			sensorTitle.setText(channelTitle);

			//температура датчика
			sensorTemperature = (TextView) customView
					.findViewById(R.id.sensorTemperature);
			try{
				sensorTemperature.setText(values.get(newChannel.getId() * 3)
					+ " \u00B0C");
			}catch(Exception ex){
				sensorTemperature.setText("- \u00B0C");
			}

			//влажность датчика
			sensorHumidity = (TextView) customView
					.findViewById(R.id.sensorHumidity);
			try{
			sensorHumidity.setText(values.get(newChannel.getId() * 3 + 1)
					+ " % RH");
			}catch(Exception ex){
				sensorHumidity.setText("- % RH");
			}

			//информация о типе ошибки
			String errorType = new String();
			try{
				errorType = values.get(newChannel.getId() * 3 + 2);
			}catch(Exception ex){
				errorType = "2";
			}

			//отображение типа возникшей ошибки
			TextView warning = (TextView) customView.findViewById(R.id.warning);

			//в случае отсутствия ошибок строка-пояснение убирается с элемента списка
			if(errorType.equals("0"))
				warning.setVisibility(View.GONE);

			if(errorType.equals("1")) {
				warning.setVisibility(View.VISIBLE);
				warning.setText("Датчик не привязан");
			}

			if(errorType.equals("2")) {
				warning.setVisibility(View.VISIBLE);
				warning.setText("Нет сигнала с датчика");
			}

			if(errorType.equals("3")){
				warning.setVisibility(View.VISIBLE);
				warning.setText("�������� ������� �������");
			}
*/
			break;
		}

		return customView;
	}

	public static boolean[] getChecked() {
		return checked;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	//обработчик событий изменения состояния seekBar
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//получение индекса элемента списка
		int tag = (Integer)seekBar.getTag();
		//получение текущего объекта канала
		ChannelElement current = (ChannelElement) getItem(tag);

        RequestTask requestTask = new RequestTask(new RequestInterface() {
			@Override
			public void callBack() {
			}
		});
		String url = new String();

		//строка запроса на шлюз
		url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
				+ (current.getId() - 1) + "&cmd=6&fm=3&br="
				+ seekBar.getProgress();

		//изменение состояния UI
		LinearLayout ll = (LinearLayout) ((LinearLayout) seekBar.getParent())
				.getParent();
		switch (ll.getId()) {
		case R.id.dimmedChannel:
			ImageButton btn = (ImageButton) ll
					.findViewById(R.id.dimmedChannelSwitch);
			if (seekBar.getProgress() == 0) {
				btn.setImageResource(R.drawable.unselected);
				checked[tag] = false;
			} else {
				btn.setImageResource(R.drawable.selected);
				checked[tag] = true;
			}
			break;
		case R.id.LEDChannel:
			ImageButton btn1 = (ImageButton) ll
					.findViewById(R.id.LEDChannelSwitchButton);
			if (seekBar.getProgress() == 0) {
				btn1.setImageResource(R.drawable.unselected);
				checked[tag] = false;
			} else {
				btn1.setImageResource(R.drawable.selected);
				checked[tag] = true;
			}
			break;
		}

		requestTask.execute(url);
		current.setState(seekBar.getProgress());

		//обновление БД
		DBManagerChannel.update(current);
	}

	//обработка событий включения-выключения канала
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int tag = (Integer)buttonView.getTag();
		//получение текущего объекта канала
		ChannelElement current = (ChannelElement) getItem(tag);
		checked[tag] = buttonView.isChecked();

        RequestTask  requestTask = new RequestTask(new RequestInterface() {
			@Override
			public void callBack() {
			}
		});
		String url = new String();
		if (isChecked) {
			url = "http://192.168.0.168/api.htm?ch=" + (current.getId() - 1)
					+ "&cmd=2";
		} else {
			url = "http://192.168.0.168/api.htm?ch=" + (current.getId() - 1)
					+ "&cmd=0";
		}
		//отправка команды на шлюз
        requestTask.execute(url);
	}

	//обработчики события нажатия на кнопки
	@Override
	public void onClick(View v) {
		//воспроизведение звука
		v.setSoundEffectsEnabled(true);
		if (SettingsValues.getSound()) {
			Uri path = Uri.parse("android.resource://"
					+ context.getPackageName() + "/" + R.raw.sound);
			MediaPlayer mp = MediaPlayer.create(context, path);
			mp.start();
		}

		//получение текущего элемента списка каналов
		int tag = (Integer)v.getTag();
		ChannelElement current = (ChannelElement) getItem(tag);

        RequestTask requestTask = new RequestTask(new RequestInterface() {
			@Override
			public void callBack() {
			}
		});
		String url = new String();

		//по id компонентов UI выбираем необходимую кнопку
		switch (v.getId()) {
		case R.id.scenarioStart:
			//запуск сценария
			url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
					+ (current.getId() - 1) + "&cmd=7";
			current.setState(100);
			current.setPreviousState(0);
			DBManagerChannel.update(current);
			requestTask.execute(url);
			break;
		case R.id.scenarioSave:
			//сохранение сценария
			url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
					+ (current.getId() - 1) + "&cmd=8";
			requestTask.execute(url);
			current.setState(0);
			current.setPreviousState(100);
			DBManagerChannel.update(current);
			break;
		case R.id.LEDChannelSwitchButton:
			//включение-выключение в диодном канале
			checked[tag] = !checked[tag];
			LinearLayout LEDChannel = (LinearLayout) ((LinearLayout) v
					.getParent()).getParent();

			SeekBar seekBar = (SeekBar) LEDChannel
					.findViewById(R.id.LEDSeekBar);
			if (checked[tag]) {
				ImageButton btn = (ImageButton) v;
				btn.setImageResource(R.drawable.selected);
				if (current.getPreviousState() == 0) {
					current.setState(255);
					seekBar.setProgress(255);
				} else {
					current.setState(current.getPreviousState());
					seekBar.setProgress(current.getPreviousState());
				}

				DBManagerChannel.update(current);
			} else {
				url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
						+ (current.getId() - 1) + "&cmd=0";

				ImageButton btn = (ImageButton) v;

				btn.setImageResource(R.drawable.unselected);
				current.setState(0);
				current.setPreviousState(seekBar.getProgress());
				seekBar.setProgress(0);
				DBManagerChannel.update(current);
			}
			url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
					+ (current.getId() - 1) + "&cmd=6&fm=3&br="
					+ seekBar.getProgress();

			requestTask.execute(url);
			break;
		case R.id.LEDChannelColor:
			//переливание цвета
			url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
					+ (current.getId() - 1) + "&cmd=17";
			requestTask.execute(url);
			break;
		case R.id.LEDChannelSave:
			//сохранение сценария диодного канала
			url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
					+ (current.getId() - 1) + "&cmd=10";
			requestTask.execute(url);
			break;
		case R.id.ledChannelStart:
			//запуск сценария диодного канала
			url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
					+ (current.getId() - 1) + "&cmd=16";
			requestTask.execute(url);
			break;
		case R.id.simpleChannelSwitch:
			//включение-выключение в обычном канале
			checked[tag] = !checked[tag];
			if (checked[tag]) {
				url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
						+ (current.getId() - 1) + "&cmd=2";
				ImageButton btn = (ImageButton) v;
				btn.setImageResource(R.drawable.selected);
				current.setState(255);
				DBManagerChannel.update(current);
			} else {
				url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
						+ (current.getId() - 1) + "&cmd=0";
				ImageButton btn = (ImageButton) v;
				btn.setImageResource(R.drawable.unselected);
				current.setState(0);
				DBManagerChannel.update(current);
			}

			requestTask.execute(url);
			break;
		case R.id.dimmedChannelSwitch:
			//включение-выключение в диммированном канале
			checked[tag] = !checked[tag];
			LinearLayout dimmedChannel = (LinearLayout) ((LinearLayout) v
					.getParent()).getParent();
			SeekBar seekBarDimmedChannel = (SeekBar) dimmedChannel
					.findViewById(R.id.dimmedChannelSeekBar);
			if (checked[tag]) {
				ImageButton btn = (ImageButton) v;
				btn.setImageResource(R.drawable.selected);
				if (current.getPreviousState() == 0) {
					current.setState(255);
					seekBarDimmedChannel.setProgress(255);
				} else {
					current.setState(current.getPreviousState());
					seekBarDimmedChannel
							.setProgress(current.getPreviousState());

				}
				url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
						+ (current.getId() - 1) + "&cmd=6&fm=3&br="
						+ seekBarDimmedChannel.getProgress();
				DBManagerChannel.update(current);
			} else {
				url = "http://" + SettingsValues.getIP() + "/api.htm?ch="
						+ (current.getId() - 1) + "&cmd=0";

				ImageButton btn = (ImageButton) v;
				btn.setImageResource(R.drawable.unselected);
				current.setPreviousState(seekBarDimmedChannel.getProgress());
				current.setState(0);
				seekBarDimmedChannel.setProgress(0);
				DBManagerChannel.update(current);
			}
			requestTask.execute(url);
			break;

        case R.id.openCloseSave:
            //http://192.168.0.168:8080/api.htm?ch=0&cmd=8&br=0
            StringBuilder sb = new StringBuilder();
            sb.append("http://").append(SettingsValues.getIP()).append("/api.htm?ch=");
            sb.append(current.getId() - 1).append("&cmd=8");
            requestTask.execute(sb.toString());
		}
	}
}
