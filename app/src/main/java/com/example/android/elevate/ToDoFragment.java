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
import java.util.List;

/**
 * Created by Joel on 10/20/2017.
 */

public class ToDoFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ToDoItem> myDataset;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_todo);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDataset = new ArrayList<ToDoItem>();
        Calendar cal = Calendar.getInstance();
        myDataset.add(new ToDoItem("Eat", cal, cal));
        myDataset.add(new ToDoItem("Sleep", cal ,cal));
        myDataset.add(new ToDoItem("Code", cal, cal));
        myDataset.add(new ToDoItem("Repeat", cal, cal));

        mAdapter = new ToDoAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    public void insertItem(String name, Calendar startTime, Calendar endTime){
        myDataset.add(new ToDoItem(name, startTime, endTime));
        mAdapter.notifyItemInserted(myDataset.size() - 1);
    }
}