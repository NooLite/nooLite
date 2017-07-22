package com.noolite.domain;

/**
 * Created by urix on 7/21/2017.
 */

public class NooChannel {
    private int id; //id канала
    private String name; //имя канала
    private int type; //тип канала
    private int state; //текущее состояние
    private int previousState; //прошлое состояние

    public NooChannel(int id) {
        this.id = id;
    }

    public NooChannel(int id, String name, int type, int state, int previousState) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.state = state;
        this.previousState = previousState;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPreviousState(){
        return this.previousState;
    }

    public void setPreviousState(int previousState){
        this.previousState = previousState;
    }

    @Override
    public String toString() {
        return "ChannelElement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", previousState=" + previousState +
                '}';
    }


}
