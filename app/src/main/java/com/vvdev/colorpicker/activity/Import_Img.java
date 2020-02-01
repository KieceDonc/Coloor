package com.vvdev.colorpicker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.CirclePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import static android.view.View.inflate;
import static com.vvdev.colorpicker.fragment.Import.ImportFragment.IntentExtraPath;
import static com.vvdev.colorpicker.ui.CirclePicker.timeUpdateCirclePicker;

public class Import_Img extends AppCompatActivity {

    private View CirclePickerView;
    private CirclePicker mCirclePicker;
    private ConstraintLayout importImgConstraintLayout;
    private ImageView Img;
    private boolean circlePickerAlreadyAdded = false;
    private boolean circleViewVisibility=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.import_img);
        importImgConstraintLayout= findViewById(R.id.import_DefaultViewerConstraint); // get root constraint layout

        Intent receiveData = getIntent(); // get intent
        String path = receiveData.getStringExtra(IntentExtraPath); // get img path from intent

        Img = findViewById(R.id.import_ImageView); // get view of img
        Glide.with(this).load(path).fitCenter().into(Img); // set img

        //TODO request both permission ( write / read external storage )
        setupCirclePicker();
    }

    private void setupCirclePicker(){
        final Context c = this;
        findViewById(R.id.startCirclePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!circlePickerAlreadyAdded){
                    circlePickerAlreadyAdded=true;
                    CirclePickerView = inflate(c,R.layout.circlepicker,importImgConstraintLayout);
                    importImgConstraintLayout.bringChildToFront(CirclePickerView);// make view to first plan
                    mCirclePicker = findViewById(R.id.CirclePicker);
                }else if(circleViewVisibility){
                    circleViewVisibility=false;
                    mCirclePicker.setVisibility(View.GONE);
                }else{
                    mCirclePicker.setVisibility(View.INVISIBLE);
                    mCirclePicker.updatePhoneBitmap();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCirclePicker.setVisibility(View.VISIBLE);
                        }
                    }, timeUpdateCirclePicker+50);
                    circleViewVisibility=true;
                }
            }
        });
    }

}
