package com.example.android.elevate;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment{
    MaterialCalendarView cal;
    boolean tasks = true;
    boolean mood = false;

    public CalendarFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        final Switch tasks_mood = v.findViewById(R.id.tasks_mood);
        tasks_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tasks) {
                    tasks = false;
                } else {
                    tasks = true;
                }
                if (mood) {
                    mood = false;
                } else {
                    mood = true;
                }
            }
        });

        cal = v.findViewById(R.id.calendarView);


        cal.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new ToDoFragment(Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR)).commit();
            }
        });

        setDayColors(v);
        return v;
    }


    private void setDayColors(View v) {
        MainActivity main = (MainActivity)getActivity();

        if (tasks) {
            for (String day_year : main.database.dayToItemsMap.keySet()) {
                setDayColor(day_year, v);
            }
        }
    }

    private void setDayColor(String day_year, View v) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_YEAR, Integer.valueOf(day_year.split(":")[0]));
//        calendar.set(Calendar.YEAR, Integer.valueOf(day_year.split(":")[1]));

        Drawable dayColor = getResources().getDrawable( getColor(day_year) );
//        dayColor.setTint(getColor(day_year));

        int day_of_year = Integer.valueOf(day_year.split(":")[0]);

        cal.addDecorator(new EventDecorator(day_of_year, dayColor));

//        Calendar offset = Calendar.getInstance();
//        offset.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
//        offset.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
//        offset.set(Calendar.DAY_OF_MONTH, 1);
//
//
//
//        int day_of_month = calendar.get(Calendar.DAY_OF_MONTH) + offset.get(Calendar.DAY_OF_WEEK) - 1;
//        String dayID = "day" + day_of_month;
//        int colorID = getResources().getIdentifier(dayID, "id", getActivity().getPackageName());
//
//        ImageView image = v.findViewById(colorID);

//        image.setBackgroundColor(getColor(day_year));
    }

    private int getColor(String day_year) {
        int percent;

        MainActivity main = (MainActivity)getActivity();
        ArrayList<ToDoItem> todaysList = main.database.dayToItemsMap.get(day_year);
        int totalTasks = todaysList.size();
        int completedTasks = 0;
        for (ToDoItem item : todaysList) {
            if (item.done == true) {
                completedTasks++;
            }
        }
        percent = completedTasks * 100 / totalTasks;

        if (percent <= 25) {
            return R.drawable.red;
        } else if (percent <= 50) {
            return R.drawable.orange;
        } else if (percent <= 75) {
            return R.drawable.yellow;
        } else {
            return R.drawable.green;
        }


//        return Color.argb(255,
//                (int) (255-(255*Math.max((completedTasks-(totalTasks/2.0))/(totalTasks/2.0), 0.0))),
//                (int) (255*Math.min((completedTasks)/(totalTasks/2.0), 1.0)), 0);
    }
}
