package com.example.android.elevate;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class CalendarFragment extends Fragment{
    MaterialCalendarView cal;
    MainActivity main;
    boolean tasks_display = true;
    boolean mood_display = false;

    public CalendarFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        final Switch tasks = v.findViewById(R.id.tasks);
        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tasks_display) {
                    tasks_display = false;
                } else {
                    tasks_display = true;
                }
                setDayColors();
            }
        });

        final Switch mood = v.findViewById(R.id.mood);
        mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mood_display) {
                    mood_display = false;
                } else {
                    mood_display = true;
                }
                setDayColors();
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

        main = (MainActivity)getActivity();

        setDayColors();
        return v;
    }

    //iterates through the list of all days that have either a mood
    //or a task and calls setDayColor on them
    public void setDayColors() {
        Set<String> validDays = new HashSet<String>();

        validDays.addAll(this.main.database.donenessHistory.keySet());
        validDays.addAll(this.main.database.moodHistory.keySet());

        for (String day_year : validDays) {
            setDayColor(day_year);
        }
    }

    //determines the background color of the day based on tasks
    //(white if no tasks are available) and them layers the mood
    //image on top of the color if applicable
    private void setDayColor(String day_year) {
        int dayColor = R.drawable.white;
        if (tasks_display && this.main.database.donenessHistory.keySet().contains(day_year)) {
            dayColor = getColor(day_year);
        }
        Drawable dayColorDraw = ContextCompat.getDrawable(getActivity(), dayColor);
        Drawable[] elements = new Drawable[]{dayColorDraw};
        LayerDrawable combined = new LayerDrawable(elements);
        if (mood_display && this.main.database.moodHistory.keySet().contains(day_year)) {
            Drawable dayColorMood = ContextCompat.getDrawable(getActivity(), getMood(day_year));
            combined.addLayer(dayColorMood);
        }

        int day_of_year = Integer.valueOf(day_year.split(":")[0]);

        cal.addDecorator(new EventDecorator(day_of_year, combined));

    }

    //returns the mood image corresponding to number value
    private int getMood(String day_year) {
        Integer todaysMood = this.main.database.moodHistory.get(day_year);

        if (todaysMood == 1) {
            return R.drawable.sad;
        } else if (todaysMood == 2) {
            return R.drawable.discontented;
        } else if (todaysMood == 3) {
            return R.drawable.neutral;
        } else if (todaysMood == 4) {
            return R.drawable.content;
        } else {
            return R.drawable.happy;
        }
    }

    //returns the background color corresponding to percentage of tasks comepleted
    private int getColor(String day_year) {
        int percent;

        MainActivity main = (MainActivity)getActivity();
        Integer[] todaysList = main.database.donenessHistory.get(day_year);
        int totalTasks = todaysList[1];
        int completedTasks = todaysList[0];

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
    }
}
