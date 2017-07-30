package com.noolite.asynctask;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.noolite.AppException;
import com.noolite.util.NooDialogUtils;
import com.noolite.ResultType;
import com.noolite.MainActivity;
import com.noolite.NooLiteDefs;
import com.noolite.parsers.BinParser;
import com.noolite.SettingsValues;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Void, ResultType> {
    private ProgressDialog dialog;
    private String TAG = DownloadTask.class.getSimpleName();
    private Activity context;

    public DownloadTask(Activity ctx) {
        this.context = ctx;
        this.dialog = new ProgressDialog(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Загрузка настроек шлюза ...");
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onPostExecute(ResultType result) {
        Log.d(TAG, "result = " + result);
        super.onPostExecute(result);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (ResultType.SUCCESS_RESULT.equals(result)) {
            NooDialogUtils.makeDialog("Данные успешно загружены", context);
            // finalize DEMO mode
            SettingsValues.setDemo(false);
            SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
            edit.putBoolean(NooLiteDefs.FLAG_DEMO, false);
            edit.commit();
        } else {
            NooDialogUtils.makeDialog(result.getDescription(), context);
        }
    }

    /**
     * Download Geaway config binary data.
     * Parse it and store to DB.
     *
     * @param url Geaway address
     * @return code result
     */
    @Override
    protected ResultType doInBackground(String... url) {
        Log.d(TAG, "loading gateway config: " + url[0]);

        try {
            byte[] gatewayData = loadGatewayData(url[0]);
            BinParser.parseData(gatewayData, context);

        } catch (AppException ex) {
            return ex.getResultType();

        } catch (Exception ex) {
            Log.e(TAG, "loading gateway config", ex);
            return ResultType.INTERNAL_ERROR;
        }
        return ResultType.SUCCESS_RESULT;
    }

    private byte[] loadGatewayData(String url) throws AppException {
        byte[] buffer = new byte[NooLiteDefs.GATEWAY_DATA_SIZE];
        HttpURLConnection connection;

        try {
            URL  u = new URL(url);
            String username = SettingsValues.getUsername();
            String password = SettingsValues.getPassword();
            String userPassword = username + ":" + password;
            byte[] data = userPassword.getBytes("UTF-8");

            String encoding = Base64.encodeToString(data, Base64.DEFAULT);
            connection = (HttpURLConnection) u.openConnection();
            //добавление параметров аутентификации в заголовок
            if (SettingsValues.getAuth()) {
                connection.addRequestProperty("Authorization", "Basic " + encoding);
            }
            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();

        } catch (MalformedURLException ex) {
            Log.e(TAG, "URL object", ex);
            throw new AppException(ResultType.URL_ERROR);

        } catch (UnsupportedEncodingException ex) {
            Log.e(TAG, "UTF-8 Encoding", ex);
            throw new AppException(ResultType.UNSUPPORTED_ENCODING);

        } catch (IOException e) {
            Log.e(TAG, "HttpURLConnection ", e);
            throw new AppException(ResultType.CONNECTION_ERROR);
        }
        DataInputStream dis = null;

        try {
            dis = new DataInputStream(connection.getInputStream());
            dis.readFully(buffer);

        } catch (IOException e) {
            Log.e(TAG, "DataInputStream ", e);
            throw new AppException(ResultType.READ_ERROR);

        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    Log.e(TAG, "InputStream closing ", e);
                }
            }
            connection.disconnect();
        }
        return buffer;
    }
}
