package com.everless.gravital;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * http://stacktips.com/tutorials/android/repeat-alarm-example-in-android
 */

public class AlarmReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.i("AlarmReceiver.onReceive", "alarm receiver running");
        //Toast.makeText(context, "I'm running", Toast.LENGTH_LONG).show();
        triggerNotification();
    }

    public void triggerNotification() {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        Intent intent = new Intent(context, AndroidLauncher.class);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification notification  = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(context)
                    .setContentTitle("Gravital")
                    .setContentText("There are levels waiting for you!")
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(largeIcon)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }
}