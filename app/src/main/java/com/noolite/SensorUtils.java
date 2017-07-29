package com.noolite;

import com.noolite.groups.SensorData;
import com.noolite.settings.SettingsValues;

import java.util.List;

/**
 * Created by urix on 7/29/2017.
 */

public class SensorUtils {

    public static SensorData getSensorData(int sensorId) {

        //список текущих значений показаний датчиков
        List<SensorData> sensorDataList = SettingsValues.getSensorData();

        for (SensorData sensorData : sensorDataList) {
            if (sensorData.getId() == sensorId) {
                return sensorData;
            }
        }
        return null;
    }

    public static String getSensorValue(int sensorId) {
        StringBuilder sb = new StringBuilder();
        SensorData currentSensor = getSensorData(sensorId);

        if (currentSensor != null) {
            sb.append(currentSensor.getAirTemperature() + " \u00B0C").append(" ");
            sb.append(currentSensor.getAirHumidity() + " % RH");

            //отображение типа возникшей ошибки
            if (currentSensor.getStatus() != null) {
                sb.append(currentSensor.getStatus());
            }
        } else {
            sb.append("- \u00B0C").append(" ");
            sb.append("- % RH");
        }
        return sb.toString();
    }

}
