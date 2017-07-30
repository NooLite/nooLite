package com.noolite.domain;

/**
 * Created by urix on 7/16/2017.
 */

public class SensorData {
    private int id;
    private String airTemperature;
    private String airHumidity;
    private String status;

    public SensorData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(String airTemperature) {
        this.airTemperature = airTemperature;
    }

    public String getAirHumidity() {
        return airHumidity;
    }

    public void setAirHumidity(String airHumidity) {
        this.airHumidity = airHumidity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "id=" + id +
                ", airTemperature='" + airTemperature + '\'' +
                ", airHumidity='" + airHumidity + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
