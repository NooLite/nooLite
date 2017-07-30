package com.noolite.domain;

/**
 * Created by urix on 7/10/2017.
 */

public class SensorElement {
    private String name;
    private Integer id;
    private int visibility;

    public SensorElement(String name, Integer id) {
        this.name = name;
        this.id = id;
        this.visibility = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "SensorElement{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", visibility=" + visibility +
                '}';
    }
}
