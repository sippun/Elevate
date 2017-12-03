package com.example.android.elevate;

/**
 * Created by Joel on 10/28/2017.
 */

public abstract class ToDoItem {
    String name, id;
    boolean done;

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void finish(){
        done = true;
    }
}

