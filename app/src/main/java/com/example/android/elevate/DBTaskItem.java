package com.example.android.elevate;

/**
 * Created by Joel on 11/6/2017.
 */

public class DBTaskItem {
    public String name;
    public long startTime, endTime; //Unix timestamp

    public DBTaskItem(String name, long startTime, long endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
