package com.noolite.asynctask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.params.HttpConnectionParams;

import com.noolite.settings.SettingsValues;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Void, Integer> {

    private WeakReference<DownloadInterface> weakReferenceDownloadInterface;

    public DownloadTask(DownloadInterface downloadInterface) {
        this.weakReferenceDownloadInterface = new WeakReference<DownloadInterface>(
                downloadInterface);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //запрос на скачивание бинарного файла с параметрами групп-каналов по адресу url[0]
    @Override
    protected Integer doInBackground(String... url) {
        try {
            Log.d("loading gateway config", url[0]);
            URL u = new URL(url[0]);
            String username = SettingsValues.getUsername();
            String password = SettingsValues.getPassword();
            String userPassword = username + ":" + password;
            byte[] data = userPassword.getBytes("UTF-8");
            String encoding = Base64.encodeToString(data, Base64.DEFAULT);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setConnectTimeout(20000);
            c.setReadTimeout(20000);
            c.setRequestMethod("GET");
            //добавление параметров аутентификации в заголовок
            //если это необходимо
            if (SettingsValues.getAuth())
                c.addRequestProperty("Authorization", "Basic " + encoding);
            c.setDoOutput(true);
            c.connect();

            //проверка сузествования файла для записи
            File myDirectory = new File(
                    Environment.getExternalStorageDirectory().getPath(), "nooLite");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
            //определение потока вывода
            FileOutputStream f = new FileOutputStream(new File(
                    Environment.getExternalStorageDirectory().getPath() + "/nooLite",
                    "noolite_settings.bin"));

            //определение потока ввода
            BufferedInputStream in = new BufferedInputStream(c.getInputStream());
            byte[] buffer = new byte[4102];
            int len1;
            int size = 0;

            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
                size += len1;
            }
            Log.d("loading gateway config", "data size=" + size);
            f.close();
            in.close();

        } catch (Exception ex) {
            Log.e("noolite", "loading gateway config", ex);
            return 1;
        }
        return 0;
    }

}
