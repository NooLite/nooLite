package com.noolite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by urix on 7/23/2017.
 */

public class NooDialogUtils {

    //отображение диалогового окна с текстом, передающемся в параметре str
    public static void makeDialog(String str, Context context) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog, null);
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
}
