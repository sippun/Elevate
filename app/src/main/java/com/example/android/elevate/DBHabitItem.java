package com.example.android.elevate;

/**
 * Created by Joel on 11/16/2017.
 */

public class DBHabitItem {
    public String name;
    public boolean[] days;

    public DBHabitItem(String name, boolean[] recurringDays) {
        this.name = name;
        days = recurringDays;
    }
}
