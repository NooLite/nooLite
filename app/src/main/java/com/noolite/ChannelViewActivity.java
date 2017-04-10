package com.noolite;

import java.text.ParseException;
import java.util.ArrayList;

import receiver.UpdateReceiver;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.noolite.R;
import com.noolite.adapters.ChannelListAdapter;
import com.noolite.channels.ChannelElement;
import com.noolite.dbchannels.DBManagerChannel;

/**
 * Activity для отображения списка каналов выбранной группы.
 */
public class ChannelViewActivity extends Activity implements OnClickListener {

	private static ArrayList<ChannelElement> allChannels = new ArrayList<ChannelElement>(); //список всех каналов
	private static ListView channelListView;
	private static ChannelListAdapter customAdapter;  //адаптер для списка каналов
	private ImageButton back;
	private static String groupTitle = new String();
	private ActionBar actionBar;
	private View view;
	private static Context context;
    //Intent, который вызывал открытие этого Activity и в котором передавались данные
	private static Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channels);

		context = getApplicationContext();
		//получение вызывавшего Intent
        intent = getIntent();
		//получение передаваемых в Intent данных
		ArrayList<Integer> channels = intent
				.getIntegerArrayListExtra("channels");
//		ArrayList<Integer> sensors = intent.getIntegerArrayListExtra("sensors");
		groupTitle = intent.getStringExtra("title");

		//подключение к БД каналов
		DBManagerChannel dbManager = DBManagerChannel
				.getInstance(getApplicationContext());
		dbManager.connect(getApplicationContext());

		//получение всех каналов шлюза
		try {
			allChannels = DBManagerChannel.getAll();

		} catch (ParseException e) {
			e.printStackTrace();
		}

		//выбор каналов, отображаемых в данной группе
		//от индекса отнимается 1, т.к. в бинарном файле нумерация каналов в характеристике
		// групп идет с 1, 0 в той записи означает отсутствие канала
		ArrayList<ChannelElement> channelsToView = new ArrayList<ChannelElement>();
		for (Integer i : channels) {
			if (i != 0) {
				channelsToView.add(allChannels.get(i - 1));
			}
		}
/*
		//добавление каналов, которые надо отображать
		int position = 1;
		for (Integer i : sensors) {
			if (i != 0) {
				channelsToView.add(new ChannelElement(position-1, "1" + position,
						44, 0, 0));
			}
			position++;
		}
*/
		//инициализация списка каналов и его адаптера
		channelListView = (ListView) findViewById(R.id.lvChannels);
		Log.d("channelsToView", channelsToView.toString());
		customAdapter = new ChannelListAdapter(this, channelsToView);
		channelListView.setAdapter(customAdapter);

		actionBar = getActionBar();
		LayoutInflater vi = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		view = vi.inflate(R.layout.action_bar_channel_activity, null);
		TextView actionBarTitle = (TextView) view
				.findViewById(R.id.actionBarGroupTitle);
		actionBarTitle.setText(groupTitle);

		actionBar.setCustomView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		back = (ImageButton) view.findViewById(R.id.backBtnChannels);
		back.setOnClickListener(this);


// Error: Activity com.noolite.ChannelViewActivity has leaked IntentReceiver receiver.UpdateReceiver@41c80e08 that was originally registered here. Are you missing a call to unregisterReceiver()?
//		this.registerReceiver(new UpdateReceiver(), new IntentFilter("update"));
	}

	//возвращение на главное Activity
	@Override
	public void onClick(View v) {
		SharedPreferences.Editor edit = MainActivity
				.getSharedPref().edit();
		edit.putBoolean("dialogShow", false);
		edit.commit();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		SharedPreferences.Editor edit = MainActivity
				.getSharedPref().edit();
		edit.putBoolean("dialogShow", false);
		edit.commit();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
		super.onBackPressed();
	}

	//обновление списка, которое синхронизирует отображение с текущим состоянием каналов в случае одновременного
	//управления с приложения и pebble
	public static void updateList(){
		ArrayList<Integer> channels = intent
				.getIntegerArrayListExtra("channels");
		
//		ArrayList<Integer> sensors = intent.getIntegerArrayListExtra("sensors");
		groupTitle = intent.getStringExtra("title");

		DBManagerChannel dbManager = DBManagerChannel
				.getInstance(context);
		dbManager.connect(context);

		try {
			allChannels = DBManagerChannel.getAll();

		} catch (ParseException e) {
			e.printStackTrace();
		}

		ArrayList<ChannelElement> channelsToView = new ArrayList<ChannelElement>();
		for (Integer i : channels) {
			if (i != 0) {
				Log.d("noolite", String.valueOf(i));
				channelsToView.add(allChannels.get(i - 1));
			}
		}
		
//		int position = 1;
//		for (Integer i : sensors) {
//			if (i != 0) {
//				channelsToView.add(new ChannelElement(position-1, "������ �" + position,
//						44, 0, 0));
//			}
//			position++;
//		}

		customAdapter = new ChannelListAdapter(context,
				channelsToView);
		channelListView.setAdapter(customAdapter);
	}
	
	
}

