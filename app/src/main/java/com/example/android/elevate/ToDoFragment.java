package com.example.android.elevate;

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

/**
 * Created by Joel on 10/20/2017.
 */

public class ToDoFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public HashMap<ArrayList<Integer>,ArrayList<ToDoItem>> myDataMap;

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

        ArrayList<Integer> day_month_year = new ArrayList<Integer>();
        day_month_year.add(cal.get(Calendar.DAY_OF_MONTH));
        day_month_year.add(cal.get(Calendar.MONTH));
        day_month_year.add(cal.YEAR);

        myDataMap.put({cal.DAY_OF_YEAR, cal.YEAR}, )

        mAdapter = new ToDoAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    public void insertItem(String name, Calendar startTime, Calendar endTime){
        myDataset.add(new ToDoItem(name, startTime, endTime));
        mAdapter.notifyItemInserted(myDataset.size() - 1);
    }
}