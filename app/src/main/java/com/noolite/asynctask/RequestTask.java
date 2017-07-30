package com.noolite.asynctask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.noolite.util.NooDialogUtils;
import com.noolite.ResultType;
import com.noolite.SettingsValues;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class RequestTask extends AsyncTask<String, Void, ResultType> {
    private String TAG = RequestTask.class.getSimpleName();
    private Context context;
	private ProgressDialog dialog;

	
//	private WeakReference<RequestInterface> weakReferenceRequestInterface;

	public RequestTask(Context ctx) {
		super();
        this.context = ctx;
		this.dialog = new ProgressDialog(ctx);

	}

//	public RequestTask(RequestInterface requestInterface) {
//		super();
//		this.weakReferenceRequestInterface = new WeakReference<RequestInterface>(
//				requestInterface);
//	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		dialog.setMessage("отправка команды ...");
//		dialog.setIndeterminate(true);
//		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		dialog.setCancelable(false);
//		dialog.show();
	}

    @Override
    protected void onPostExecute(ResultType result) {
        Log.d(TAG, "result = " + result);
        super.onPostExecute(result);

//        if (dialog.isShowing()) {
//            dialog.dismiss();
//        }
        if (!ResultType.SUCCESS_RESULT.equals(result) && !SettingsValues.getDemo()) {
            NooDialogUtils.makeDialog(result.getDescription(), context);
        }
    }


	//отправка запроса на шлюз по адресу url[0]
	@Override
	protected ResultType doInBackground(String... url) {
        Log.d(TAG, url[0]);

        if (url[0] == null || SettingsValues.getDemo()) {
            return ResultType.SUCCESS_RESULT;
        }

		try{

			URL obj = new URL(url[0]);
			
			String username = SettingsValues.getUsername();
			String password = SettingsValues.getPassword();
			String userPassword = username+":"+password;
			byte[] data = userPassword.getBytes("UTF-8");
			String encoding = Base64.encodeToString(data, Base64.DEFAULT);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(4000);
			con.setReadTimeout(4000);

			if(SettingsValues.getAuth()) {
                con.addRequestProperty("Authorization", "Basic " + encoding);
            }

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
		} catch (Exception ex){
			Log.e(TAG, "send RQ to gate", ex);
			return ResultType.CONNECTION_ERROR;
		}
		return ResultType.SUCCESS_RESULT;
	}

}
