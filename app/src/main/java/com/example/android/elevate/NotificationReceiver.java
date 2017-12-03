package com.example.android.elevate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

/**
 * Created by katierosengrant on 10/29/17.
 *
 * Creates the habit reminder notification.
 * Change notification text/appearance here.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private static NotificationManager notificationManager;
    private String CHANNEL_ID = "task_channel";
    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "task reminder",
            NotificationManager.IMPORTANCE_HIGH);
    private static boolean channelSet = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Notification built here
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String taskTitle = intent.getStringExtra("title");
        int NOTIFICATION_ID = intent.getIntExtra("id", 0);

        assert notificationManager != null;

        if(!channelSet){
            channel.setDescription("task reminder description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
            channelSet = true;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                        .setContentTitle("Elevate")
                        .setContentText("Have you started"+" "+taskTitle+" "+NOTIFICATION_ID+"?")
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);

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
