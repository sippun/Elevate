package com.example.android.elevate;

/**
 * Created by Joel on 10/28/2017.
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ToDoItem {
    String name, id;
    long startTime, endTime; //Unix timestamp
    boolean done;
    boolean[] recurringDays;

    public ToDoItem(DBTaskItem item){
        name = item.name;
        this.id = item.name.toLowerCase()+"ID"; //eventually should use actual push function to create unique ids
        this.startTime = item.startTime;
        this.endTime = item.endTime;
        done = item.done;

        recurringDays= new boolean[7];
        for(int i=0; i<7; i++){
            recurringDays[i]= item.recurringDays.get(i);
        }
    }

    public ToDoItem(String name, Calendar startTime, Calendar endTime, boolean[] recurringDays){

        this.name = name;
        this.id = name.toLowerCase()+"ID"; //eventually should use actual push function to create unique ids
        this.startTime = startTime.getTimeInMillis();
        this.endTime = endTime.getTimeInMillis();
        done = false;
        this.recurringDays = recurringDays;
    }

    public ToDoItem(String name, long startTime, long endTime, boolean[] recurringDays){

        this.name = name;
        this.id = name.toLowerCase()+"ID"; //eventually should use actual push function to create unique ids
        this.startTime = startTime;
        this.endTime = endTime;
        done = false;
        this.recurringDays = recurringDays;
    }

    public String getName(){
        return name;
    }
    public String getId(){return id;}
    public Calendar getStartTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        return cal;
    }
    public Calendar getEndTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endTime);
        return cal;
    }
    public boolean[] getRecurringDays(){ return recurringDays; }


    public void finish(){done = true;}


    public String toString(){
        return name+ ": " +id+" " + done;
    }

    public List<Boolean> arrayToList(boolean[] recurringDays){
        List<Boolean> newList = new ArrayList<Boolean>();
        for (boolean bool:
             recurringDays) {
            newList.add(bool);
        }
        return newList;
    }

    public DBTaskItem createDataBaseEntry(){
        return new DBTaskItem(name, startTime, endTime, done, arrayToList(recurringDays));
    }
}

