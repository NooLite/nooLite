package com.noolite.pebble;

import android.content.Context;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.noolite.SettingsValues;

import java.util.UUID;

/**
 * Created by urix on 7/25/2017.
 */

public class NooDataReceiver extends PebbleKit.PebbleDataReceiver {
    private String TAG = NooDataReceiver.class.getSimpleName();
    PebbleManager pebbleManager;
    UUID subscribedUuid;
    private int KEY_INPUT_PEBBLE = 33;

    public NooDataReceiver(UUID subscribedUuid, Context context) {
        super(subscribedUuid);
        this.subscribedUuid = subscribedUuid;
        pebbleManager = new PebbleManager(context);
    }

    @Override
    public void receiveData(Context context, int transactionId, PebbleDictionary pebbleDictionary) {

        Log.d(TAG, pebbleDictionary.toJsonString());
        // A new AppMessage was received, tell Pebble
        PebbleKit.sendAckToPebble(context, transactionId);

        //если в данный момент в настройках активирована работа с pebble,
        //происходит ее обработка
        if (SettingsValues.isWatchesEnabled()) {
            try {
                pebbleManager.sendMessage(pebbleDictionary.getString(KEY_INPUT_PEBBLE), subscribedUuid);

            } catch (Exception e) {
                Log.e(TAG, "process watches", e);
            }
        } else {
            Log.d(TAG, "Watches disabled");
        }


    }
}
