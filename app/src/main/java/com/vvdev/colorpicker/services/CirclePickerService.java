package com.vvdev.colorpicker.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;


import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.activity.CirclePickerActivityStart;
import com.vvdev.colorpicker.interfaces.ColorsData;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.vvdev.colorpicker.activity.CirclePickerActivityStart.wmCirclePickerView;
import static com.vvdev.colorpicker.activity.MainActivity.isCPRunning;

public class CirclePickerService extends Service { // TODO fix back press bug

    public static boolean waitingForResult = true;
    public static boolean circleStarted = false;

    private final static String TAG = CirclePickerService.class.getName();
    private final static String CHANNEL_CIRCLE_PICKER_NOTIFICATION_ID = "Circle_picker_channel_id";
    private NotificationCompat.Builder notificationBuilder; // use to update hexa value, plz refer to https://stackoverflow.com/questions/14885368/update-text-of-notification-not-entire-notification
    private String oldHexValue; // need to save last hex value received so we can refresh notification when user clicked on hide / show
    private int isHideOrShow = 0 ; // 0 = hide, 1 = show

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
        isCPRunning=true; // boolean use to say if service is running or not ( declare static in main activity )

        if(intent!=null){

            if(intent.getAction()!=null&&intent.getAction().equals("STOP")){
                Log.i(TAG,"intent STOP detected");
                stopService();
            }else if(intent.getAction()!=null&&intent.getAction().equals("HIDE")){
                Log.i(TAG,"intent HIDE detected");
                isHideOrShow=1;
                wmCirclePickerView.setVisibility(View.GONE);
                updateHexaValue(oldHexValue);
            }else if(intent.getAction()!=null&&intent.getAction().equals("SHOW")){
                Log.i(TAG,"intent SHOW detected");
                isHideOrShow=0;
                wmCirclePickerView.setVisibility(View.VISIBLE);
                updateHexaValue(oldHexValue);
            }else{
                Instance.set(this);
                startCirclePicker();
                notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_CIRCLE_PICKER_NOTIFICATION_ID );
                notificationFirstBuild();
            }

        }
        return START_STICKY;
    }

    private void notificationFirstBuild(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_CIRCLE_PICKER_NOTIFICATION_ID,
                    "Circle picker color",
                    NotificationManager.IMPORTANCE_LOW // TODO check google document to find the good IMPORTANCE for notification manager
            );
            channel1.setDescription("This is Channel 1");


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

        String message = getApplicationContext().getString(R.string.service_waiting_permission);


        Notification notification = notificationBuilder.setSmallIcon(R.drawable.pipette_icon_icons_com_65005) // TODO replace by the application icon
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(1, notification);
    }

    /**
     * used to update the hexadecimal value inside notification
     * @param Hexa
     */
    @SuppressLint("RestrictedApi")
    public void updateHexaValue(String Hexa){
        oldHexValue=Hexa;
        notificationBuilder.mActions.clear(); // clear all past action ( you need to do that cuz you call addAction and not setAction ) plz refer https://stackoverflow.com/questions/24465587/change-notifications-action-icon-dynamically
        notificationBuilder
                .setContentTitle(getApplicationContext().getString(R.string.service_current_hexa))
                .setContentText(Hexa)
                .addAction(0,getApplicationContext().getString(R.string.service_stop),stopIntent())
                .addAction(0,getApplicationContext().getString(R.string.service_share),shareIntent(Hexa));
        if(isHideOrShow==0){ // hide
            notificationBuilder
                    .addAction(0,getApplicationContext().getString(R.string.service_hide),hideIntent());
        }else{ // show
            notificationBuilder
                    .addAction(0,getApplicationContext().getString(R.string.service_show),showIntent());
        }
        Notification notification = notificationBuilder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);
    }

    /**
     * used to set on click listener the close button of circle picker view
     */
    public void setOnClickListenerCloseButton(){
        wmCirclePickerView.findViewById(R.id.CirclePickerCloseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Close button pressed, stopping service");
                stopService();
            }
        });
    }

    private void startCirclePicker() {
        waitingForResult = true;
        Intent startCirclePickerIntent = new Intent(this, CirclePickerActivityStart.class);
        startCirclePickerIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(startCirclePickerIntent);
        setWaitingForResult();
    }

    private void setWaitingForResult(){
        final Timer t = new Timer();
        t.schedule(new TimerTask() { // we check each 500 ms if user have finish with permission and if user hasn't give permission we stop the service
            @Override
            public void run() {
                if(!waitingForResult){
                    if(!circleStarted){
                        stopService();
                    }
                    t.cancel();
                }
            }
        }, 0, 500);
    }

    private PendingIntent shareIntent(String hexaValue){ // get sharing intent to share the color name TODO need to fix share or check if it's work on other devices, on samsung it doesn't work
        String shareBody = "Hexadecimal: "+hexaValue+" #ColorPicker"; // TODO replace by application name and application https on play store
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        Intent chooserIntent = Intent.createChooser(sharingIntent, "Share your color !");// TODO to translate
        chooserIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(getApplicationContext(), 0, chooserIntent, 0);
    }

    private PendingIntent stopIntent(){ // use to send a call in OnStartCommand
        Intent mIntent = new Intent(getApplicationContext(), CirclePickerService.class);
        mIntent.setAction("STOP");
        return PendingIntent.getService(getApplicationContext(), 0, mIntent, 0) ;
    }

    private PendingIntent hideIntent(){
        Intent mIntent = new Intent(getApplicationContext(), CirclePickerService.class);
        mIntent.setAction("HIDE");
        return PendingIntent.getService(getApplicationContext(), 0, mIntent, 0) ;
    }

    private PendingIntent showIntent(){
        Intent mIntent = new Intent(getApplicationContext(), CirclePickerService.class);
        mIntent.setAction("SHOW");
        return PendingIntent.getService(getApplicationContext(), 0, mIntent, 0) ;
    }

    private void stopService(){
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE); // TODO ask permission to draw over other app
        if(wmCirclePickerView!=null&&wmCirclePickerView.isAttachedToWindow()){
            wm.removeView(wmCirclePickerView);
        }
        isCPRunning=false; // boolean use to say if service is running or not ( declare static in main activity )
        stopForeground(true);
    }

    public static class Instance{
        private static CirclePickerService instance;

        public static void set(CirclePickerService inst) {
            instance=inst;
        }


        public static CirclePickerService getInstance() {
            return instance;
        }
    }



}
