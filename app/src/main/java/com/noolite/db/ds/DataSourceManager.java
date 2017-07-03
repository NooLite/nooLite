package com.noolite.db.ds;

import android.content.Context;

import com.noolite.db.NooLiteDB;

/**
 * Created by urix on 13.04.17.
 */

public class DataSourceManager {
    private NooLiteDB dbHelper = null;

    public ChannelsDataSource getChannelsDataSource(Context context) {

        return null;


    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

}
