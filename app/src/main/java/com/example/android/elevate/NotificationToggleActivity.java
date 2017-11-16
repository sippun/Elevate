package com.example.android.elevate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

/**
 * Created by katierosengrant on 10/29/17.
 *
 * Settings page which contains habit reminder notification toggle.
 * Set time of daily habit reminder here.
 */

public class NotificationToggleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_toggle);


        // Toggle switch for habit reminder notifications
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    /* The toggle is enabled */

                    Context context = getApplicationContext();
                    CharSequence text = "Habit Reminders: ON";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    // Get calendar instance to be able to select what time notification should be scheduled
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    // Set notification time
                    calendar.set(Calendar.HOUR_OF_DAY, 16);
                    calendar.set(Calendar.MINUTE, 23);
                    calendar.set(Calendar.SECOND, 0);

                    // Setting intent to class where Alarm broadcast message will be handled
                    Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                    intent.setAction("com.example.android.elevate.MY_NOTIFICATION"); ///////

                    // Pending Intent for if user clicks on notification
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getApplicationContext(),
                            100,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    // Get instance of AlarmManager service
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    assert alarmManager != null;
                    alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent);

                    // sendBroadcast(intent); // (this sends notifications immediately)

                } else {
                    /* The toggle is disabled */

                    Context context = getApplicationContext();
                    CharSequence text = "Habit Reminders: OFF";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }
            }
        });





    }
}
