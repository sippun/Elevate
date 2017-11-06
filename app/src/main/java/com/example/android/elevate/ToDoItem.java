package com.example.android.elevate;

/**
 * Created by Joel on 10/28/2017.
 */

import java.util.Calendar;

public class ToDoItem {
    String name;
    Calendar startTime, endTime;
    boolean done;

    //boolean mon, tue, wed, thur, fri, sat, sun;

    public ToDoItem(String name, Calendar startTime, Calendar endTime){
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        done = false;
    }

    public String getName(){
        return name;
    }
    public Calendar getStartTime(){ return startTime;}
    public Calendar getEndTime(){ return endTime;}
    public void finish(){done = true;}
}