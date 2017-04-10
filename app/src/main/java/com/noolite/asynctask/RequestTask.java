package com.noolite.asynctask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import com.noolite.SettingsActivity;
import com.noolite.settings.SettingsValues;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class RequestTask extends AsyncTask<String, Void, Integer> {

	
	private WeakReference<RequestInterface> weakReferenceRequestInterface;

	
	public RequestTask(RequestInterface requestInterface) {
		this.weakReferenceRequestInterface = new WeakReference<RequestInterface>(
				requestInterface);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}


	//отправка запроса на шлюз по адресу url[0]
	@Override
	protected Integer doInBackground(String... url) {
		try{
			Log.d("RequestTask: ", url[0]);
			URL obj = new URL(url[0]);
			
			String username = SettingsValues.getUsername();
			String password = SettingsValues.getPassword();
			String userPassword = username+":"+password;
			byte[] data = userPassword.getBytes("UTF-8");
			String encoding = Base64.encodeToString(data, Base64.DEFAULT);
			
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			//проверна на необходимость добавление параметра аутентификации
			// в заговолок http запроса
			if(SettingsValues.getAuth())
				con.addRequestProperty("Authorization", "Basic " + encoding);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
		}catch(Exception ex){
			Log.e("RequestTask: ", "send RQ to gate", ex);
			return 1;
		}
		return 0;
	}

}
