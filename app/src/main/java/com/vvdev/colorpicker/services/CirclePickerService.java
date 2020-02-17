package com.vvdev.colorpicker.services;

import android.app.LauncherActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.activity.CirclePickerActivityStart;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class CirclePickerService extends Service {

    public static boolean waitingForResult = true;
    public static boolean circleStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startCirclePicker();
        setWaitingForResult();
        notificationHandler();
        return START_STICKY;
    }

    private void notificationHandler(){

        String idChannel = "ColorPickerChannel";
        Intent mainIntent;

        mainIntent = new Intent(this, LauncherActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = null;
        // The id of the channel.

        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null);
        builder.setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.pipette_icon_icons_com_65005)
                .setContentIntent(pendingIntent)
                .setContentText("test desc");

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        } else {
            builder.setContentTitle(getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(ContextCompat.getColor(this, R.color.transparent))
                    .setVibrate(new long[]{100, 250})
                    .setLights(Color.YELLOW, 500, 5000)
                    .setAutoCancel(true);
        }
        mNotificationManager.notify(1, builder.build());

        /*NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "CirclePickerChannel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)//TODO create a proper notification https://developer.android.com/training/notify-user/build-notification
                .setSmallIcon(R.drawable.palette_ir_background)
                .setContentTitle("Color picker")
                .setContentText(" test")
                .setPriority(NotificationCompat.PRIORITY_HIGH);*/
    }

    private void startCirclePicker() {
        Intent startCirclePickerIntent = new Intent(this, CirclePickerActivityStart.class);
        startCirclePickerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startCirclePickerIntent);
    }

    private void setWaitingForResult(){
        final Timer t = new Timer();
        t.schedule(new TimerTask() { // we check each 500 ms if user have finish with permission and if user hasn't give permission we stop the service
            @Override
            public void run() {
                if(!waitingForResult){
                    if(!circleStarted){
                        stopSelf();
                    }
                    t.cancel();
                }
            }
        }, 0, 500);
    }

}
