package com.example.android.elevate;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Joel on 10/28/2017.
 */

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private DataBase dataBase;
    private List<ToDoItem> items;
    public static CardView card;
    //private static final DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ToggleButton nameToggleButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.todo_name);
            nameToggleButton = itemView.findViewById(R.id.todo_toggle);
            card = itemView.findViewById(R.id.card);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ToDoAdapter(DataBase dataBase) {
        this.dataBase = dataBase;
        this.items = dataBase.todaysItemsList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ToDoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_todo, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("addTask", "onBind runs");
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ToDoItem item = items.get(position);

        final String itemID = item.getId();

        TextView textView = holder.nameTextView;
        textView.setText(item.getName());

        Log.d("addTask", itemID);

        final ToggleButton toggleButton = holder.nameToggleButton;
        toggleButton.setChecked(item.done);

        //if(item.done) card.setCardBackgroundColor(R.color.cardview_light_background);

         //Set up toggle to alter the database:
         toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 //if (isChecked) {
                 //    card.setCardBackgroundColor(R.color.cardview_light_background);
                 //}else {
                 //    card.setCardBackgroundColor(R.color.lightGray);
                 //}

                 dataBase.updateDoneness(itemID, isChecked);
                 Log.d("Toggle", itemID);
             }
         });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }


}