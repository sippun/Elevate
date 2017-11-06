package com.example.android.elevate;

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

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Joel on 10/20/2017.
 */

public class ToDoFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<ToDoItem> myDataset;
    private static final String userDataPath = "bethsTestTree";

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


        return rootView;
    }

    public void insertItem(String name, Calendar startTime, Calendar endTime){
        myDataset.add(new ToDoItem(name, startTime, endTime));
        mAdapter.notifyItemInserted(myDataset.size() - 1);
    }
}