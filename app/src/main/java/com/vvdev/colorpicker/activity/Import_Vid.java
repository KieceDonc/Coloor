package com.vvdev.colorpicker.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.CirclePickerOld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import static android.view.View.inflate;
import static com.vvdev.colorpicker.fragment.Import.ImportFragment.IntentExtraPath;
import static com.vvdev.colorpicker.ui.CirclePickerOld.timeUpdateCirclePicker;


public class Import_Vid extends AppCompatActivity {

    private View CirclePickerView;
    private CirclePickerOld mCirclePicker;
    private ConstraintLayout constraintLayout;
    private VideoView Vid;
    private boolean circlePickerAlreadyAdded = false;
    private boolean circleViewVisibility=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.import_vid);
        constraintLayout= findViewById(R.id.import_DefaultViewerConstraint); // get root constraint layout

        Intent receiveData = getIntent(); // get intent
        Uri path = Uri.parse(receiveData.getStringExtra(IntentExtraPath)); // get img path from intent

        Vid = findViewById(R.id.import_VidView); // get view of vid
        Vid.setVideoURI(path);
        Vid.getRootView().getContext();

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
                    CirclePickerView = inflate(c,R.layout.circlepicker,constraintLayout);
                    constraintLayout.bringChildToFront(CirclePickerView);// make view to first plan
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

