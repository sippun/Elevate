package com.example.android.elevate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Jason on 11/23/2017.
 */

public class MoodNotificationReceiver extends BroadcastReceiver{
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Notification built here

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int NOTIFICATION_ID = 102;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String CHANNEL_ID = "mood_channel";
        CharSequence name = "mood channel";
        String description = "mood prompt channel description";

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        channel.enableVibration(true);
        channel.setVibrationPattern(null);
        assert notificationManager != null; ////
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_popup_reminder) /* required */
                        .setContentTitle("Elevate")
                        .setContentText("PLease log your mood");

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
