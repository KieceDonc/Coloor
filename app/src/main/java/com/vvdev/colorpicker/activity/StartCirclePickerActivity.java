package com.vvdev.colorpicker.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.service.ScreenCapture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static com.vvdev.colorpicker.service.ScreenCapture.REQUEST_MEDIA_PROJECTION;

/**
 * Life cycle :
 *  0- TODO need to ask storage permission
 *  1- request perm to draw over all app
 *  2- request perm to recording screen
 *  3- starting circle picker and finish this activity
 */
public class StartCirclePickerActivity extends AppCompatActivity {

    public static ScreenCapture mScreenCapture;

    private static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY = 1234;

    private AlertDialog.Builder builder = new AlertDialog.Builder(this)
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( ContextCompat.checkSelfPermission( this, Settings.ACTION_MANAGE_OVERLAY_PERMISSION ) != PackageManager.PERMISSION_GRANTED ) {
            AlertDialog alertDialog = builder.create();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            }else{
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            alertDialog.show();
        }else{
            startCapture();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ACTION_MANAGE_OVERLAY){
            if(resultCode == Activity.RESULT_OK){
                startCapture();
            }else{
                permissionNotGiven();
            }
        }

        if(requestCode == REQUEST_MEDIA_PROJECTION){
            mScreenCapture.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                startCirclePicker();
            }else if (resultCode == Activity.RESULT_CANCELED) {
                permissionNotGiven();
            }
        }
    }

    /**
     * Handle permission here. Like Manifest.permission.WRITE_EXTERNAL_STORAGE
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mScreenCapture != null) {
            mScreenCapture.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void permissionNotGiven(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void startCapture(){
        mScreenCapture = ScreenCapture.newInstance(this);
        mScreenCapture.screenCapture();
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
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                4656455,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.circlepicker, null);
        wm.addView(myView,params);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
