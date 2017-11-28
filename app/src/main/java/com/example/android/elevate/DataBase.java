package com.example.android.elevate;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Beth on 11/16/2017.
 */

public class DataBase {
    private final String TAG="DataBaseTag";
    private FirebaseUser user;
    private String userDataPath = "users/guest";
    public HashMap<String,ArrayList<ToDoItem>> dayToItemsMap = new HashMap<>();
    public ArrayList<ToDoItem> activeItemsList = new ArrayList<ToDoItem>();

    public DataBase(){}


    public void logIntoFirebase() {
//        user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (user != null) {
//            userDataPath = "users/" + user.getUid();
//
//        }
    }

    public void addItemFromFirebaseToToDoFragment(final RecyclerView.Adapter mAdapter){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference tasks = database.getReference(userDataPath+"/tasks");

        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    ToDoItem item = new ToDoItem(task.getValue(DBTaskItem.class));
                    item.setId(task.getKey());

                    if(item != null) {
                        Log.d(TAG+"TaskList", item.name);
                        insertItem(item);
                    }else{
                        Log.d(TAG+"TaskList", "item = null");
                    }
                }

                mAdapter.notifyDataSetChanged();
                Log.d(TAG+"Final", dayToItemsMap.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"TaskList", "Something went wrong with getting the existing tasks");
            }

        });

    }


    //insert to-do item into lists from startTime to endTime
    public void insertNewItem(String name, Calendar startTime, Calendar endTime,boolean[] recurringDays){
        ToDoItem item = new ToDoItem(name, startTime, endTime, recurringDays);

        if(user != null) {
            Log.d(TAG+"addTask", item.toString());
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(user.getUid())
                    .child("tasks");
            String key = ref.push().getKey();
            item.setId(key);
            ref.child("/"+key).setValue( item.createDataBaseEntry());

            insertItem(item);
        }else{
            Log.d(TAG+"addTask", "user =null");
        }

    }

    public void insertItem(ToDoItem item){
        //pointer starts at startTime and increments towards endTime
        Calendar pointer = (Calendar)item.getStartTime().clone();
        while(pointer.compareTo(item.getEndTime()) <= 0) { //before or at this date
            //check if pointer's weekday is to be recurred
            if (checkWeekDayRecur(pointer, item.getRecurringDays())) {
                //hashmap key = "day:year"
                //if hashmap doesn't contain key, insert item into a new list
                //otherwise insert item into existing list, then increment pointer by 1 day
                String key = pointer.get(Calendar.DAY_OF_YEAR) + ":" + pointer.get(Calendar.YEAR);

                ArrayList<ToDoItem> toDoList = dayToItemsMap.get(key);
                if (toDoList != null) {
                    if (!itemInList(toDoList, item.id)) {
                        toDoList.add(item);
                    }
                } else {
                    toDoList = new ArrayList<>();
                    toDoList.add(item);
                    dayToItemsMap.put(key, toDoList);
                }
                Log.d(TAG+" afterInsert", dayToItemsMap.toString());
            }
            pointer.add(Calendar.DATE, 1);
        }
    }

    //return true if recurList has pointer's weekday flagged as true
    public boolean checkWeekDayRecur(Calendar pointer, boolean[] recurringDays){
        switch(pointer.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY: return recurringDays[0];
            case Calendar.TUESDAY: return recurringDays[1];
            case Calendar.WEDNESDAY: return recurringDays[2];
            case Calendar.THURSDAY: return recurringDays[3];
            case Calendar.FRIDAY: return recurringDays[4];
            case Calendar.SATURDAY: return recurringDays[5];
            case Calendar.SUNDAY: return recurringDays[6];
            default: return false;
        }
    }

    boolean itemInList(List<ToDoItem> toDoList, String id){
        Log.d("LoopStart", toDoList.toString());
        for (ToDoItem item: toDoList) {
            Log.d("LoopItter", item.getId() +" vs "+ id);
            if(item.getId() == id) {
                Log.d("LoopEnd", "true");
                return true;
            }
        }
        Log.d("LoopEnd", "false");
        return false;
    }
}
