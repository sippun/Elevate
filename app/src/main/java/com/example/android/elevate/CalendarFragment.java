package com.example.android.elevate;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment{
    CalendarView cal;

    public CalendarFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        cal = v.findViewById(R.id.calendarView);

        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                getFragmentManager().beginTransaction().
                        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).
                        replace(R.id.fragment_container, new ToDoFragment(dayOfMonth, month, year)).
                        addToBackStack(null).
                        commit();
                }
        });
        setDayColors(v);
        return v;
    }

    private void setDayColors(View v) {
        MainActivity main = (MainActivity)getActivity();
        for (String day_year : main.database.dayToItemsMap.keySet()) {
            setDayColor(day_year, v);
        }
    }

    private void setDayColor(String day_year, View v) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, Integer.valueOf(day_year.split(":")[0]));
        cal.set(Calendar.YEAR, Integer.valueOf(day_year.split(":")[1]));

        Calendar offset = Calendar.getInstance();
        offset.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        offset.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        offset.set(Calendar.DAY_OF_MONTH, 1);

        int day_of_month = cal.get(Calendar.DAY_OF_MONTH) + offset.get(Calendar.DAY_OF_WEEK) - 1;
        String dayID = "day" + day_of_month;
        int colorID = getResources().getIdentifier(dayID, "id", getActivity().getPackageName());

        ImageView image = v.findViewById(colorID);
        image.setBackgroundColor(getColor(day_year));
    }

    private int getColor(String day_year) {
        MainActivity main = (MainActivity)getActivity();
        ArrayList<ToDoItem> todaysList = main.database.dayToItemsMap.get(day_year);
        int totalTasks = todaysList.size();
        int completedTasks = 0;
        for (ToDoItem item : todaysList) {
            if (item.done == true) {
                completedTasks++;
            }
        }
        return Color.argb(255,
                (int) (255-(255*Math.max((completedTasks-(totalTasks/2.0))/(totalTasks/2.0), 0.0))),
                (int) (255*Math.min((completedTasks)/(totalTasks/2.0), 1.0)), 0);
    }
}
