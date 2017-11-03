package com.example.android.elevate;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AddActivity extends AppCompatActivity {

    DatePickerDialog.OnDateSetListener dListener1, dListener2;
    TimePickerDialog.OnTimeSetListener tListener1, tListener2;
    int currentDay, currentMonth, currentYear;
    String date1, date2;
    Calendar time1, time2;
    private static final DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

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
        Button button_AddTask = (Button) findViewById(R.id.button_CreateTask);

        //retrieve date info from intent by TasksActivity. default set to 1/1/2017
        time1 = time2 = cal;
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

        //Click on create task button to bundle up task info
        //and send it back to TasksActivity thru intent with RESULT_OK
        //format: Task Title, Start Time, Start Date, End Time, End Date
        button_AddTask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(taskTitle.getText().length()> 0) {
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    intent.putExtra("title", taskTitle.getText().toString());
                    intent.putExtra("time1", time1);
                    intent.putExtra("time2", time2);
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
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH, month);
        time.set(Calendar.YEAR, year);
    };
}
