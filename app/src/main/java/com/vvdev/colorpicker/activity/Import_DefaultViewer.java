package com.vvdev.colorpicker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
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

public class Import_DefaultViewer extends AppCompatActivity {

    private View CirclePickerView;
    private CirclePicker mCirclePicker;
    private ConstraintLayout importImgConstraintLayout;
    private ImageView Img;
    private boolean circlePickerAlreadyAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.import_defaultviewer);

        importImgConstraintLayout= findViewById(R.id.import_DefaultViewerConstraint); // get root constraint layout


        Intent receiveData = getIntent(); // get intent
        String path = receiveData.getStringExtra(IntentExtraPath); // get img path from intent

        Img = findViewById(R.id.import_ImageView); // get view of img
        Glide.with(this).load(path).fitCenter().into(Img); // set img

        //TODO request both permission ( write / read external storage )

        final Context c = this;
        findViewById(R.id.startCirclePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Start circle picker
                 */
                if(!circlePickerAlreadyAdded){
                    circlePickerAlreadyAdded=true;
                    CirclePickerView = inflate(c,R.layout.circlepicker,importImgConstraintLayout);
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
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
