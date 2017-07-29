package com.noolite.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.noolite.ResultType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by urix on 7/23/2017.
 */

public class PingTask extends AsyncTask<String, Void, ResultType> {
    private String TAG = PingTask.class.getSimpleName();

    public PingTask() {
        super();
    }

    @Override
    protected void onPostExecute(ResultType result) {
        Log.d(TAG, "result = " + result);
        super.onPostExecute(result);
    }

    @Override
    protected ResultType doInBackground(String... url) {
        try{
            Log.d(TAG, url[0]);
            URL obj = new URL(url[0]);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(4000);
            con.setReadTimeout(4000);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (Exception ex){
            Log.e(TAG, "ping", ex);
            return ResultType.CONNECTION_ERROR;
        }
        return ResultType.SUCCESS_RESULT;
    }
}
