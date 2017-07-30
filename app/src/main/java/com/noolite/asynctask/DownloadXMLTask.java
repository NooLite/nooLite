package com.noolite.asynctask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import com.noolite.NooLiteDefs;
import com.noolite.parsers.XMLParser;
import com.noolite.SettingsValues;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class DownloadXMLTask extends AsyncTask<String, Void, Void> {
    private String TAG = DownloadXMLTask.class.getSimpleName();

	public DownloadXMLTask() {
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	//скачивание XML файла по заданному пути url[0]
	@Override
	protected Void doInBackground(String... url) {

		try {
            Log.d(TAG, url[0]);
			byte[] sensorData = loadSensorData(url[0]);
			XMLParser.parse(sensorData);
		} catch (Exception ex) {
			Log.e(TAG, "loading sensor data", ex);
		}

		return null;
	}

	private byte[] loadSensorData(String url) throws IOException {
		URL sensorUrl = new URL(url);

		String username = SettingsValues.getUsername();
		String password = SettingsValues.getPassword();
		String userPassword = username+":"+password;
		byte[] data = userPassword.getBytes("UTF-8");
		String encoding = Base64.encodeToString(data, Base64.DEFAULT);

		HttpURLConnection urlConnection = (HttpURLConnection) sensorUrl.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setConnectTimeout(4000);
		urlConnection.setReadTimeout(4000);
		urlConnection.setDoOutput(true);
		//добавление параметра авторизации в заголовок запроса, если это необходимо
		if(SettingsValues.getAuth()) {
			urlConnection.addRequestProperty("Authorization", "Basic " + encoding);
		}
		urlConnection.connect();
        BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());

        byte[] tmp = new byte[NooLiteDefs.SENSOR_DATA_SIZE * 10];
        int off = 0;
        int len = tmp.length;
        int ret;

		try {
            while ((ret = bis.read(tmp, off, len)) > 0) {
                off += ret;
                len -= ret;
            }
		} finally {
			if (bis != null) {
				try {
                    bis.close();
				} catch (IOException e) {
					Log.e(TAG, "InputStream closing ", e);
				}
			}
			urlConnection.disconnect();
		}
		return Arrays.copyOf(tmp, off);
	}


}