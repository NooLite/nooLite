package com.noolite.parsers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.noolite.NooLiteDefs;
import com.noolite.groups.SensorData;
import com.noolite.settings.SettingsValues;

import android.content.Context;
import android.util.Log;

//парсер для XML с информацией о датчиках
public class XMLParser {
    private static String TAG = XMLParser.class.getSimpleName();
    public static final String TAG_T = "snst";
    public static final String TAG_H = "snsh";
    public static final String TAG_S = "snt";
    public static final String TAG_HEADER = "response";


	public static void parse(byte[] sensorData) {

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
            ByteArrayInputStream is = new ByteArrayInputStream(sensorData);
			xpp.setInput(is, "cp1251");
            List<SensorData> sensorDataList = new ArrayList<SensorData>();
            SensorData currentSensor = new SensorData();
            String currentTag = "";

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {

                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();

                    if (tagName.startsWith(TAG_HEADER)) {
                        currentTag = TAG_HEADER;
                    } else if (tagName.startsWith(TAG_T)) {
                        currentTag = TAG_T;
                    } else if (tagName.startsWith(TAG_H)) {
                        currentTag = TAG_H;
                    } else if (tagName.startsWith(TAG_S)) {
                        currentTag = TAG_S;
                    }
                }
				//если текущий обрабатываемый элемент - текст, то его значения записываются в
				// ArrayList со значениями показателей датчика
                else if (xpp.getEventType() == XmlPullParser.TEXT) {
					String tmp = xpp.getText();

                    if (currentTag.equals(TAG_T)) {
                        currentSensor.setAirTemperature(tmp);

                    } else if (currentTag.equals(TAG_H)) {
                        currentSensor.setAirHumidity(tmp);

                    } else if (currentTag.equals(TAG_S)) {
                        currentSensor.setStatus(tmp);
                    }

				} else if (xpp.getEventType() == XmlPullParser.END_TAG) {

                    if (currentSensor.getStatus() != null &&
                            currentSensor.getAirTemperature() != null &&
                            currentSensor.getAirHumidity() != null) {
                        currentSensor.setId(sensorDataList.size() + 1);
                        sensorDataList.add(currentSensor);
                        Log.d(TAG, currentSensor.toString());
                        currentSensor = new SensorData();
                    }
                    currentTag = "";
                }
				xpp.next();
			}
			//передача полученных значений в класс, хранящий текущие настройки
            SettingsValues.setSensorData(sensorDataList);

        } catch (XmlPullParserException e ) {
            Log.e(TAG, "", e);

        } catch (IOException e) {
            Log.e(TAG, "", e);

        }

	}
}
