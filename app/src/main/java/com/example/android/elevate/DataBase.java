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

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Beth on 11/16/2017.
 */

public class DataBase {
    private final String TAG="DataBaseTag";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user;
    private String userDataPath = "users/guest";
    public HashMap<String,ArrayList<ToDoItem>> dayToItemsMap = new HashMap<>();
    public ArrayList<ToDoItem> todaysItemsList = new ArrayList<ToDoItem>();

    public DataBase(){}


    public void logIntoFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userDataPath = "users/" + user.getUid();

        }
    }

    //add to-do item to list of tasks on firebase
    public void addNewItem(String name, Calendar startTime, Calendar endTime,boolean[] recurringDays){
        ToDoItem item = new ToDoItem(name, startTime, endTime, recurringDays);

        if(user != null) {
            Log.d(TAG+"addTask", item.toString());
            DatabaseReference ref= database.getReference(userDataPath+"/tasks");
            String key = ref.push().getKey();
            item.setId(key);
            ref.child("/"+key).setValue( item.createDataBaseEntry() );

        }else{
            Log.d(TAG+"addTask", "user =null");
        }

    }

    //setup todaysItemsList for use
    public void refreshTodaysList(final String today, final RecyclerView.Adapter mAdapter){
        firebaseListToCalendar("/tasks", "/calendar/"+today, today, mAdapter);
        //including this seems to dupicate all tasks, despite there not being any habits yet. Not sure why.
        //firebaseListToCalendar("/habits", "/calendar/"+today, today, mAdapter);
    }

    private void firebaseListToCalendar(String from, String to, final String today, final RecyclerView.Adapter mAdapter){
        final DatabaseReference dataDay = database.getReference(userDataPath+to);
        DatabaseReference itemList = database.getReference(userDataPath+from);

        itemList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    DBTaskItem item = task.getValue(DBTaskItem.class);
                    if(item != null && beforeOrOnDate(today, longCalAsString(item.endTime))) {
                        dataDay.child("/" + task.getKey()).setValue(item);
                    }
                }
                updateLocal(today, mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"TaskList", "Something went wrong with populating a day");
            }
        });
    }
    private void updateLocal(final String today, final RecyclerView.Adapter mAdapter){
        final DatabaseReference tasks = database.getReference(userDataPath+"/calendar/"+today);
        Log.d(TAG, today);
        todaysItemsList.clear();

        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    ToDoItem item = new ToDoItem(task.getValue(DBTaskItem.class));
                    item.setId(task.getKey());

                    if(item != null) {
                        Log.d(TAG+"refresh", item.name);

                        todaysItemsList.add(item);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        Log.d(TAG+"refresh", "item = null");
                    }
                }

                mAdapter.notifyDataSetChanged();
                Log.d(TAG+"Refresh", todaysItemsList.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"TaskList", "Something went wrong with getting the existing tasks");
            }
        });
    }





    private int getYear(String today){
        String[] part = today.split(":");
        return Integer.parseInt(part[1]);
    }
    private int getDay(String today){
        String[] part = today.split(":");
        return Integer.parseInt(part[0]);
    }

    private String longCalAsString(long date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.DAY_OF_YEAR) + ":" +cal.get(Calendar.YEAR);
    }

    private boolean beforeOrOnDate(String today, String reference){
        int todayDay = getDay(today);
        int todayYear = getYear(today);
        int refDay = getDay(reference);
        int refYear = getYear(reference);

        if(todayYear < refYear){
            return true;
        }else if( todayYear == refYear){
            if(todayDay <= refDay){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
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
