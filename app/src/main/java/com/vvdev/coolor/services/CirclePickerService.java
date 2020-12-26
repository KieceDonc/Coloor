package com.vvdev.coolor.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import com.vvdev.coolor.R;
import com.vvdev.coolor.activity.CirclePickerActivityStart;
import com.vvdev.coolor.interfaces.ScreenCapture;
import com.vvdev.coolor.ui.customview.CirclePickerView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.vvdev.coolor.activity.CirclePickerActivityStart.wmCirclePickerView;

public class CirclePickerService extends Service {

    private final static String TAG = CirclePickerService.class.getName();
    private final static String CHANNEL_CIRCLE_PICKER_NOTIFICATION_ID = "Circle_picker_channel_id";
    private final static String INTENT_ACTION_STOP = "STOP";
    private final static String INTENT_ACTION_HIDE = "HIDE";
    private final static String INTENT_ACTION_SHOW = "SHOW";
    private final static String INTENT_ACTION_START = "START";
    private final static int NOTIFICATION_ID = 1;

    private CirclePickerView circlePickerView;
    private ImageView closeButton;
    private ImageView saveButton;
    private ImageView zoomInButton;
    private ImageView zoomOutButton;

    private NotificationCompat.Builder notificationBuilder; // use to update hexa value, plz refer to https://stackoverflow.com/questions/14885368/update-text-of-notification-not-entire-notification
    private String oldHexValue; // need to save last hex value received so we can refresh notification when user clicked on hide / show
    private int isHideOrShow = 0 ; // 0 = hide, 1 = show

    //public boolean tryToStartCirclePicker = false; TODO to active premium version

    @Override
    public void onCreate() {
        super.onCreate();
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_CIRCLE_PICKER_NOTIFICATION_ID );
        notificationFirstBuild();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null&&intent.getAction()!=null){
            if(intent.getAction().equals(INTENT_ACTION_STOP)){
                Log.i(TAG,"intent STOP detected");
                stopService();
            }else if(intent.getAction().equals(INTENT_ACTION_HIDE)){
                Log.i(TAG,"intent HIDE detected");
                hideCirclePicker();
            }else if(intent.getAction().equals(INTENT_ACTION_SHOW)){
                Log.i(TAG,"intent SHOW detected");
                showCirclePicker();
            }else if(intent.getAction().equals(INTENT_ACTION_START)){
                Instance.set(this);
                notificationFirstBuild();
                //tryToStartCirclePicker=true;TODO to active premium version
                startCirclePicker();
            }

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Instance.set(null);
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


        Notification notification = notificationBuilder.setSmallIcon(R.drawable.ic_pipette_notification)
                .setContentText(message)
                .setSound(null)
                .setVibrate(null)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(NOTIFICATION_ID,notification);
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

    /**
     * Used to send a stop call in OnStartCommand
     * @return Pending intent to stop service
     */
    private PendingIntent stopIntent(){
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

    /**
     * used to set on click listener the close button of circle picker view
     */
    public void setup(){
        if(canWorkOnCPVView()){
            circlePickerView = wmCirclePickerView.findViewById(R.id.CirclePicker);

            closeButton = wmCirclePickerView.findViewById(R.id.CirclePickerCloseButton);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Close button pressed, stopping service");
                    stopService();
                }
            });

            saveButton = wmCirclePickerView.findViewById(R.id.CirclePickerSave);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"save button clicked");
                    circlePickerView.saveCurrentColor();
                }
            });

            zoomInButton = wmCirclePickerView.findViewById(R.id.CirclePickerZoomIn);
            zoomInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"zoom in button clicked");
                    circlePickerView.zoomIn();
                }
            });

            zoomOutButton =wmCirclePickerView.findViewById(R.id.CirclePickerZoomOut);
            zoomOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"zoom out button clicked");
                    circlePickerView.zoomOut();
                }
            });
        }
    }


    private void startCirclePicker() {
        try{
            Intent startCirclePickerIntent = new Intent(this, CirclePickerActivityStart.class);
            startCirclePickerIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(startCirclePickerIntent);
        }catch (NullPointerException | IllegalStateException ex){
            if(ex.getMessage().equals("Cannot start already started MediaProjection")){
                Toast.makeText(getApplicationContext(),"MediaProjection api already running, please stop it",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"enable to start circle picker",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onCirclePickerPermissionGiven(){
        setup();
        if(canWorkOnCPVView()){
            circlePickerView.readyToInit();
            Toast.makeText(getApplicationContext(), getString(R.string.service_you_can_keep_outside), Toast.LENGTH_SHORT).show();
        }

    }

    public void onCirclePickerPermissionDenied(){
        stopService();
    }

    /**
     * Make invisible wmCirclePickerView
     * You must use this method to hide this wmCirclePickerView also you will face a bug with MediaProjection API
     */
    public void hideCirclePicker(){ // used to avoid wmCirclePickerView.setVisibility(INVISIBLE) who's creating bugs
        if(canWorkOnCPVView()){
            isHideOrShow=1;
            circlePickerView.setVisibility(INVISIBLE);
            closeButton.setVisibility(INVISIBLE);
            saveButton.setVisibility(INVISIBLE);
            zoomOutButton.setVisibility(INVISIBLE);
            zoomInButton.setVisibility(INVISIBLE);
            updateHexaValue(oldHexValue);
        }
    }

    /**
     * Make visible wmCirclePickerView
     * You must use this method to show this wmCirclePickerView also you will face a bug with MediaProjection API
     */
    public void showCirclePicker(){  // used to avoid wmCirclePickerView.setVisibility(INVISIBLE) who's creating bugs
        if(canWorkOnCPVView()){
            isHideOrShow=0;
            circlePickerView.setVisibility(VISIBLE);
            closeButton.setVisibility(VISIBLE);
            saveButton.setVisibility(VISIBLE);
            zoomOutButton.setVisibility(VISIBLE);
            zoomInButton.setVisibility(VISIBLE);
            updateHexaValue(oldHexValue);
        }
    }

    public boolean canWorkOnCPVView(){ // CPV = circle picker view
        if(wmCirclePickerView!=null){
            if(wmCirclePickerView.isAttachedToWindow()){
                return true;
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() { // solve dimmed problems
            @Override
            public void run() {
                stopService();
            }
        }, 50);
        return false;
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
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void stopService(){
        Log.i(TAG,"stopping service");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if(wmCirclePickerView!=null&&wmCirclePickerView.isAttachedToWindow()){
            wm.removeView(wmCirclePickerView);
        }
        wmCirclePickerView=null;
        Instance.set(null);
        ScreenCapture screenCapture = ScreenCapture.Instance.get();
        if(screenCapture!=null){
            screenCapture.stop();
        }
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
        stopForeground(true);
    }

    public static class Instance{
        private static CirclePickerService instance;

        public static void set(CirclePickerService inst) {
            instance=inst;
        }

        public static CirclePickerService get() {
            return instance;
        }
    }

    public static void start(Context context){
        if(wmCirclePickerView==null&&Instance.get()==null){
            Intent CirclePickerServiceIntent = new Intent(context, CirclePickerService.class);
            CirclePickerServiceIntent.setAction(INTENT_ACTION_START);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(CirclePickerServiceIntent);
            }else{
                context.startService(CirclePickerServiceIntent);
            }
        }
    }



}
