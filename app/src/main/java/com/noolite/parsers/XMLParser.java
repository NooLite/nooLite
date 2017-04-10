package com.noolite.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.noolite.settings.SettingsValues;

import android.content.Context;
import android.util.Log;

//парсер для XML с информацией о датчиках
public class XMLParser {

	public static void parse(Context context, InputStream in) {

		try {

			XmlPullParser xpp = prepareXpp(in);
			ArrayList<String> sensorValues = new ArrayList<String>();
			int count=0;

			//последвательное чтение XML
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				//если текущий обрабатываемый элемент - текст, то его значения записываются в
				// ArrayList со значениями показателей датчика
				case XmlPullParser.TEXT:
					String tmp = xpp.getText();
					if((tmp.charAt(0)>='0' && tmp.charAt(0)<='9') || (tmp.charAt(0)=='-')){
						sensorValues.add(tmp);
						Log.d("noolite", "    "+tmp);
					}
					break;
				}
				xpp.next();
			}
			//передача полученных значений в класс, хранящий текущие настройки
			SettingsValues.setSensorValues(sensorValues);

		} catch (XmlPullParserException e ) {
			Log.d("noolite", e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("noolite", e.toString());
		}

	}

	//подготовка парсера
	public static XmlPullParser prepareXpp(InputStream in) throws XmlPullParserException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		//настройка потока ввода
		xpp.setInput(in, "cp1251");
		return xpp;
	}
}
