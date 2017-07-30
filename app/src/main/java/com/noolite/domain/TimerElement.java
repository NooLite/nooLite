package com.noolite.domain;

//класс для хранения информации о таймере
public class TimerElement {

    private int id;  //id таймера
    private boolean isOn;   //вкл. или выкл.
    private boolean singleActivation;  //одинарное или многократное срабатывание
    private boolean[] activeDays;  //информация о днях срабатывания
    private int hour;  //час срабатывания таймера
    private int minute;  //минута срабатывания таймера
    private int command;  //тип команды, включаемой по таймеру
    private String title;  //название таймера

    public TimerElement(int id, boolean isOn, boolean singleActivation,
                        boolean[] activeDays, int hour, int minute, int command){
        this.id = id;
        this.isOn = isOn;
        this.singleActivation = singleActivation;
        this.activeDays = activeDays;
        this.hour = hour;
        this.minute = minute;
        this.command = command;
        this.title = new String();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOn() {
        return isOn;
    }

    public boolean isSingleActivation() {
        return singleActivation;
    }

    public int getActiveDays() {

        int result=0;
        int power = 1;
        for(int i=0; i<7; i++){
            if(activeDays[i])
                result += power;
            power *= 2;
        }
        return result;
    }

    public boolean[] getDaysArray(){
        return this.activeDays;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getCommand(){
        return this.command;
    }

    //получение времени в формате ЧЧ:ММ
    public String getTime() {
        String hourValue = new String();
        if(this.hour<10){
            hourValue = "0"+String.valueOf(this.hour);
        }else{
            hourValue = String.valueOf(this.hour);
        }

        String minuteValue = new String();
        if(this.minute<10){
            minuteValue = "0"+String.valueOf(this.minute);
        }else{
            minuteValue = String.valueOf(this.minute);
        }

        return hourValue+":"+minuteValue;
    }
}
