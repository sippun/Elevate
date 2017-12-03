package com.example.android.elevate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Joel on 12/2/2017.
 */

public class TaskItem {
    String name, id;
    long startTime, endTime; //Unix timestamp
    boolean done;
    boolean[] recurringDays;

    public TaskItem(DBTaskItem item){
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

    public TaskItem(String name, Calendar startTime, Calendar endTime, boolean[] recurringDays){

        this.name = name;
        this.id = name.toLowerCase()+"ID"; //eventually should use actual push function to create unique ids
        this.startTime = startTime.getTimeInMillis();
        this.endTime = endTime.getTimeInMillis();
        done = false;
        this.recurringDays = recurringDays;
    }

    public TaskItem(String name, long startTime, long endTime, boolean[] recurringDays){

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

    public void setId(String id) {this.id = id;}
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
