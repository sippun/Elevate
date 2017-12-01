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

import java.util.ArrayList;
import java.util.Calendar;

public class ToDoFragment extends Fragment {
    private static final String TAG = "ToDoFragTag";
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

        main = (MainActivity)getActivity();
        DataBase dataBase = main.database;

        Log.d(TAG, dataBase.todaysItemsList.toString());
        mAdapter = new ToDoAdapter(dataBase);
        mRecyclerView.setAdapter(mAdapter);
        dataBase.refreshTodaysList(day_year, mAdapter);

        mAdapter.notifyDataSetChanged();

        Log.d(TAG, dataBase.mapOfArraysToString(dataBase.donenessHistory) );
        Log.d(TAG, dataBase.moodHistory.toString() );
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

}