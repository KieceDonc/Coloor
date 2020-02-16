package com.vvdev.colorpicker.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ScreenCapture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static com.vvdev.colorpicker.interfaces.ScreenCapture.mMediaProjectionManager;


/**
 * Life cycle :
 *  0- TODO need to ask storage permission
 *  1- request perm to draw over all app
 *  2- request perm to recording screen
 *  3- starting circle picker and finish this activity
 */

public class StartCirclePicker extends AppCompatActivity {

    public static View wmCirclePickerView;

    private static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY = 1234;
    private static final int REQUEST_CODE_MEDIA_PROJECTION = 5555;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) { // TODO handle storage permission
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                setupAlertDialog();
                AlertDialog alertDialog = builder.create();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                } else {
                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                }
                alertDialog.show();
            } else {
                startCapture();
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ACTION_MANAGE_OVERLAY){
            if(!Settings.canDrawOverlays(this)){
                startCapture();
            }else{
                permissionNotGiven();
            }
        }

        if(requestCode == REQUEST_CODE_MEDIA_PROJECTION){
            if (resultCode == Activity.RESULT_OK) {
                ScreenCapture.setUpMediaProjection(resultCode,data);// TODO make it more cleaner in ScreenCapture
                startCirclePicker();
            }else if (resultCode == Activity.RESULT_CANCELED) {
                permissionNotGiven();
            }
        }
    }

    private void permissionNotGiven(){ ;
        finish();
    }

    private void startCapture(){
        mMediaProjectionManager = (MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_MEDIA_PROJECTION);
    }

    private void startCirclePicker(){
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE); // TODO ask permission to draw over other app
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                FLAG_HARDWARE_ACCELERATED|FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        wmCirclePickerView = inflater.inflate(R.layout.circlepicker, null);
        wm.addView(wmCirclePickerView,params);

        finish();
    }

    private void setupAlertDialog(){
        builder = new AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)

                //set title
                .setTitle("Warning!")
                //set message
                .setMessage("You need to give the permission to draw over all application, also ColorPicker won't be able to work !")
                //set positive button
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivityForResult(intent, REQUEST_CODE_ACTION_MANAGE_OVERLAY);
                        dialogInterface.dismiss();
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
                });
    }
}
