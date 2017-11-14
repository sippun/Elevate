package com.example.android.elevate;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    DatePickerDialog.OnDateSetListener dListener1, dListener2;
    TimePickerDialog.OnTimeSetListener tListener1, tListener2;
    int currentDay, currentMonth, currentYear; //temp values to hold input
    String date1, date2;
    private Calendar time1, time2;   //actual start and end times to be passed back to main activity
    //private static final DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private boolean[] recurringDays = {true, true, true, true, true, true, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setTitle("New Task");
        final Calendar cal = Calendar.getInstance();

        //set up layout components of addTask page
        final TextView startDate = (TextView) findViewById(R.id.text_StartDate);
        final TextView endDate = (TextView) findViewById(R.id.text_EndDate);

        final EditText taskTitle = (EditText) findViewById(R.id.text_TaskTitle);
        final TextView startTime = (TextView) findViewById(R.id.text_StartTime);
        final TextView endTime = (TextView) findViewById(R.id.text_EndTime);

        final CheckedTextView checkedMon = (CheckedTextView)findViewById(R.id.checkedMon);
        final CheckedTextView checkedTue = (CheckedTextView)findViewById(R.id.checkedTue);
        final CheckedTextView checkedWed = (CheckedTextView)findViewById(R.id.checkedWed);
        final CheckedTextView checkedThu = (CheckedTextView)findViewById(R.id.checkedThu);
        final CheckedTextView checkedFri = (CheckedTextView)findViewById(R.id.checkedFri);
        final CheckedTextView checkedSat = (CheckedTextView)findViewById(R.id.checkedSat);
        final CheckedTextView checkedSun = (CheckedTextView)findViewById(R.id.checkedSun);

        Button button_AddTask = (Button) findViewById(R.id.button_CreateTask);
        RadioGroup taskTypeGroup = (RadioGroup) findViewById(R.id.radio_TaskType);
        taskTypeGroup.check(R.id.radio_task);

        //retrieve date info from intent by TasksActivity. default set to 1/1/2017
        time1 = Calendar.getInstance();
        time2 = Calendar.getInstance();
        currentDay = getIntent().getIntExtra("day", cal.get(Calendar.DAY_OF_MONTH));
        currentMonth = getIntent().getIntExtra("month", cal.get(Calendar.MONTH)+1);
        currentYear = getIntent().getIntExtra("year",cal.get(Calendar.YEAR));

        //set startdate and enddate to default current date. Simplifies user input
        date1 = date2 = currentMonth+"/"+currentDay+"/"+currentYear;
        startDate.setText(date1);
        endDate.setText(date2);

        //click on startTime/endTime boxes to open up time pickers. Assign correct listeners.
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(AddActivity.this,
                        R.style.Theme_AppCompat_Light_Dialog,
                        tListener1,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                );
                dialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(AddActivity.this,
                        R.style.Theme_AppCompat_Light_Dialog,
                        tListener2,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                );
                dialog.show();
            }
        });

        //click startDate box to open date picker
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(AddActivity.this,
                        R.style.Theme_AppCompat_Light_Dialog,
                        dListener1,
                        currentYear, currentMonth-1, currentDay);
                dialog.show();
            }
        });

        //click endDate box to open date picker
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog1 = new DatePickerDialog(AddActivity.this,
                        R.style.Theme_AppCompat_Light_Dialog,
                        dListener2,
                        currentYear, currentMonth-1, currentDay);
                dialog1.show();
            }
        });

        //set each time picker listener's behavior on setting a time
        //endTime syncs with startTime initially but adds one hour (assume 1-hour task duration)
        tListener1 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTime(time1,hourOfDay, minute);
                setTime(time2,hourOfDay+1, minute);
                startTime.setText(hourOfDay+":"+minute);
                endTime.setText((hourOfDay+1)+":"+minute);
            }
        };

        tListener2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTime(time2, hourOfDay, minute);
                endTime.setText(hourOfDay+":"+minute);
            }
        };

        //Set each date picker listener's behavior on setting a date
        //endDate automatically syncs with startDate changes. endDate can be manually set later.
        dListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date1 = date2 = (month+1) +"/"+dayOfMonth+"/"+year;
                startDate.setText(date1);
                endDate.setText(date2);
                setDate(time1, year, month, dayOfMonth);
                setDate(time2, year, month, dayOfMonth);
            }
        };

        dListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date2 = (month+1) +"/"+dayOfMonth+"/"+year;
                endDate.setText(date2);
                setDate(time2, year, month, dayOfMonth);
            }
        };

        //check'em all
        check(checkedMon, checkedTue, checkedWed, checkedThu, checkedFri, checkedSat, checkedSun);

        //Checked behaviors for each weekday.
        //Checking a weekday turns its corresponding recurringDays[] entry to true
        checkedMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedMon.isChecked()){
                    checkedMon.setChecked(false);
                    recurringDays[0] = false;
                }else{
                    checkedMon.setChecked(true);
                    recurringDays[0] = true;
                }
            }
        });

        checkedTue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedTue.isChecked()){
                    checkedTue.setChecked(false);
                    recurringDays[1] = false;
                }else{
                    checkedTue.setChecked(true);
                    recurringDays[1] = true;
                }
            }
        });

        checkedWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedWed.isChecked()){
                    checkedWed.setChecked(false);
                    recurringDays[2] = false;
                }else{
                    checkedWed.setChecked(true);
                    recurringDays[2] = true;
                }
            }
        });

        checkedThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedThu.isChecked()){
                    checkedThu.setChecked(false);
                    recurringDays[3] = false;
                }else{
                    checkedThu.setChecked(true);
                    recurringDays[3] = true;
                }
            }
        });

        checkedFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedFri.isChecked()){
                    checkedFri.setChecked(false);
                    recurringDays[4] = false;
                }else{
                    checkedFri.setChecked(true);
                    recurringDays[4] = true;
                }
            }
        });

        checkedSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedSat.isChecked()){
                    checkedSat.setChecked(false);
                    recurringDays[5] = false;
                }else{
                    checkedSat.setChecked(true);
                    recurringDays[5] = true;
                }
            }
        });

        checkedSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedSun.isChecked()){
                    checkedSun.setChecked(false);
                    recurringDays[6] = false;
                }else{
                    checkedSun.setChecked(true);
                    recurringDays[6] = true;
                }
            }
        });

        //Click on create task button to bundle up task info
        //and send it back to MainActivity thru intent with RESULT_OK
        //format: Task Title, Start Time, End Time
        button_AddTask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DBTaskItem newTask = new DBTaskItem(taskTitle.getText().toString(),
                        time1.getTimeInMillis(),
                        time2.getTimeInMillis());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(user.getUid())
                            .child("tasks")
                            .push()
                            .setValue(newTask);
                }

                if(taskTitle.getText().length()> 0) {
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    intent.putExtra("title", taskTitle.getText().toString());
                    intent.putExtra("time1", time1);
                    intent.putExtra("time2", time2);
                    intent.putExtra("recur", recurringDays);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void setTime(Calendar time, int hour, int minute){
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
    };

    public void setDate(Calendar time, int year, int month, int day){
        time.set(Calendar.DAY_OF_MONTH, day);
        time.set(Calendar.MONTH, month);
        time.set(Calendar.YEAR, year);
    };

    public void check(CheckedTextView... blah){
        for(CheckedTextView bla: blah){
            bla.setChecked(true);
        }
    }
}
