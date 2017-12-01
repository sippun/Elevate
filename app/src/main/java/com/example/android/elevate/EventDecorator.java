package com.example.android.elevate;

import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class EventDecorator implements DayViewDecorator {

    private int day_of_year;
    private Drawable dayColor;

    public EventDecorator(int day_of_year, Drawable dayColor) {
        this.day_of_year = day_of_year;
        this.dayColor = dayColor;
    }

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

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(this.dayColor);
    }
}
