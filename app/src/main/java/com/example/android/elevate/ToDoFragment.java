package com.example.android.elevate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

public class ToDoFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
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

    //insert to-do item into lists from startTime to endTime
    public void insertItem(String name, Calendar startTime, Calendar endTime){

        //pointer starts at startTime and increments towards endTime
        Calendar pointer = (Calendar)startTime.clone();
        while(pointer.before(endTime)){

            //hashmap key = "day:year"
            String key = pointer.get(Calendar.DAY_OF_YEAR)+":"+pointer.get(Calendar.YEAR);
            if(main.myDataMap.get(key)!=null){
                main.myDataMap.get(key).add(new ToDoItem(name, startTime, endTime));
                mAdapter.notifyDataSetChanged();
            }else{
                ArrayList<ToDoItem> myDataset = new ArrayList<>();
                myDataset.add(new ToDoItem(name, startTime, endTime));
                main.myDataMap.put(key, myDataset);
                mAdapter.notifyDataSetChanged();
            }
            pointer.add(Calendar.DATE, 1);
        }
    }
}