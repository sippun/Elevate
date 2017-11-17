package com.example.android.elevate;

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
    private List<ToDoItem> items;
    private static final DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    private static final String userDataPath = "user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ToDoAdapter(List<ToDoItem> items) {
        this.items = items;
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
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("addTask", "onBind runs");
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ToDoItem item = items.get(position);

        final String itemID = item.getId();

        TextView textView = holder.nameTextView;
        textView.setText(item.getName() + "  "+
                sdf.format(item.getStartTime().getTime()) + "  "+
                sdf.format(item.getEndTime().getTime()));

        Log.d("addTask", itemID);

        //final ToggleButton toggleButton = holder.nameToggleButton;
        //FirebaseDatabase database = FirebaseDatabase.getInstance();

//        final DatabaseReference doneness = database.getReference(userDataPath+"/tasks/"+itemID+"/done");
//         //Pull initial state from the database:
//         doneness.addListenerForSingleValueEvent(new ValueEventListener() {
//             @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                 toggleButton.setChecked((boolean) dataSnapshot.getValue());
//            }
//             @Override
//             public void onCancelled(DatabaseError databaseError) {
//                     Log.d("Toggle", "Something went wrong with getting doneness of "+itemID);
//             }
//         });
//
//         //Set up toggle to alter the database:
//         toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                 doneness.setValue(isChecked);
//                 Log.d("Toggle", itemID);
//             }
//         });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }


}