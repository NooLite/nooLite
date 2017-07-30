package com.noolite;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;


//Activity настроек работы с pebble
public class PebbleSettings extends Activity{
	
	private ActionBar actionBar;
	private View view;
	private ImageButton back, pebbleEnabled;
	private TextView connectionStatus, moreInfoTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pebble_settings);
		LayoutInflater vi = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = vi.inflate(R.layout.action_bar_settings_activity, null);
		actionBar = getActionBar();

		actionBar.setCustomView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		//инициализация UI в соответствии с текущими настройками
		pebbleEnabled = (ImageButton) findViewById(R.id.pebbleEnabledSwitch);
		if(SettingsValues.isWatchesEnabled()){
			pebbleEnabled.setImageResource(R.drawable.selected);
			SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
			edit.putBoolean("watches", true);
			edit.commit();
		}else{
			pebbleEnabled.setImageResource(R.drawable.unselected);
			SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
			edit.putBoolean("watches", false);
			edit.commit();
		}

		//обработчик события изменения параметра "включить работу с pebble"
		//и обновление системных настроек приложения в соответствии с новыми параметрами
		pebbleEnabled.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(SettingsValues.isWatchesEnabled()){
					pebbleEnabled.setImageResource(R.drawable.unselected);
					SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
					edit.putBoolean("watches", false);
					SettingsValues.setUseWatches(false);
					edit.commit();
				}else{
					pebbleEnabled.setImageResource(R.drawable.selected);
					SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
					SettingsValues.setUseWatches(true);
					edit.putBoolean("watches", true);
					edit.commit();
				}
			}
		});

		//кнопка для возврата на меню настроек и обрабочик события нажатия на него
		back = (ImageButton) view.findViewById(R.id.backBtnSettings);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SettingsMenu.class);
				startActivity(intent);
				finish();
			}
		});

		//диалоговое окно с требованием включить Bluetooth для работы с pebble
		int REQUEST_ENABLE_BT = 0;
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		//отображение текущего состояния соединения с pebble
		connectionStatus = (TextView) findViewById(R.id.connectionStatus);
		if(PebbleKit.isWatchConnected(getApplicationContext())){
			connectionStatus.setText("Подключено");
		}else{
			connectionStatus.setText("Не подключено");
		}

		//переход на окно с подробным описанием работы pebble и приложения
		moreInfoTextView = (TextView) findViewById(R.id.moreInfoTextView);
		moreInfoTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
				startActivity(intent);
				finish();
			}
		});	
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, SettingsMenu.class);
		startActivity(intent);
		finish();
		super.onBackPressed();
	}	

}
