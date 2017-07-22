package com.noolite.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by urix on 7/21/2017.
 */

public class NooGroup {

    private int id;  //id группы
    private String name; //имя группы
    private List<Integer> channels; //список индексов каналов, входящих в группу
    private List<NooChannel> channelElements; //список индексов каналов, входящих в группу
    private List<NooChannel> sensorElements; //список индексов датчиков, входящих в группу
    private boolean visibility; //видимость группы в приложении

    public NooGroup() {
    }

    public NooGroup(int id, String name) {
        this.id = id;
        this.name = name;
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

    public List<NooChannel> getSensorElements() {
        if (sensorElements == null) {
            sensorElements = new ArrayList<NooChannel>(4);
        }
        return sensorElements;
    }

    public void setSensorElements(List<NooChannel> sensorElements) {
        this.sensorElements = sensorElements;
    }

    public List<NooChannel> getChannelElements() {
        if (channelElements == null) {
            channelElements = new ArrayList<NooChannel>(8);
        }
        return channelElements;
    }

    public void setChannelElements(List<NooChannel> channelElements) {
        this.channelElements = channelElements;
    }

    public boolean getVisibility(){
        return this.visibility;
    }

    public void setVisibility(boolean visibility){
        this.visibility = visibility;
    }
}
