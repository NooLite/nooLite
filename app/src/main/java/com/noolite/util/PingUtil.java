package com.noolite.util;

import android.content.Context;
import android.util.Log;

import com.noolite.ResultType;
import com.noolite.SettingsValues;
import com.noolite.asynctask.PingTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by urix on 7/23/2017.
 */

public class PingUtil {
    private static String TAG = PingUtil.class.getSimpleName();

    public static boolean ping(Context context) {
        boolean ret = false;

        if (SettingsValues.getDemo()) {
            return true;
        }
        PingTask task = new PingTask();

        try {
            ResultType result = task.execute(UrlUtils.getPingUrl()).get();

            if (!ResultType.SUCCESS_RESULT.equals(result)) {
                NooDialogUtils.makeDialog(result.getDescription(), context);
            } else {
                return true;
            }

        } catch (InterruptedException e) {
            Log.e(TAG, "ping" , e);
        } catch (ExecutionException e) {
            Log.e(TAG, "ping" , e);
        }

        return ret;

    }

}
