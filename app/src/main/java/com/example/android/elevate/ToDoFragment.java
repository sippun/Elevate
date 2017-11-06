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
import java.util.HashMap;
import java.util.List;


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
        myDataset.add(new ToDoItem("Eat", cal, cal));
        myDataset.add(new ToDoItem("Sleep", cal ,cal));
        myDataset.add(new ToDoItem("Code", cal, cal));
        myDataset.add(new ToDoItem("Repeat", cal, cal));

        main = (MainActivity)getActivity();

        if(main.myDataMap.get(day_year) == null) {
            main.myDataMap.put(day_year, myDataset);
        }

        mAdapter = new ToDoAdapter(main.myDataMap.get(day_year));
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public void insertItem(String name, Calendar startTime, Calendar endTime){

        for(int i = startTime.get(Calendar.YEAR); i <= endTime.get(Calendar.YEAR); i++){
            for(int j=startTime.get(Calendar.DAY_OF_YEAR); j<=endTime.get(Calendar.DAY_OF_YEAR);j++){

                String key = j+":"+i;

                if(main.myDataMap.get(key)!=null){
                    main.myDataMap.get(key).add(new ToDoItem(name, startTime, endTime));
                    mAdapter.notifyDataSetChanged();
                }else{
                    ArrayList<ToDoItem> myDataset = new ArrayList<>();
                    myDataset.add(new ToDoItem(name, startTime, endTime));
                    main.myDataMap.put(key, myDataset);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}