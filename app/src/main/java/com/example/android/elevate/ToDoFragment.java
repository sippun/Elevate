package com.example.android.elevate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;

public class ToDoFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<ToDoItem> myDataset;
    private static final String userDataPath = "bethsTestTree";

    String day_year;
    Calendar cal;
    public MainActivity main;

    @SuppressLint("ValidFragment")
    public ToDoFragment(int day, int month, int year){
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        day_year =  cal.get(Calendar.DAY_OF_YEAR) + ":" +year;
    }

    public ToDoFragment(){
        cal = Calendar.getInstance();
        day_year = cal.get(Calendar.DAY_OF_YEAR) +":" +
                cal.get(Calendar.YEAR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_todo);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
  
        myDataset = new ArrayList<ToDoItem>();
        final Calendar cal = Calendar.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference tasks = database.getReference(userDataPath+"/tasks");

        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    ToDoItem item = task.getValue(ToDoItem.class);
                    myDataset.add(new ToDoItem(item.name, cal, cal));
                    Log.d("TaskList", item.name);
                }

                mAdapter = new ToDoAdapter(myDataset);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TaskList", "Something went wrong with getting the existing tasks");
            }
        });

        //initiate dummy list with current time as start/end time
        ArrayList<ToDoItem> myDataset = new ArrayList<>();

        main = (MainActivity)getActivity();

        if(main.myDataMap.get(day_year) == null) {
            main.myDataMap.put(day_year, myDataset);
        }

        mAdapter = new ToDoAdapter(main.myDataMap.get(day_year));
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    //insert to-do item into lists from startTime to endTime
    public void insertItem(String name, Calendar startTime, Calendar endTime, ArrayList<Integer> recurringDays){

        //pointer starts at startTime and increments towards endTime
        Calendar pointer = (Calendar)startTime.clone();
        while(pointer.before(endTime)) {

            //check if pointer's weekday is set to recur
            if (checkWeekDayRecur(pointer, recurringDays)) {
                //hashmap key = "day:year"
                //if hashmap doesn't contain key, insert item into a new list
                //otherwise insert item into existing list, then increment pointer by 1 day
                String key = pointer.get(Calendar.DAY_OF_YEAR) + ":" + pointer.get(Calendar.YEAR);
                if (main.myDataMap.get(key) != null) {
                    main.myDataMap.get(key).add(new ToDoItem(name, startTime, endTime, recurringDays));
                    mAdapter.notifyDataSetChanged();
                } else {
                    ArrayList<ToDoItem> myDataset = new ArrayList<>();
                    myDataset.add(new ToDoItem(name, startTime, endTime, recurringDays));
                    main.myDataMap.put(key, myDataset);
                    mAdapter.notifyDataSetChanged();
                }
                pointer.add(Calendar.DATE, 1);
            }
        }
    }

    //return true if recurList has pointer's weekday flagged as 1
    public boolean checkWeekDayRecur(Calendar pointer, ArrayList<Integer> recurringDays){
        switch(pointer.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY: return recurringDays.get(0)==1;
            case Calendar.TUESDAY: return recurringDays.get(1)==1;
            case Calendar.WEDNESDAY: return recurringDays.get(2)==1;
            case Calendar.THURSDAY: return recurringDays.get(3)==1;
            case Calendar.FRIDAY: return recurringDays.get(4)==1;
            case Calendar.SATURDAY: return recurringDays.get(5)==1;
            case Calendar.SUNDAY: return recurringDays.get(6)==1;
            default: return false;
        }
    }
}