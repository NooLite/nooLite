package com.noolite.asynctask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import com.noolite.parsers.BinParser;
import com.noolite.settings.SettingsValues;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class DownloadXMLTask extends AsyncTask<String, Void, Void> {

	private WeakReference<DownloadInterface> weakReferenceDownloadInterface;

	public DownloadXMLTask(DownloadInterface downloadInterface) {
		this.weakReferenceDownloadInterface = new WeakReference<DownloadInterface>(
				downloadInterface);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	//скачивание XML файла по заданному пути url[0]
	@Override
	protected Void doInBackground(String... url) {

		try {
			URL link = new URL(url[0]);
			
			String username = SettingsValues.getUsername();
			String password = SettingsValues.getPassword();
			String userPassword = username+":"+password;
			byte[] data = userPassword.getBytes("UTF-8");
			String encoding = Base64.encodeToString(data, Base64.DEFAULT);
			
			HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			//добавление параметра авторизации в заголовок запроса, если это необходимо
			if(SettingsValues.getAuth())
				urlConnection.addRequestProperty("Authorization", "Basic " + encoding);
			urlConnection.connect();
			File myDirectory = new File(Environment.getExternalStorageDirectory().getPath(), "nooLite");

			//проверка на наличие файла записи
			if(!myDirectory.exists()) {                                 
			  myDirectory.mkdirs();
			}
			//определение потока вывода
			FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/nooLite",
					"sens.xml"));

			//определение потока ввода
			InputStream inputStream = urlConnection.getInputStream();

			byte[] buffer = new byte[4096];
			
			int downloadLength = 0;

			while ((downloadLength = inputStream.read(buffer)) > 0) {
            	fileOutputStream.write(buffer, 0, downloadLength);
			}
			//закрытие потока записи в файл
			fileOutputStream.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}