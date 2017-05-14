package com.noolite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.Toast;

import com.noolite.adapters.CustomListAdapter;
import com.noolite.asynctask.DownloadInterface;
import com.noolite.asynctask.DownloadXMLTask;
import com.noolite.channels.ChannelElement;
import com.noolite.dbchannels.DBManagerChannel;
import com.noolite.dbgroup.DBManagerGroup;
import com.noolite.groups.GroupElement;
import com.noolite.parsers.BinParser;
import com.noolite.parsers.XMLParser;
import com.noolite.pebble.PebbleManager;
import com.noolite.settings.SettingsValues;

//начальное Activity приложения
public class MainActivity extends Activity implements OnItemClickListener,
		OnClickListener {

	private ArrayList<GroupElement> groups = new ArrayList<GroupElement>(); //список отображаемых групп

	private DBManagerGroup dbManager; //обьект-синглтон для работы с бд
	private Intent intent;

	//элементы UI приложения
	private ActionBar actionBar;
	private View view;
	private ImageButton settingsBtn, timerButton;
	private ListView groupListView;

	//объект, получающий доступ к настройкам приложения
	private static SharedPreferences sharedPref;

	//объект, работающий с pebble и ID приложения для pebble
	private PebbleManager pm;
	private final static UUID APP_UUID = UUID
			.fromString("1151b807-682b-46c2-a945-1707516fce6f");


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//регистрация обработчика получения сообщений с pebble
		if(pm==null){
			pm = new PebbleManager(getApplicationContext());
// Compilation Error
//			pm.registerReceivedDataHandler();
		}

        Intent intent = new Intent("myAction");
		sendBroadcast(intent);

		//создание папки для хранения данных приложения, если она не существует
		File myDirectory = new File(Environment.getExternalStorageDirectory(), "nooLite");
		if(!myDirectory.exists()) {                                 
			  myDirectory.mkdirs();
		}

		//получение системных настроек приложения
		sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		SettingsValues.setSound(sharedPref.getBoolean(getString(R.string.play_sound), true));
		SettingsValues.setIP(sharedPref.getString("IP", "192.168.0.168"));
		SettingsValues.setPassword(sharedPref.getString("password", ""));
		SettingsValues.setUsername(sharedPref.getString("username", ""));
		SettingsValues.setAuth(sharedPref.getBoolean("auth", true));
		SettingsValues.setDownloads(sharedPref.getInt("downloads", 0));
		SettingsValues.setDemo(sharedPref.getBoolean("demo", true));
		SettingsValues.setUseWatches(sharedPref.getBoolean("watches", true));
		boolean isJustStarted = sharedPref.getBoolean("dialogShow", true);
		
		groupListView = (ListView) findViewById(R.id.lvGroups);

		//получение записей из БД
		dbManager = DBManagerGroup.getInstance(this);
		dbManager.connect(this);
		try {
			groups = dbManager.getAll();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//проверка на необходимость показа демо-версии
		//при необходимости БД инициализируется значениями для демо-режима
		if(SettingsValues.getDemo()){
			if(isJustStarted){
				showWarningDialog();
				SharedPreferences.Editor edit = MainActivity
						.getSharedPref().edit();
				edit.putBoolean("dialogShow", false);
				edit.commit();
			}

			try {
				groups = DBManagerGroup.getAll();

                if (groups == null || groups.size() == 0) {
                    initDB();
                }
                groups = DBManagerGroup.getAll();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
			edit.putBoolean("demo", false);
			edit.commit();
			SettingsValues.setDemo(false);
		}		

		//связь списка и его адаптера
		final CustomListAdapter customAdapter = new CustomListAdapter(this,
				groups);
		groupListView.setAdapter(customAdapter);
		groupListView.setOnItemClickListener(this);

		//связь с action bar и его настройка
		LayoutInflater vi = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = vi.inflate(R.layout.action_bar_main_activity, null);
		actionBar = getActionBar();
		actionBar.setCustomView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		//связь с кнопками action bar
		settingsBtn = (ImageButton) view.findViewById(R.id.settingsBtn);
		//по нажатию - переход в настройки
		settingsBtn.setOnClickListener(this);
/*
        timerButton = (ImageButton) view.findViewById(R.id.timersBtn);
		//по нажатию - открытие окна с таймерами

        timerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TimersActivity.class);
                startActivity(intent);
                finish();
            }
        });
*/

		//скачивание xml
		DownloadXMLTask dt = new DownloadXMLTask(new DownloadInterface() {
			@Override
			public void callBackDownload() {
				
			}
		});

		dt.execute("http://" + SettingsValues.getIP() + "/sens.xml");
		
		InputStream in = null;
		try {
			in = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/nooLite/sens.xml"));
			XMLParser.parse(getApplicationContext(), in);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


	}

	//диалог-предупреждение о демо-режиме
	private void showWarningDialog() {
		final AlertDialog.Builder adb = new AlertDialog.Builder(this);

		view = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.dialog, null);

		adb.setView(view);

		TextView msg = (TextView) view.findViewById(R.id.message);
        msg.setText("Программа находится в демонстрационном режиме! "+
                "для полноценной работы программы необходимо приобрести Ethernet-шлюз PR1132 и выполнить синхронизацию.");

        Button btnOk = (Button) view.findViewById(R.id.okDialogButton);
		final Dialog alertDialog = adb.create();
		alertDialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);

		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		alertDialog.show();
		
	}

	//инициализация БД демо-версии
	private void initDB() {
		DBManagerGroup dmGroup = DBManagerGroup.getInstance(getApplicationContext());
		dmGroup.connect(getApplicationContext());
		
		ArrayList<Integer> sensors = new ArrayList<Integer>();
		for(int i=0; i<4; i++){
			sensors.add(0);
		}
		ArrayList<Integer> channels = new ArrayList<Integer>();
		channels.add(1);
		channels.add(2);
		channels.add(3);
		DBManagerGroup.add(new GroupElement(0, "Зал demo", channels, sensors, true));
		channels.clear();
		channels.add(4);
		channels.add(5);
		channels.add(6);
		DBManagerGroup.add(new GroupElement(1, "Спальня demo", channels, sensors, true));
		channels.clear();
		channels.add(7);
		DBManagerGroup.add(new GroupElement(2, "Прихожая demo", channels, sensors, true));
		
		DBManagerChannel dmChannel = DBManagerChannel.getInstance(getApplicationContext());
		dmChannel.connect(getApplicationContext());
		DBManagerChannel.add(new ChannelElement(0, "Люстра", 0, 0, 0));
		DBManagerChannel.add(new ChannelElement(1, "Бра", 1, 0, 0));
		DBManagerChannel.add(new ChannelElement(2, "Торшер", 0, 0, 0));
		DBManagerChannel.add(new ChannelElement(3, "Люстра", 0, 0, 0));
		DBManagerChannel.add(new ChannelElement(4, "Подсветка", 3, 0, 0));
		DBManagerChannel.add(new ChannelElement(5, "Вечер", 2, 0, 0));
		DBManagerChannel.add(new ChannelElement(6, "Общее освещение", 0, 0, 0));
	}

	//по нажатию на элемент списка происходит передача данных в Activity каналов
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//создание структур данных с информацией о группе
		ArrayList<Integer> channelsToView = new ArrayList<Integer>();
		for (Integer i : groups.get(position).getChannels()) {
			channelsToView.add(i);
		}
		ArrayList<Integer> sensorsToView = new ArrayList<Integer>();
		
		for (Integer i: groups.get(position).getSensors()){
			sensorsToView.add(i);
		}
		//передача информации о группе в другое Activity
		intent = new Intent(this, ChannelViewActivity.class);
		intent.putIntegerArrayListExtra("channels", channelsToView);
		intent.putIntegerArrayListExtra("sensors", sensorsToView);
		intent.putExtra("title", groups.get(position).getName());
		startActivity(intent);
		finish();
	}

	//переход в Activity таймеров
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settingsBtn:			
			intent = new Intent(this, SettingsMenu.class);
			startActivity(intent);
			finish();
			break;
		}
	}
	
	@Override
	protected void onPause() {
        //Urix: added due to exception
        super.onPause();
		super.onDestroy();
		SharedPreferences.Editor edit = MainActivity
				.getSharedPref().edit();
		edit.putBoolean("dialogShow", true);
		edit.commit();
	}
	
	public static SharedPreferences getSharedPref(){
		return sharedPref;
	}	
}