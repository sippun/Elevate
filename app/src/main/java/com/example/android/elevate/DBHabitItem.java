package com.example.android.elevate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joel on 11/16/2017.
 */

public class DBHabitItem {
    public String name;
    List<Boolean> recurringDays;

    public DBHabitItem(String name, boolean[] days) {
        this.name = name;
        recurringDays = new ArrayList<>();
        for (boolean b : days) {
            recurringDays.add(b);
        }
    }
}
