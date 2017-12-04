package com.example.android.elevate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

/**
 * Created by Jason on 11/23/2017.
 */

public class MoodNotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        // Notification built here

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int NOTIFICATION_ID = intent.getIntExtra("id", 12);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String CHANNEL_ID = "mood_channel";
        CharSequence name = "mood channel";
        String description = "mood prompt description";

        NotificationChannel mood_channel = new NotificationChannel(CHANNEL_ID, name, importance);
        mood_channel.setDescription(description);
        mood_channel.enableLights(true);
        mood_channel.setLightColor(Color.BLUE);
        mood_channel.enableVibration(true);
        mood_channel.setVibrationPattern(null);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(mood_channel);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_record_mood) /* required */
                        .setContentTitle("Elevate")
                        .setContentText("Please record your mood")
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MoodInputUI.class);

        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        // pass the Notification object to the system
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
