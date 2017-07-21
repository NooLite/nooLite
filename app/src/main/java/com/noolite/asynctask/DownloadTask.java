package com.noolite.asynctask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.params.HttpConnectionParams;

import com.noolite.MainActivity;
import com.noolite.NooLiteDefs;
import com.noolite.R;
import com.noolite.parsers.BinParser;
import com.noolite.settings.SettingsValues;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DownloadTask extends AsyncTask<String, Void, Integer> {
    private ProgressDialog dialog;
    private String TAG = "DownloadTask";
    private Activity context;

    private WeakReference<DownloadInterface> weakReferenceDownloadInterface;

    public DownloadTask(DownloadInterface downloadInterface) {
        this.weakReferenceDownloadInterface = new WeakReference<DownloadInterface>(
                downloadInterface);
    }

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
    protected void onPostExecute(Integer integer) {
        Log.d(NooLiteDefs.NOO_LOG, "DownloadTask : onPostExecute");
        super.onPostExecute(integer);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

//        makeDialog("Ошибка подключения к адресу, проверьте соединение с Wi-Fi или IP адрес");

        makeDialog("Данные успешно загружены");
        SettingsValues.setDemo(false);
        SharedPreferences.Editor edit = MainActivity.getSharedPref().edit();
        edit.putBoolean(NooLiteDefs.FLAG_DEMO, false);
        edit.commit();
    }

    //отображение диалогового окна с текстом, передающемся в параметре str
    public void makeDialog(String str) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        View view = (LinearLayout) context.getLayoutInflater().inflate(R.layout.dialog, null);
        adb.setView(view);
        TextView msg = (TextView) view.findViewById(R.id.message);
        msg.setText(str);
        Button btnOk = (Button) view.findViewById(R.id.okDialogButton);
        final Dialog alertDialog = adb.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    //запрос на скачивание бинарного файла с параметрами групп-каналов по адресу url[0]
    @Override
    protected Integer doInBackground(String... url) {
        Log.d(TAG, "loading gateway config: " + url[0]);

        try {
            byte[] fileData = loadGatewayData(url[0]);
            update();
        } catch (IOException ex) {
            Log.e(TAG, "loading gateway config", ex);
            return 1;
        }
        return 0;


    }

    private byte[] loadGatewayData(String url) throws IOException {
        URL u = new URL(url);
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
        if (SettingsValues.getAuth()) {
            c.addRequestProperty("Authorization", "Basic " + encoding);
        }
        c.setDoOutput(true);
        c.connect();

        //проверка сузествования файла для записи
        File myDirectory = new File(
                Environment.getExternalStorageDirectory().getPath(), NooLiteDefs.NOO_LITE);
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        //определение потока вывода
        FileOutputStream f = new FileOutputStream(
                new File(Environment.getExternalStorageDirectory().getPath() +
                        "/" + NooLiteDefs.NOO_LITE,
                        NooLiteDefs.NOO_SETTINGS_BIN));

        //определение потока ввода
        BufferedInputStream in = new BufferedInputStream(c.getInputStream());
        byte[] buffer = new byte[4102];
        int len1;
        int size = 0;

        while ((len1 = in.read(buffer)) > 0) {
            f.write(buffer, 0, len1);
            size += len1;
        }
        Log.d(NooLiteDefs.NOO_LOG, "data size=" + size);
        f.close();
        in.close();

//        BinParser binParse = new BinParser(buffer, context);
//        binParse.parceData();

        return buffer;
    }

    private void update() throws IOException {
        Log.d(TAG, "DataBase updating");

        String FILEPATH = Environment.getExternalStorageDirectory()
                .getPath() + "/nooLite/noolite_settings.bin";

        File file = new File(FILEPATH);
        byte[] fileData = new byte[(int) file.length()];

        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        dis.readFully(fileData);
        dis.close();
        BinParser binParse = new BinParser(fileData, context);
        binParse.parceData();

    }


}
