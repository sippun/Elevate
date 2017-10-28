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
import java.util.List;

/**
 * Created by Joel on 10/20/2017.
 */

public class ToDoFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ToDoItem> myDataset;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_todo);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDataset = new ArrayList<ToDoItem>();
        myDataset.add(new ToDoItem("Eat"));
        myDataset.add(new ToDoItem("Sleep"));
        myDataset.add(new ToDoItem("Code"));
        myDataset.add(new ToDoItem("Repeat"));

        mAdapter = new ToDoAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }
}