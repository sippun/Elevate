package com.example.android.elevate;

import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class EventDecorator implements DayViewDecorator {

    private int day_of_year;
    private Drawable dayBG;

    //each day has its own decorator since there are many combinations
    //of mood and background color
    public EventDecorator(int day_of_year, Drawable dayBG) {
        this.day_of_year = day_of_year;
        this.dayBG = dayBG;
    }

    //retrieves the Calendar equivalent of the provided CalendarDay
    //and checks to see if this matches the correct day_of_year
    //this loops quite frequently and can unfortunately be slow
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, day.getYear());
        cal.set(Calendar.MONTH, day.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, day.getDay());
        int loopDay = cal.get(Calendar.DAY_OF_YEAR);
        if (loopDay == this.day_of_year) {
            return true;
        }
        return false;
    }

    //sets the background of the DayViewFacade that matches
    //the day_of_year found in shouldDecorate()
    @Override
    public void decorate(DayViewFacade view) {

        view.setBackgroundDrawable(this.dayBG);
    }
}
