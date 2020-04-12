package com.vvdev.coolor.services;

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


import com.vvdev.coolor.R;
import com.vvdev.coolor.activity.CirclePickerActivityStart;
import com.vvdev.coolor.ui.customview.CirclePickerView;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.vvdev.coolor.activity.CirclePickerActivityStart.wmCirclePickerView;

public class CirclePickerService extends Service {

    public static boolean waitingForResult = true;
    public static boolean circleStarted = false;

    private final static String TAG = CirclePickerService.class.getName();
    private final static String CHANNEL_CIRCLE_PICKER_NOTIFICATION_ID = "Circle_picker_channel_id";
    private final static String INTENT_ACTION_STOP = "STOP";
    private final static String INTENT_ACTION_HIDE = "HIDE";
    private final static String INTENT_ACTION_SHOW = "SHOW";
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
        if(intent!=null){

            if(intent.getAction()!=null&&intent.getAction().equals(INTENT_ACTION_STOP)){
                Log.i(TAG,"intent STOP detected");
                stopService();
            }else if(intent.getAction()!=null&&intent.getAction().equals(INTENT_ACTION_HIDE)){
                Log.i(TAG,"intent HIDE detected");
                isHideOrShow=1;
                wmCirclePickerView.setVisibility(View.GONE);
                updateHexaValue(oldHexValue);
            }else if(intent.getAction()!=null&&intent.getAction().equals(INTENT_ACTION_SHOW)){
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
            channel1.setSound(null,null);
            channel1.setVibrationPattern(null);


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

        String message = getApplicationContext().getString(R.string.service_waiting_permission);


        Notification notification = notificationBuilder.setSmallIcon(R.drawable.pipette_icon_icons_com_65005)
                .setContentText(message)
                .setSound(null)
                .setVibrate(null)
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
    public void setOnClickListenerOutsideButton(){
        if(wmCirclePickerView!=null){
            final CirclePickerView cpv = wmCirclePickerView.findViewById(R.id.CirclePicker);

            wmCirclePickerView.findViewById(R.id.CirclePickerCloseButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Close button pressed, stopping service");
                    stopService();
                }
            });

            wmCirclePickerView.findViewById(R.id.CirclePickerSave).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"save button clicked");
                    cpv.saveCurrentColor();
                }
            });

            wmCirclePickerView.findViewById(R.id.CirclePickerZoomIn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"zoom in button clicked");
                    cpv.zoomIn();
                }
            });

            wmCirclePickerView.findViewById(R.id.CirclePickerZoomOut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"zoom out button clicked");
                    cpv.zoomOut();
                }
            });
        }else{
            throw new RuntimeException("Couldn't set outside circle picker view button on click listener because wmCirclePickerView is null");
        }
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
        String shareBody = "Hexadecimal: "+hexaValue+" "+getResources().getString(R.string.share_self_promo);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        Intent chooserIntent = Intent.createChooser(sharingIntent, getApplicationContext().getResources().getString(R.string.service_share_title));
        chooserIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(getApplicationContext(), 0, chooserIntent, 0);
    }

    private PendingIntent stopIntent(){ // use to send a call in OnStartCommand
        Intent mIntent = new Intent(getApplicationContext(), CirclePickerService.class);
        mIntent.setAction(INTENT_ACTION_STOP);
        return PendingIntent.getService(getApplicationContext(), 0, mIntent, 0) ;
    }

    private PendingIntent hideIntent(){
        Intent mIntent = new Intent(getApplicationContext(), CirclePickerService.class);
        mIntent.setAction(INTENT_ACTION_HIDE);
        return PendingIntent.getService(getApplicationContext(), 0, mIntent, 0) ;
    }

    private PendingIntent showIntent(){
        Intent mIntent = new Intent(getApplicationContext(), CirclePickerService.class);
        mIntent.setAction(INTENT_ACTION_SHOW);
        return PendingIntent.getService(getApplicationContext(), 0, mIntent, 0) ;
    }

    public void stopService(){
        Log.i(TAG,"stopping service");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if(wmCirclePickerView!=null&&wmCirclePickerView.isAttachedToWindow()){
            wm.removeView(wmCirclePickerView);
        }
        wmCirclePickerView=null;
        Instance.set(null);
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
