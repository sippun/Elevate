package com.example.android.elevate;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ToggleButton;

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
    private String openDay = "000:0000";

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
        }

    }

    //setup todaysItemsList for use
    public void refreshTodaysList(final String today, final RecyclerView.Adapter mAdapter){
        openDay = today;
        todaysItemsList.clear();
        mAdapter.notifyDataSetChanged();
        getExistingList(today, mAdapter);

    }

    private void getExistingList(final String today, final RecyclerView.Adapter mAdapter){
        DatabaseReference firebaseCalendar = database.getReference(userDataPath+"/calendar/" +today);

        firebaseCalendar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<ToDoItem> fireBaseList = new ArrayList<ToDoItem>();

                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    DBTaskItem item = task.getValue(DBTaskItem.class);
                    if(item != null ) {
                        ToDoItem tditem = new ToDoItem(item);
                        tditem.setId(task.getKey());
                        fireBaseList.add(tditem);
                    }
                }
                firebaseListToCalendar("/tasks", "/calendar/"+today, today, fireBaseList,mAdapter);
                //including this seems to duplicate all tasks, despite there not being any habits yet.
                // Probably because it is adding from the same list two separate times simultaneously?.
                // firebaseListToCalendar("/habits", "/calendar/"+today, today, fireBaseList, mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"TaskList", "Something went wrong with populating a day");
            }
        });
    }

    private void firebaseListToCalendar(String from, String to, final String today,
                                        final ArrayList<ToDoItem> fireBaseList,
                                        final RecyclerView.Adapter mAdapter){
        final DatabaseReference dataDay = database.getReference(userDataPath+to);
        final DatabaseReference itemList = database.getReference(userDataPath+from);

        itemList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    DBTaskItem item = task.getValue(DBTaskItem.class);
                    if(item != null &&
                            beforeOrOnDate(longCalAsString(item.startTime), today) &&
                            beforeOrOnDate(today, longCalAsString(item.endTime)) &&
                            !itemInList(fireBaseList, task.getKey())) {

                        Log.d(TAG, task.getKey() + " " + fireBaseList.toString());
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


        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    if(task.getValue(DBTaskItem.class) != null) {
                        ToDoItem item = new ToDoItem(task.getValue(DBTaskItem.class));
                        item.setId(task.getKey());
                        todaysItemsList.add(item);
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

    public void updateDoneness( String itemID, boolean done){
        //update Firebase:
        String path = userDataPath+"/calendar/"+ openDay+"/"+itemID+"/done";
        database.getReference(path).setValue(done);
        //update Local: (not strictly necessary, but there is a noticeable lag in display of recently changed values otherwise)
        for (ToDoItem item: todaysItemsList) {
            if(item.id.equals(itemID)) {
                item.done = done;
            }
        }
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
        for (ToDoItem item: toDoList) {
            if(item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
