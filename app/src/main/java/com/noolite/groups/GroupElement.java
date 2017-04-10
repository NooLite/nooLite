package com.noolite.groups;

import java.util.ArrayList;

import com.getpebble.android.kit.util.PebbleDictionary;

//класс для хранения информации о группе
public class GroupElement {

	private int id;  //id группы
	private String name; //имя группы
	private ArrayList<Integer> channels; //список индексов каналов, входящих в группу
	private ArrayList<Integer> sensors; //список индексов датчиков, входящих в группу
	private boolean visibility; //видимость группы в приложении

	public GroupElement(int id, String name, ArrayList<Integer> channels,
			ArrayList<Integer> sensors, boolean visibility) {
		this.id = id;
		this.name = name;
		this.channels = channels;
		this.sensors = sensors;
		this.visibility = visibility;
		
		
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Integer> getChannels() {
		return channels;
	}

	public void setChannels(ArrayList<Integer> channels) {
		this.channels = channels;
	}

	public ArrayList<Integer> getSensors() {
		return sensors;
	}

	public boolean getVisibility(){
		return this.visibility;
	}
	
	public void setVisibility(boolean visibility){
		this.visibility = visibility;
	}
}
