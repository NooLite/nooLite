package com.noolite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.noolite.adapters.CustomListAdapter;
import com.noolite.asynctask.DownloadXMLTask;
import com.noolite.domain.ChannelElement;
import com.noolite.db.ds.BasicDataSource;
import com.noolite.db.ds.ChannelsDataSource;
import com.noolite.db.ds.DataSourceManager;
import com.noolite.db.ds.GroupDataSource;
import com.noolite.domain.GroupElement;
import com.noolite.domain.SensorElement;
import com.noolite.pebble.ReceiverFactory;
import com.noolite.util.UrlUtils;

//начальное Activity приложения
public class MainActivity extends Activity implements OnItemClickListener,
		OnClickListener {

	private ArrayList<GroupElement> groups = new ArrayList<GroupElement>(); //список отображаемых групп

	private ImageButton settingsBtn;
	private ListView groupListView;

	//объект, получающий доступ к настройкам приложения
	private static SharedPreferences sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
//		SettingsValues.setIP(sharedPref.getString("IP", "192.168.0.168"));
		SettingsValues.setIP(sharedPref.getString("IP", "192.168.0.168:8080"));
		SettingsValues.setPassword(sharedPref.getString("password", ""));
		SettingsValues.setUsername(sharedPref.getString("username", ""));
		SettingsValues.setAuth(sharedPref.getBoolean("auth", true));
		SettingsValues.setDownloads(sharedPref.getInt("downloads", 0));
		SettingsValues.setDemo(sharedPref.getBoolean("demo", true));
		SettingsValues.setUseWatches(sharedPref.getBoolean("watches", true));
		boolean isJustStarted = sharedPref.getBoolean("dialogShow", true);
		
		groupListView = (ListView) findViewById(R.id.lvGroups);
		GroupDataSource groupDS = DataSourceManager.getInstance().getGroupDS(getApplicationContext());
        groups = groupDS.getAll();

        //проверка на необходимость показа демо-версии
		//при необходимости БД инициализируется значениями для демо-режима
		if (SettingsValues.getDemo()) {
			if(isJustStarted){
				showWarningDialog();
				SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
				edit.putBoolean("dialogShow", false);
				edit.commit();
			}
            groups = groupDS.getAll();

            if (groups == null || groups.size() == 0) {
                createDemoData();
                groups = groupDS.getAll();
            }
		} else {
			SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
			edit.putBoolean(NooLiteDefs.FLAG_DEMO, false);
			edit.commit();
			SettingsValues.setDemo(false);

            DownloadXMLTask dt = new DownloadXMLTask();
            dt.execute(UrlUtils.getSensorUrl());
		}		

		//связь списка и его адаптера
		final CustomListAdapter customAdapter = new CustomListAdapter(this,	groups);
		groupListView.setAdapter(customAdapter);
		groupListView.setOnItemClickListener(this);

		//связь с action bar и его настройка
		LayoutInflater vi = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = vi.inflate(R.layout.action_bar_main_activity, null);
		ActionBar actionBar = getActionBar();
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
        ReceiverFactory.getInstance().createAndRegisterReceiver(getApplicationContext());
	}

	//диалог-предупреждение о демо-режиме
	private void showWarningDialog() {
		final AlertDialog.Builder adb = new AlertDialog.Builder(this);
		View view = getLayoutInflater().inflate(R.layout.dialog, null);
		adb.setView(view);

		TextView msg = (TextView) view.findViewById(R.id.message);
        msg.setText("Программа находится в демонстрационном режиме! "+
                "для полноценной работы программы необходимо приобрести Ethernet-шлюз PR1132 и выполнить синхронизацию.");

        Button btnOk = (Button) view.findViewById(R.id.okDialogButton);
		final Dialog alertDialog = adb.create();
		alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		alertDialog.show();
		
	}

	//инициализация БД демо-версии
	private void createDemoData() {
        GroupDataSource groupDS = DataSourceManager.getInstance().getGroupDS(getApplicationContext());
        ChannelsDataSource channelDS = DataSourceManager.getInstance().getChannelsDS(getApplicationContext());

        ChannelElement channelElement1 = new ChannelElement(1, "Люстра", 0, 0, 0);
        ChannelElement channelElement2 = new ChannelElement(2, "Бра", 1, 0, 0);
        ChannelElement channelElement3 = new ChannelElement(3, "Торшер", 0, 0, 0);
        ChannelElement channelElement4 = new ChannelElement(4, "Люстра", 0, 0, 0);
        ChannelElement channelElement5 = new ChannelElement(5, "Подсветка", 3, 0, 0);
        ChannelElement channelElement6 = new ChannelElement(6, "Вечер", 2, 0, 0);
        ChannelElement channelElement7 = new ChannelElement(7, "Общее освещение", 0, 0, 0);
        channelDS.add(channelElement1);
        channelDS.add(channelElement2);
        channelDS.add(channelElement3);
        channelDS.add(channelElement4);
        channelDS.add(channelElement5);
        channelDS.add(channelElement6);
        channelDS.add(channelElement7);

        List<SensorElement> sensors = new ArrayList<SensorElement>();
        for (int i = 1; i <= 4; i++){
            sensors.add(new SensorElement("Датчик" + i, i));
        }


        ArrayList<Integer> channels = new ArrayList<Integer>();
        channels.add(1);
        channels.add(2);
        channels.add(3);
        GroupElement groupElement = new GroupElement(1, "Зал demo", true);
        groupElement.setChannels(channels);
        groupElement.setSensorElements(sensors);
        groupDS.add(groupElement);
        channelDS.boundChannel(groupElement, channelElement1);
        channelDS.boundChannel(groupElement, channelElement2);
        channelDS.boundChannel(groupElement, channelElement3);

        channels.clear();
        channels.add(4);
        channels.add(5);
        channels.add(6);
        groupElement = new GroupElement(2, "Спальня demo", true);
        groupElement.setChannels(channels);
        groupElement.setSensorElements(sensors);
        groupDS.add(groupElement);
        channelDS.boundChannel(groupElement, channelElement4);
        channelDS.boundChannel(groupElement, channelElement5);
        channelDS.boundChannel(groupElement, channelElement6);

        channels.clear();
        channels.add(7);
        groupElement = new GroupElement(3, "Прихожая demo", true);
        groupElement.setChannels(channels);
        groupElement.setSensorElements(sensors);
        groupDS.add(groupElement);
        channelDS.boundChannel(groupElement, channelElement7);
	}

	//по нажатию на элемент списка происходит передача данных в Activity каналов
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		//передача информации о группе в другое Activity
        Intent intent = new Intent(this, ChannelViewActivity.class);
		intent.putExtra(BasicDataSource.GROUP_ID, id);
		startActivity(intent);
		finish();
	}

	//переход в Activity таймеров
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settingsBtn:
            Intent intent = new Intent(this, SettingsMenu.class);
			startActivity(intent);
			finish();
			break;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
        super.onPause();
		SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
		edit.putBoolean("dialogShow", true);
		edit.commit();

//        if (ReceiverFactory.getInstance().getReceiver() != null) {
//            unregisterReceiver(ReceiverFactory.getInstance().getReceiver());
//        }



	}
	
	public static SharedPreferences getSharedPref(){
		return sharedPref;
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataSourceManager.getInstance().close();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}