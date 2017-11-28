package com.example.android.elevate;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    private static final String TAG = "AddActTag";
    DatePickerDialog.OnDateSetListener dListener1, dListener2;
    TimePickerDialog.OnTimeSetListener tListener1, tListener2;
    int currentDay, currentMonth, currentYear; //temp values to hold input
    String date1, date2;
    private Calendar time1, time2;   //actual start and end times to be passed back to main activity
    //private static final DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private EditText taskTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setTitle("New Task");
        taskTitle = (EditText) findViewById(R.id.text_TaskTitle);

        RadioGroup taskTypeGroup = (RadioGroup) findViewById(R.id.radio_TaskType);

        taskTypeGroup.check(R.id.radio_task);
        inflateAddTask();

        taskTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                removeInputType();
                switch (checkedId) {
                    case R.id.radio_habit:
                        inflateAddHabit();
                        break;
                    case R.id.radio_task:
                        inflateAddTask();
                        break;
                }
            }
        });
    }

    private void removeInputType() {
        ViewGroup parent = findViewById(R.id.add_input_layout);
        parent.removeAllViews();
    }

    private ArrayList<CheckedTextView> initWeekdayRecurringList(boolean fill) {
        final CheckedTextView checkedMon = findViewById(R.id.checkedMon);
        final CheckedTextView checkedTue = findViewById(R.id.checkedTue);
        final CheckedTextView checkedWed = findViewById(R.id.checkedWed);
        final CheckedTextView checkedThu = findViewById(R.id.checkedThu);
        final CheckedTextView checkedFri = findViewById(R.id.checkedFri);
        final CheckedTextView checkedSat = findViewById(R.id.checkedSat);
        final CheckedTextView checkedSun = findViewById(R.id.checkedSun);

        final ArrayList<CheckedTextView> checkDays = new ArrayList<CheckedTextView>() {
            {
                add(checkedMon); add(checkedTue); add(checkedWed); add(checkedThu);
                add(checkedFri); add(checkedSat); add(checkedSun);
            }
        };

        for(int i = 0; i < checkDays.size(); i++) {
            final CheckedTextView dayCheckView = checkDays.get(i);
            dayCheckView.setChecked(fill);
            dayCheckView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dayCheckView.toggle();
                }
            });
        }

        return checkDays;
    }

    private void inflateAddHabit() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup)findViewById(R.id.add_input_layout);
        View addHabitView = inflater.inflate(R.layout.input_add_habit, parent);

        final ArrayList<CheckedTextView> checkDays = initWeekdayRecurringList(true);

        Button button_AddHabit = findViewById(R.id.button_CreateTask);
        button_AddHabit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean[] recurringDays = getRecurringDays(checkDays);
                DBHabitItem newHabit = new DBHabitItem(taskTitle.getText().toString(),
                        recurringDays);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null && newHabit != null) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(user.getUid())
                            .child("habits")
                            .push()
                            .setValue(newHabit);
                }

                time1.set(Calendar.SECOND, 0);
                time2.set(Calendar.SECOND, 0); // temp

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

    private boolean[] getRecurringDays(ArrayList<CheckedTextView> days) {
        boolean[] recurringDaysArray = new boolean[days.size()];
        for(int i = 0; i < recurringDaysArray.length; i++) {
            recurringDaysArray[i] = days.get(i).isChecked();
        }
        return recurringDaysArray;
    }

    private void inflateAddTask() {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup)findViewById(R.id.add_input_layout);
        View addTaskView = inflater.inflate(R.layout.input_add_task, parent);
        final Calendar cal = Calendar.getInstance();

        //set up layout components of addTask page
        final TextView startDate = (TextView) findViewById(R.id.text_StartDate);
        final TextView endDate = (TextView) findViewById(R.id.text_EndDate);

        final TextView startTime = (TextView) findViewById(R.id.text_StartTime);
        final TextView endTime = (TextView) findViewById(R.id.text_EndTime);

        Button button_AddTask = (Button) findViewById(R.id.button_CreateTask);

        //retrieve date info from intent by TasksActivity. default set to 1/1/2017
        time1 = Calendar.getInstance();
        time2 = Calendar.getInstance();
        time1.set(Calendar.SECOND, 0);
        time2.set(Calendar.SECOND, 0);
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

        final ArrayList<CheckedTextView> checkDays = initWeekdayRecurringList(false);

        //Click on create task button to bundle up task info
        //and send it back to MainActivity thru intent with RESULT_OK
        //format: Task Title, Start Time, End Time
        button_AddTask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
              
                boolean[] recurringDays = getRecurringDays(checkDays);
                ToDoItem newTask = new ToDoItem(taskTitle.getText().toString(),

                        time1.getTimeInMillis(),
                        time2.getTimeInMillis(),
                        recurringDays);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG+"addTask", newTask.name);



                if(user != null) {
                    Log.d(TAG+"addTask", newTask.toString());
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(user.getUid())
                            .child("tasks")
                            .push()
                            .setValue(newTask.createDataBaseEntry());
                }else{
                    Log.d(TAG+"addTask", "user =null");

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
}
