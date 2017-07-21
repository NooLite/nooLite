package com.noolite;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.noolite.adapters.CustomListAdapter;
import com.noolite.groups.GroupElement;
import com.noolite.groups.SensorElement;

//Меню настроек, где доступен выбор - общие настройки приложения или настройки работы с pebble
public class SettingsMenu extends Activity implements OnClickListener, OnItemClickListener{
	
	private ActionBar actionBar;
	private View view;
	private ImageButton back;
	private ListView menu;

	private final int APP_SETTINGS = 0;
	private final int PEBBLE_SETTINGS = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_menu);
		LayoutInflater vi = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = vi.inflate(R.layout.action_bar_settings_activity, null);
		actionBar = getActionBar();

		actionBar.setCustomView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		back = (ImageButton) view.findViewById(R.id.backBtnSettings);
		back.setOnClickListener(this);

		//создание списка меню
		ArrayList<GroupElement> listItems = new ArrayList<GroupElement>();
		listItems.add(new GroupElement(1, "Общее", true));
		listItems.add(new GroupElement(2, "Pebble Watch", true));
		
		menu = (ListView) findViewById(R.id.menu);
		final CustomListAdapter customAdapter = new CustomListAdapter(this,
				listItems);
		menu.setAdapter(customAdapter);
		menu.setOnItemClickListener(this);
		
	}

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

	//переход на необходимое Activity настроек
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case APP_SETTINGS:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			finish();
			break;
		case PEBBLE_SETTINGS:
			intent = new Intent(this, PebbleSettings.class);
			startActivity(intent);
			finish();
			break;
		}
		
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
	
}
