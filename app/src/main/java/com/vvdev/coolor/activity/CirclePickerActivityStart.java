package com.vvdev.coolor.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.ScreenCapture;
import com.vvdev.coolor.services.CirclePickerService;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static com.vvdev.coolor.interfaces.ScreenCapture.mMediaProjectionManager;

public class CirclePickerActivityStart extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static View wmCirclePickerView;
    public static WindowManager.LayoutParams wmCirclePickerParams;

    private static final String TAG = CirclePickerActivityStart.class.getName();

    private final ActivityResultLauncher<Intent> drawOverAllAppsPerm = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (result) -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ScreenCapture.setUpMediaProjection(result.getResultCode(),result.getData());
                    startCirclePicker();
                }else{
                    CirclePickerService.Instance.get().onCirclePickerPermissionDenied();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams wp = getWindow().getAttributes();
        wp.dimAmount = 0f;

        startCapture();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void startCapture(){
        mMediaProjectionManager = (MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        drawOverAllAppsPerm.launch(mMediaProjectionManager.createScreenCaptureIntent());
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

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CirclePickerService.Instance.get().onCirclePickerPermissionGiven();
                    }
                },50);
            }
        }, 300);
        finish();
    }
}
