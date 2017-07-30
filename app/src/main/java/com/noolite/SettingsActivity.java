package com.noolite;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.noolite.asynctask.DownloadTask;
import com.noolite.util.UrlUtils;

//настройки приложения
public class SettingsActivity extends Activity implements OnClickListener {

	private ActionBar actionBar;
	private Button getData, saveData;
	private ImageButton back;
	private EditText ipField, usernameField, passwordField;

	private final String FILEPATH = Environment.getExternalStorageDirectory()
			.getPath() + "/nooLite/noolite_settings.bin";
	private CheckBox playSound, needAuthentification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		LayoutInflater vi = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.action_bar_settings_activity, null);

		//инициализация элементов UI согласно текущим настройкам приложения
		playSound = (CheckBox) findViewById(R.id.playSound);
		playSound.setChecked(SettingsValues.getSound());
		playSound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (playSound.isChecked()) {
					SettingsValues.setSound(true);
				} else {
					SettingsValues.setSound(false);
				}
				SharedPreferences.Editor edit = MainActivity.getSharedPref()
						.edit();
				edit.putBoolean(getString(R.string.play_sound),
						playSound.isChecked());
				edit.commit();
			}
		});

		ipField = (EditText) findViewById(R.id.ipField);
		ipField.setText(SettingsValues.getIP());

		passwordField = (EditText) findViewById(R.id.passwordField);
		passwordField.setText(SettingsValues.getPassword());

		usernameField = (EditText) findViewById(R.id.usernameField);
		usernameField.setText(SettingsValues.getUsername());

		//инициализация UI для идентификации
		needAuthentification = (CheckBox) findViewById(R.id.needAuth);
		needAuthentification.setChecked(MainActivity.getSharedPref()
				.getBoolean("auth", true));
		if (!needAuthentification.isChecked()) {
			TextView usernameTextView = (TextView) findViewById(R.id.usernameLabel);
			usernameTextView.setTextColor(0xffbbbbbb);
			TextView passwordTextView = (TextView) findViewById(R.id.passwordLabel);
			passwordTextView.setTextColor(0xffbbbbbb);
			usernameField.setEnabled(false);
			usernameField.setTextColor(0xffbbbbbb);
			passwordField.setEnabled(false);
			passwordField.setTextColor(0xffbbbbbb);
		}

		//изменение настроек аутентификации
		needAuthentification.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (needAuthentification.isChecked()) {
					TextView usernameTextView = (TextView) findViewById(R.id.usernameLabel);
					usernameTextView.setTextColor(0xff7f7f7f);
					TextView passwordTextView = (TextView) findViewById(R.id.passwordLabel);
					passwordTextView.setTextColor(0xff7f7f7f);
					usernameField.setEnabled(true);
					usernameField.setTextColor(0xff00B89C);
					passwordField.setEnabled(true);
					passwordField.setTextColor(0xff00B89C);
					SettingsValues.setAuth(true);
					//обновление информации в SharedPreferences приложения
					SharedPreferences.Editor edit = MainActivity
							.getSharedPref().edit();
					edit.putBoolean("auth", true);
					edit.commit();
				} else {
					TextView usernameTextView = (TextView) findViewById(R.id.usernameLabel);
					usernameTextView.setTextColor(0xffbbbbbb);
					TextView passwordTextView = (TextView) findViewById(R.id.passwordLabel);
					passwordTextView.setTextColor(0xffbbbbbb);
					SettingsValues.setAuth(false);
					usernameField.setEnabled(false);
					usernameField.setTextColor(0xffbbbbbb);
					passwordField.setEnabled(false);
					passwordField.setTextColor(0xffbbbbbb);
					//обновление информации в SharedPreferences приложения
					SharedPreferences.Editor edit = MainActivity
							.getSharedPref().edit();
					edit.putBoolean("auth", false);
					edit.commit();
				}

			}
		});

		getData = (Button) findViewById(R.id.getData);
		getData.setOnClickListener(this);
		
		saveData = (Button) findViewById(R.id.saveData);
		saveData.setOnClickListener(this);

		actionBar = getActionBar();
		actionBar.setCustomView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		back = (ImageButton) view.findViewById(R.id.backBtnSettings);
		back.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.getData:
			//запись данных и обновление информации со шлюза
			SettingsValues.setIP(ipField.getText().toString());
			SettingsValues.setUsername(usernameField.getText().toString());
			SettingsValues.setPassword(passwordField.getText().toString());
			SettingsValues.setAuth(needAuthentification.isChecked());

			SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
			edit.putString("IP", ipField.getText().toString());
			edit.commit();
			edit.putString("username", usernameField.getText().toString());
			edit.commit();
			edit.putString("password", passwordField.getText().toString());
			edit.commit();
			edit.putBoolean("auth", needAuthentification.isChecked());
			edit.commit();
            DownloadTask dt = new DownloadTask(this);
            dt.execute(UrlUtils.getGatewaySettingsUrl());
			break;

		//возврат на Activity с меню настроек по нажатии на кнопку "назад"
		case R.id.backBtnSettings:
			Intent intent = new Intent(this, SettingsMenu.class);
			startActivity(intent);
			finish();
			break;

		//сохранение информации, введенной пользователем
		case R.id.saveData:
			SettingsValues.setIP(ipField.getText().toString());
			SettingsValues.setUsername(usernameField.getText().toString());
			SettingsValues.setPassword(passwordField.getText().toString());
			SettingsValues.setAuth(needAuthentification.isChecked());

			edit = MainActivity.getSharedPref().edit();
			edit.putString("IP", ipField.getText().toString());
			edit.commit();

			edit.putString("username", usernameField.getText().toString());
			edit.commit();

			edit.putString("password", passwordField.getText().toString());
			edit.commit();

			edit.putBoolean("auth", needAuthentification.isChecked());
			edit.commit();

			//вывод идалога об удачном сохранении новых параметров
			makeDialog("данные успешно сохранены");
			break;
		}
	}

	//отображение диалогового окна с текстом, передающемся в параметре str
	public void makeDialog(String str) {
		final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);
		adb.setView(view);
		TextView msg = (TextView) view.findViewById(R.id.message);
		msg.setText(str);
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

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, SettingsMenu.class);
		startActivity(intent);
		finish();
		super.onBackPressed();
	}
}