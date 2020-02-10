package com.vvdev.colorpicker.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.Service.ScreenCapture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.vvdev.colorpicker.Service.ScreenCapture.REQUEST_MEDIA_PROJECTION;


public class CirclePickerActivity extends AppCompatActivity {

    public static ScreenCapture mScreenCapture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreenCapture = ScreenCapture.newInstance(this);
        mScreenCapture.screenCapture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mScreenCapture.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_MEDIA_PROJECTION){
            if (resultCode == Activity.RESULT_OK) {
                startCirclePicker();
            }else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
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
                4564564,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.circlepicker, null);
        wm.addView(myView,params);
    }
}
