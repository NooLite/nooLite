package com.noolite.groups;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by urix on 7/16/2017.
 */

public class SensorStatus {

    public static final String SENSOR_STATUS_0 = "0";
    public static final String SENSOR_STATUS_1 = "1";
    public static final String SENSOR_STATUS_2 = "2";
    public static final String SENSOR_STATUS_3 = "3";
    public static final Map<String, String> SENSOR_STATUS_MSG = new HashMap<String, String>();

    static {
        SENSOR_STATUS_MSG.put(SENSOR_STATUS_0, null);
        SENSOR_STATUS_MSG.put(SENSOR_STATUS_1, "Датчик не привязан");
        SENSOR_STATUS_MSG.put(SENSOR_STATUS_2, "Нет сигнала с датчика");
        SENSOR_STATUS_MSG.put(SENSOR_STATUS_3, "Внутренняя ошибка (3)");
    }

    public static String getStatusMessage(String key) {
        return SENSOR_STATUS_MSG.get(key);
    }

}
