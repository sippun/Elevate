package com.example.android.elevate;

/**
 * Created by Joel on 10/28/2017.
 */

import java.util.ArrayList;
import java.util.Calendar;

public class ToDoItem {
    String name, id;
    Calendar startTime, endTime;
    boolean done;
    ArrayList<Integer> recurringDays;

    public ToDoItem(String name, Calendar startTime, Calendar endTime, ArrayList<Integer> recurringDays){

        this.name = name;
        this.id = name.toLowerCase()+"ID"; //eventually should use actual push function to create unique ids
        this.startTime = startTime;
        this.endTime = endTime;
        done = false;
        this.recurringDays = new ArrayList<>(recurringDays);
    }

    public String getName(){
        return name;
    }
    public String getId(){return id;}
    public Calendar getStartTime(){ return startTime;}
    public Calendar getEndTime(){ return endTime;}

    public void finish(){done = true;}
}

