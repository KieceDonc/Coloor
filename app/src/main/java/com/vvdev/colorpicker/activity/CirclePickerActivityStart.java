package com.vvdev.colorpicker.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ScreenCapture;
import com.vvdev.colorpicker.services.CirclePickerService;
import com.vvdev.colorpicker.ui.CirclePickerView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static com.vvdev.colorpicker.interfaces.ScreenCapture.mMediaProjectionManager;


/**
 * Life cycle :
 *  0- TODO need to ask storage permission
 *  1- request perm to draw over all app
 *  2- request perm to recording screen
 *  3- starting circle picker and finish this activity
 */

public class CirclePickerActivityStart extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static View wmCirclePickerView;
    public static WindowManager.LayoutParams wmCirclePickerParams;

    public static boolean isCirclePickerActivityRunning = false;

    private static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY = 1234;
    private static final int REQUEST_CODE_MEDIA_PROJECTION = 5555;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) { // TODO handle storage permission
        super.onCreate(savedInstanceState);

        isCirclePickerActivityRunning = true;

        WindowManager.LayoutParams wp = getWindow().getAttributes();
        wp.dimAmount = 0f;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                showAlertDialog();
            } else {
                startCapture();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CirclePickerService.waitingForResult=false;
        isCirclePickerActivityRunning=false;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_MEDIA_PROJECTION){
            if (resultCode == Activity.RESULT_OK) {
                ScreenCapture.setUpMediaProjection(resultCode,data);// TODO make it more cleaner in ScreenCapture
                finish();
                startCirclePicker();
            }else if(resultCode == Activity.RESULT_CANCELED){
                permissionNotGiven();
            }else{

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
        CirclePickerService.circleStarted=false;
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
                wm.addView(wmCirclePickerView,wmCirclePickerParams);
                CirclePickerService.circleStarted=true;
                try {
                    CirclePickerService.Instance.getInstance().setOnClickListenerOutsideButton(); // used to set on click listener the close button
                } catch (Exception e) {
                    e.printStackTrace();
                }

                finish();

            }
        }, 500); }

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
                .setIcon(android.R.drawable.ic_dialog_alert)
                //set title
                .setTitle("Warning!")
                //set message
                .setMessage("You need to give the permission to draw over all application, do you want to give the permission ?")
                //set positive button
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        @SuppressLint("InlinedApi") Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION); // we only call the alert dialog if we are SDK > 23
                        startActivityForResult(intent, REQUEST_CODE_ACTION_MANAGE_OVERLAY);
                        dialogInterface.dismiss();
                        permissionNotGiven(); // used to fix a bug
                    }
                })
                //set negative button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
