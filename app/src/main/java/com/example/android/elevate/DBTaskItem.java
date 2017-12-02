package com.example.android.elevate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joel on 11/6/2017.
 */

public class DBTaskItem {
    public String name;
    public long startTime, endTime; //Unix timestamp
    boolean done;
    List<Boolean> recurringDays;
    int notifId;

    public DBTaskItem(){
        recurringDays = new ArrayList<Boolean>();
        for(int i = 0; i< 7; i++){
            recurringDays.add(true);
        }
    }

    public DBTaskItem(String name, long startTime, long endTime, boolean done, List<Boolean> recurringDays, int notifId) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.done = done;
        this.recurringDays = recurringDays;
        this.notifId = notifId;
    }
}
