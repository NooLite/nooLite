package com.noolite.pebble;

import android.content.BroadcastReceiver;
import android.content.Context;

import com.getpebble.android.kit.PebbleKit;

import java.util.UUID;

/**
 * Created by urix on 7/29/2017.
 */

public class ReceiverFactory {
    private static ReceiverFactory impl = new ReceiverFactory();
    private NooDataReceiver nooDataReceiver = null;

    private ReceiverFactory() {
    }

    public static ReceiverFactory getInstance() {
        return impl;
    }

    /**
     * Usage:
     * PebbleKit.registerReceivedDataHandler(getApplicationContext(),
     *     ReceiverFactory.getInstance().getReceiver(getApplicationContext()));
     *
     * @param context main Application context
     * @return Receiver instance
     */
    public PebbleKit.PebbleDataReceiver getReceiver(Context context) {

        if (nooDataReceiver == null) {
//            nooDataReceiver = new NooDataReceiver(UUID.randomUUID(), context);
            nooDataReceiver = new NooDataReceiver(
                    UUID.fromString("1151b807-682b-46c2-a945-1707516fce6f"), context);
        }
        return nooDataReceiver;
    }

    public PebbleKit.PebbleDataReceiver getReceiver() {
        return nooDataReceiver;
    }

    /**
     * Alternative initialization of Received Data Handler.
     *
     * @param context main Application context
     */
    public void createAndRegisterReceiver(Context context) {

        if (nooDataReceiver == null) {
            nooDataReceiver = new NooDataReceiver(
                    UUID.fromString("1151b807-682b-46c2-a945-1707516fce6f"), context);
            PebbleKit.registerReceivedDataHandler(context, nooDataReceiver);
        }
    }
}
