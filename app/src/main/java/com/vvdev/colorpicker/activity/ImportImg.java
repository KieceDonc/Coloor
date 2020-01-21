package com.vvdev.colorpicker.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.CirclePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import static com.vvdev.colorpicker.fragment.Import.ImportFragment.IntentExtraImgPath;

public class ImportImg extends AppCompatActivity {

    private boolean canGoToNormalScreen = true;
    private View CirclePickerView;
    private CirclePicker mCirclePicker;
    private ConstraintLayout importImgConstraintLayout;
    private ImageView Img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        //makeActivityFullScreen();
        setContentView(R.layout.import_img);

        importImgConstraintLayout= findViewById(R.id.importImgConstraint); // get root constraint layout
        /*importImgConstraintLayout.setOnClickListener(new View.OnClickListener() { // set on click listener for animation effect
            @Override
            public void onClick(View v) {
                makeActivityNormalScreen();
                canGoToNormalScreen=false;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canGoToNormalScreen=true;
                    }
                }, 2000);
                tryToReturnToNormalScreen();
            }
        });*/

        Intent receiveData = getIntent(); // get intent
        String imgPath = receiveData.getStringExtra(IntentExtraImgPath); // get img path from intent

        Img = findViewById(R.id.importImg_ImageView); // get view of img
        Glide.with(this).load(imgPath).into(Img); // set img

        /**
         * Start circle picker
         */
        LayoutInflater inflater = LayoutInflater.from(this);
        CirclePickerView = inflater.inflate(R.layout.circlepicker,importImgConstraintLayout);
        importImgConstraintLayout.bringChildToFront(CirclePickerView);// make view to first plan
        mCirclePicker = findViewById(R.id.CirclePicker);

        mCirclePicker.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect PhonePickerRect = new Rect();
                importImgConstraintLayout.getGlobalVisibleRect(PhonePickerRect);
                mCirclePicker.setMovableDimension(PhonePickerRect); // give dimension
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void makeActivityFullScreen(){
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }
/*
    public void makeActivityNormalScreen(){
        //requestWindowFeature(Window.);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    public void tryToReturnToNormalScreen(){
        if(canGoToNormalScreen){
            makeActivityNormalScreen();
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tryToReturnToNormalScreen();
                }
            }, 500);
        }
    }*/
}
