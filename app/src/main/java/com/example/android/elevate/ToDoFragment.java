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
import java.util.HashMap;
import java.util.List;

/**
 * Created by Joel on 10/20/2017.
 */

public class ToDoFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String day_year;

    @SuppressLint("ValidFragment")
    public ToDoFragment(int day, int month, int year){
        Calendar tmp = Calendar.getInstance();
        tmp.set(Calendar.DAY_OF_MONTH, day);
        tmp.set(Calendar.MONTH, month);
        day_year = tmp.DAY_OF_YEAR + ":" + year;
    }

    public ToDoFragment(){
        Calendar cal = Calendar.getInstance();
        day_year = cal.DAY_OF_YEAR+":"+cal.YEAR;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_todo);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Calendar cal = Calendar.getInstance();
        ArrayList<ToDoItem> myDataset = new ArrayList<>();
        myDataset.add(new ToDoItem("Eat", cal, cal));
        myDataset.add(new ToDoItem("Sleep", cal ,cal));
        myDataset.add(new ToDoItem("Code", cal, cal));
        myDataset.add(new ToDoItem("Repeat", cal, cal));

        getActivity().myDataMap.put(day_year, myDataset);

        mAdapter = new ToDoAdapter(MainActivity.myDataMap.get(day_year));
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    public void insertItem(String name, Calendar startTime, Calendar endTime){

        for(int i = startTime.YEAR; i <= endTime.YEAR; i++){
            for(int j=startTime.DAY_OF_YEAR; i<=endTime.DAY_OF_YEAR;i++){
                String key = i+":"+j;
                if(MainActivity.myDataMap.get(key)!=null){
                    MainActivity.myDataMap.get(key).add(new ToDoItem(name, startTime, endTime));
                }
                else{
                    ArrayList<ToDoItem> myDataset = new ArrayList<>();
                    myDataset.add(new ToDoItem(name, startTime, endTime));
                    MainActivity.myDataMap.put(key, myDataset);
                }
            }
        }
        //mAdapter.notifyItemInserted(myDataset.size() - 1);
    }
}