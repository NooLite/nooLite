package com.noolite.settings;

import com.noolite.groups.SensorData;

import java.util.ArrayList;
import java.util.List;

public class SettingsValues {

	//класс для хранения настроек текущей сессии приложения (при закрытии приложения и т.д.
	//настройки записываются в SharedPreferences, откуда потом инициализируются при включении
	private static boolean playSound = true;  //включен или выключен звук
	private static String ipAddress = "192.168.0.168";  //IP адрес
	private static String password = "";  //пароль для аутентификации
	private static String username = "";  //логин для аутентификации
	private static boolean needAuth = true;  //требуется или не требуется аутентицикация
	private static int downloads = 0;  //число скачиваний информации со шлюза
	private static boolean isDemo = true;  //активен или неактивен демо-режим
	private static boolean useWatches = true;  //включить или выключить работу с pebble
	public static ArrayList<String> sensorValues = new ArrayList<String>();  //значения датчиков
    private static List<SensorData> sensorData = new ArrayList<SensorData>();  //значения датчиков
	
	public static void setUseWatches(boolean useW){
		useWatches = useW;
	}
	
	public static boolean isWatchesEnabled(){
		return useWatches;
	}
	
	public static void setDemo(boolean demo){
		isDemo= demo;
	}
	
	public static boolean getDemo(){
		return isDemo;
	}
	
	public static void setAuth(boolean newAuth){
		needAuth = newAuth;
	}
	
	public static boolean getAuth(){
		return needAuth;
	}
	
	public static void setSensorValues(ArrayList<String> values){
		sensorValues = values;
	}

    public static synchronized void setSensorData(List<SensorData> values){
        sensorData.clear();
        sensorData.addAll(values);
    }

    public static List<SensorData> getSensorData(){
        return sensorData;
    }

	public static void setSound(boolean play){
		playSound = play;
	}
	
	public static boolean getSound(){
		return playSound;
	}
	
	public static void setIP(String address){
		ipAddress = address;
	}
	
	public static String getIP(){
		return ipAddress;
	}
	
	public static void setPassword(String newPassword){
		password = newPassword;
	}
	
	public static void setUsername( String newUsername ){
		username = newUsername;
	}
	
	public static String getPassword(){
		return password;
	}
	
	public static String getUsername(){
		return username;
	}
	
	public static void setDownloads(int newDownloads){
		downloads =  newDownloads;
	}
	
	public static int getDownloads(){
		return downloads;
	}
	
	

}
