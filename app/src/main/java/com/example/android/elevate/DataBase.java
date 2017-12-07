package com.example.android.elevate;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Beth on 11/16/2017.
 */

public class DataBase {
    private final String TAG="DataBaseTag";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user;
    private String userDataPath = "users/guest";
    public ArrayList<ToDoItem> todaysItemsList = new ArrayList<ToDoItem>();
    public HashMap<String, Integer[]> donenessHistory = new HashMap<>();
    public HashMap<String, Integer> moodHistory = new HashMap<>();
    private String openDay = "000:0000";

    public DataBase(){}


    public void logIntoFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userDataPath = "users/" + user.getUid();
            setupHistory();
        }
    }


    private void setupHistory(){
        setupProductivityHistory();
        setupMoodHistory();
    }

    //add to-do item to list of tasks on firebase
    public void addNewTask(String name, Calendar startTime, Calendar endTime,boolean[] recurringDays, int notifId){
        if(startTime.get(Calendar.DATE) == endTime.get(Calendar.DATE)){
            recurringDays[getDayOfWeek(startTime)] = true;
        }

        ToDoItem item = new ToDoItem(name, startTime, endTime, recurringDays, notifId);
        if(user != null) {
            Log.d(TAG+"addTask", item.toString());
            DatabaseReference ref= database.getReference(userDataPath+"/tasks");
            String key = ref.push().getKey();
            item.setId(key);
            ref.child("/"+key).setValue( item.createDataBaseEntry() );
        }
    }

    public void addNewHabit(String name, boolean[] recurringDays, int notifId) {
        Calendar startTime, endTime;
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        endTime.add(Calendar.DATE, 30);
        startTime.setTime(startTime.getTime());
        endTime.setTime(endTime.getTime());

        ToDoItem item = new ToDoItem(name, startTime, endTime, recurringDays, notifId);

        if(user != null) {
            DatabaseReference ref= database.getReference(userDataPath+"/tasks"); //lol
            String key = ref.push().getKey();
            item.setId(key);
            ref.child("/"+key).setValue( item.createDataBaseEntry() );
        }
    }

    private int getDayOfWeek(Calendar day){
        //add 5 and mod 7 to handle monday being the first day of the week.
        return (day.get(Calendar.DAY_OF_WEEK)+5) % 7;
    }

    //setup todaysItemsList for use
    public void refreshTodaysList(final String today, final RecyclerView.Adapter mAdapter){
        openDay = today;
        todaysItemsList.clear();
        mAdapter.notifyDataSetChanged();
        getExistingList(today, mAdapter);

    }

    private void getExistingList(final String today, final RecyclerView.Adapter mAdapter){
        DatabaseReference firebaseCalendar = database.getReference(userDataPath+"/calendar/" +today);

        firebaseCalendar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<ToDoItem> fireBaseList = new ArrayList<ToDoItem>();

                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    DBTaskItem item = task.getValue(DBTaskItem.class);
                    if(item != null ) {
                        ToDoItem tditem = new ToDoItem(item);
                        tditem.setId(task.getKey());
                        fireBaseList.add(tditem);
                    }
                }
                firebaseListToCalendar("/tasks", "/calendar/"+today, today, fireBaseList,mAdapter);
                //including this seems to duplicate all tasks, despite there not being any habits yet.
                // Probably because it is adding from the same list two separate times simultaneously?.
                //firebaseListToCalendar("/habits", "/calendar/"+today, today, fireBaseList, mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"TaskList", "Something went wrong with populating a day");
            }
        });
    }

    private void firebaseListToCalendar(String from, String to, final String today,
                                        final ArrayList<ToDoItem> fireBaseList,
                                        final RecyclerView.Adapter mAdapter){
        final DatabaseReference dataDay = database.getReference(userDataPath+to);
        final DatabaseReference itemList = database.getReference(userDataPath+from);

        itemList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    DBTaskItem item = task.getValue(DBTaskItem.class);
                    if(item != null &&
                            beforeOrOnDate(longCalAsString(item.startTime), today) &&
                            beforeOrOnDate(today, longCalAsString(item.endTime)) &&
                            checkWeekDayRecur(stringCalAsCalendar(today), new ToDoItem(item).recurringDays) &&
                            !itemInList(fireBaseList, task.getKey())) {

                        Log.d(TAG, task.getKey() + " " + fireBaseList.toString());
                        dataDay.child("/" + task.getKey()).setValue(item);
                    }
                }
                updateLocal(today, mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"TaskList", "Something went wrong with populating a day");
            }
        });
    }
    private void updateLocal(final String today, final RecyclerView.Adapter mAdapter){
        final DatabaseReference tasks = database.getReference(userDataPath+"/calendar/"+today);

        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot task : dataSnapshot.getChildren()) {
                    if(task.getValue(DBTaskItem.class) != null) {
                        ToDoItem item = new ToDoItem(task.getValue(DBTaskItem.class));
                        item.setId(task.getKey());
                        todaysItemsList.add(item);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"TaskList", "Something went wrong with getting the existing tasks");
            }
        });
    }

    public void updateDoneness( String itemID, boolean done){
        //update Firebase:
        String path = userDataPath+"/calendar/"+ openDay+"/"+itemID+"/done";
        database.getReference(path).setValue(done);
        //update Local: (not strictly necessary, but there
        // is a noticeable lag in display of recently changed values otherwise)
        for (ToDoItem item: todaysItemsList) {
            if(item.id.equals(itemID)) {
                item.done = done;
                if(done && item.getNotifId()>0 ){
                    //if done is true, cancel all notifications related to this item
                    MainActivity.cancel(item.getNotifId());
                }
            }
        }
    }

    private void setupProductivityHistory(){
        String path = userDataPath + "/calendar/";
        int lastyear = Calendar.getInstance().get(Calendar.YEAR)+1;
        int numDaysInYear = 366;

        for(int year = 2017; year <= lastyear; year++ ){
            for(int day= 1; day<=numDaysInYear; day++){
                final String key = day+":"+year;
                database.getReference(path+key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            donenessHistory.put(key, getNumTasks(dataSnapshot));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Something went wrong with getting the number of tasks");
                    }
                });
            }
        }
    }

    private Integer[] getNumTasks(DataSnapshot dataSnapshot){
        int totalTasks = (int) dataSnapshot.getChildrenCount();
        int completedTasks = 0;
        for (DataSnapshot task: dataSnapshot.getChildren()) {
            DBTaskItem item = task.getValue(DBTaskItem.class);
            if (item != null){
                if(item.done) {
                    completedTasks++;
                }
            }else{
                totalTasks = Math.max(totalTasks-1, 0);
            }
        }

        Integer[] array = {completedTasks, totalTasks};

        return array;
    }


    public void recordMood( int rating ){
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        database.getReference(String.format("%s/mood/%d:%d/%d:%d",userDataPath, day, year, hour, minute)).setValue(rating);
    }

    private void setupMoodHistory(){
        String path = userDataPath + "/mood/";
        int lastyear = Calendar.getInstance().get(Calendar.YEAR)+1;
        int numDaysInYear = 366;

        for(int year = 2017; year <= lastyear; year++ ){
            for(int day= 1; day<=numDaysInYear; day++){
                final String key = day+":"+year;
                database.getReference(path+key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            moodHistory.put(key, getAverageMood(dataSnapshot));
                            Log.d("moodHistory", String.format("%s = %s", key, moodHistory.toString()) );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Something went wrong with getting mood data");
                    }
                });
            }
        }
    }

    private int getAverageMood(DataSnapshot dataSnapshot){
        int numRecords = (int) dataSnapshot.getChildrenCount();
        if(numRecords != 0) {
            int moodPoints = 0;
            for (DataSnapshot mood : dataSnapshot.getChildren()) {
                if(mood.getValue(int.class) != null) {
                    moodPoints += mood.getValue(int.class);
                }else{
                    numRecords = Math.max(numRecords-1, 1);
                }
            }
            return moodPoints / numRecords;
        }else{
            return -1;
        }
    }

    private int getYear(String today){
        String[] part = today.split(":");
        return Integer.parseInt(part[1]);
    }
    private int getDay(String today){
        String[] part = today.split(":");
        return Integer.parseInt(part[0]);
    }

    private String longCalAsString(long date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.DAY_OF_YEAR) + ":" +cal.get(Calendar.YEAR);
    }

    private Calendar stringCalAsCalendar(String date){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, getDay(date) );
        cal.set(Calendar.YEAR, getYear(date) );
        return cal;
    }

    private boolean beforeOrOnDate(String today, String reference){
        int todayDay = getDay(today);
        int todayYear = getYear(today);
        int refDay = getDay(reference);
        int refYear = getYear(reference);

        if(todayYear < refYear){
            return true;
        }else if( todayYear == refYear){
            if(todayDay <= refDay){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    //return true if recurList has pointer's weekday flagged as true
    private boolean checkWeekDayRecur(Calendar pointer, boolean[] recurringDays){
        switch(pointer.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY: return recurringDays[0];
            case Calendar.TUESDAY: return recurringDays[1];
            case Calendar.WEDNESDAY: return recurringDays[2];
            case Calendar.THURSDAY: return recurringDays[3];
            case Calendar.FRIDAY: return recurringDays[4];
            case Calendar.SATURDAY: return recurringDays[5];
            case Calendar.SUNDAY: return recurringDays[6];
            default: return false;
        }
    }

    boolean itemInList(List<ToDoItem> toDoList, String id){
        for (ToDoItem item: toDoList) {
            if(item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public String mapOfArraysToString(HashMap<String, Integer[]> map){
        ArrayList<String> newString = new ArrayList<String>();
        for(String key: map.keySet()) {
            newString.add(String.format("(%s=[%d, %d])", key, map.get(key)[0], map.get(key)[1]));
        }
        return newString.toString();
    }
}
