package com.noolite.groups;

import com.noolite.channels.ChannelElement;

import java.util.ArrayList;
import java.util.List;

//класс для хранения информации о группе
public class GroupElement {

	private int id;  //id группы
	private String name; //имя группы
	private List<Integer> channels; //список индексов каналов, входящих в группу
    private List<ChannelElement> channelElements; //список индексов каналов, входящих в группу
	private List<SensorElement> sensorElements; //список индексов датчиков, входящих в группу
	private boolean visibility; //видимость группы в приложении

	public GroupElement() {
	}

	public GroupElement(int id, String name, boolean visibility) {
		this.id = id;
		this.name = name;
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

	public List<Integer> getChannels() {
		if (channels == null) {
			channels = new ArrayList<Integer>(8);
		}
		return channels;
	}

	public void setChannels(List<Integer> channels) {
		this.channels = channels;
	}

	public List<SensorElement> getSensorElements() {
		if (sensorElements == null) {
			sensorElements = new ArrayList<SensorElement>(4);
		}
		return sensorElements;
	}

	public void setSensorElements(List<SensorElement> sensorElements) {
		this.sensorElements = sensorElements;
	}

    public List<ChannelElement> getChannelElements() {
        if (channelElements == null) {
            channelElements = new ArrayList<ChannelElement>(8);
        }
        return channelElements;
    }

    public void setChannelElements(List<ChannelElement> channelElements) {
        this.channelElements = channelElements;
    }

    public boolean getVisibility(){
		return this.visibility;
	}
	
	public void setVisibility(boolean visibility){
		this.visibility = visibility;
	}
}
