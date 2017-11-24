package com.example.android.elevate;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
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
    private static final String userDataPath = "users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid();
    public HashMap<String,ArrayList<ToDoItem>> dayToItemsMap = new HashMap<>();
    private HashMap<String,RecyclerView.Adapter> dayToAdapterMap = new HashMap<>();
    public ArrayList<ToDoItem> activeItemsList = new ArrayList<ToDoItem>();

    public DataBase(){}

    public void registerAdaptor(String day, RecyclerView.Adapter mAdaptor){
        dayToAdapterMap.put(day, mAdaptor);
    }

    public void addItemFromFirebaseToToDoFragment(final RecyclerView.Adapter mAdapter){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference tasks = database.getReference(userDataPath+"/tasks");

        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    ToDoItem item = new ToDoItem(task.getValue(DBTaskItem.class));

                    if(item != null) {
                        insertItem(item/*, mAdapter*/);
                        Log.d(TAG+"TaskList", item.name);
                    }else{
                        Log.d(TAG+"TaskList", "item = null");
                    }
                }

                mAdapter.notifyDataSetChanged();
                Log.d(TAG, dayToItemsMap.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"TaskList", "Something went wrong with getting the existing tasks");
            }
        });
    }


    //insert to-do item into lists from startTime to endTime
    public void insertItem(String name, Calendar startTime, Calendar endTime,boolean[] recurringDays/*, RecyclerView.Adapter mAdapter*/){
        //pointer starts at startTime and increments towards endTime
        Calendar pointer = (Calendar)startTime.clone();
        while(pointer.compareTo(endTime) <= 0) { //before or at this date
            //check if pointer's weekday is to be recurred
            if (checkWeekDayRecur(pointer, recurringDays)) {

                //hashmap key = "day:year"
                //if hashmap doesn't contain key, insert item into a new list
                //otherwise insert item into existing list, then increment pointer by 1 day
                String key = pointer.get(Calendar.DAY_OF_YEAR) + ":" + pointer.get(Calendar.YEAR);
                if (dayToItemsMap.get(key) != null) {
                    dayToItemsMap.get(key).add(new ToDoItem(name, startTime, endTime, recurringDays));
                } else {
                    ArrayList<ToDoItem> myDataset = new ArrayList<>();
                    myDataset.add(new ToDoItem(name, startTime, endTime, recurringDays));
                    dayToItemsMap.put(key, myDataset);
                }
                Log.d(TAG+" afterInsert", dayToItemsMap.toString());
                //mAdapter.notifyDataSetChanged();
            }
            pointer.add(Calendar.DATE, 1);
        }
    }

    public void insertItem(ToDoItem item/*, RecyclerView.Adapter mAdapter*/){
        insertItem(item.name, item.getStartTime(), item.getEndTime(), item.getRecurringDays()/*, mAdapter*/);
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
}
