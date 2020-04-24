package com.vvdev.coolor.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.ScreenCapture;
import com.vvdev.coolor.services.CirclePickerService;
import com.vvdev.coolor.ui.customview.CirclePickerView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static com.vvdev.coolor.interfaces.ScreenCapture.mMediaProjectionManager;

public class CirclePickerActivityStart extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static View wmCirclePickerView;
    public static WindowManager.LayoutParams wmCirclePickerParams;

    public static boolean isCirclePickerActivityRunning = false;

    private static final String TAG = CirclePickerActivityStart.class.getName();
    private static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY = 1234;
    private static final int REQUEST_CODE_MEDIA_PROJECTION = 5555;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       isCirclePickerActivityRunning = true;

        WindowManager.LayoutParams wp = getWindow().getAttributes();
        wp.dimAmount = 0f;


        if (!Settings.canDrawOverlays(this)) {
            showAlertDialog();
        } else {
            startCapture();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() { // used to solve a bug
            @Override
            public void run() {
                CirclePickerService cps = CirclePickerService.Instance.get();
                if(cps!=null){
                    cps.waitingForResult=false;
                    cps.setup();// used to set on click listener the close button
                }
                isCirclePickerActivityRunning=false;
            }
        }, 300);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_MEDIA_PROJECTION){
            if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG,"OnActivityResult REQUEST_CODE_MEDIA_PROJECTION == Activity.RESULT_OK");
                ScreenCapture.setUpMediaProjection(resultCode,data);
                startCirclePicker();
            }else if(resultCode == Activity.RESULT_CANCELED){
                permissionNotGiven();
            }
        }else if(requestCode == REQUEST_CODE_ACTION_MANAGE_OVERLAY){
            if(resultCode == Activity.RESULT_OK){
                startCapture();
            }else{
                permissionNotGiven();
            }
        }
    }

    private void permissionNotGiven(){
        CirclePickerService cps = CirclePickerService.Instance.get();
        if(cps!=null){
            cps.circleStarted=false;
            cps.setup();// used to set on click listener the close button
        }
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void startCapture(){
        mMediaProjectionManager = (MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_MEDIA_PROJECTION);
    }

    private void startCirclePicker(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() { // solve dimmed problems
            @Override
            public void run() {
                //Do something after 100ms
                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                int LAYOUT_FLAG;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                }

                wmCirclePickerParams = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        FLAG_HARDWARE_ACCELERATED|FLAG_NOT_TOUCH_MODAL|FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSPARENT);
                wmCirclePickerParams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                wmCirclePickerView = inflater.inflate(R.layout.circlepicker, null);

                CirclePickerView cpv = wmCirclePickerView.findViewById(R.id.CirclePicker);
                cpv.initWithCustomParams();

                wm.addView(wmCirclePickerView,wmCirclePickerParams);

                CirclePickerService cps = CirclePickerService.Instance.get();
                if(cps!=null){
                    cps.circleStarted=true;
                    cps.setup();// used to set on click listener the close button
                }
            }
        }, 300);
        finish();
    }

    private void showAlertDialog(){
        //set icon
        //set title
        //set message
        //set positive button
        //set what would happen when positive button is clicked
        //set negative button
        //set what should happen when negative button is clicked
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_info)
                //set title
                .setTitle(getResources().getString(R.string.circle_picker_activity_title))
                //set message
                .setMessage(getResources().getString(R.string.circle_picker_activity_message))
                //set positive button
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        @SuppressLint("InlinedApi") Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())); // we only call the alert dialog if we are SDK > 23
                        startActivityForResult(intent, REQUEST_CODE_ACTION_MANAGE_OVERLAY);
                        dialogInterface.dismiss();
                    }
                })
                //set negative button
                .setNegativeButton(getResources().getString(R.string.circle_picker_activity_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        dialogInterface.dismiss();
                        permissionNotGiven();
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }
}