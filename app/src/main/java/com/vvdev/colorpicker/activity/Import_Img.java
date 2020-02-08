package com.vvdev.colorpicker.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.CirclePicker;

import androidx.annotation.Nullable;
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
        Log.e("test",path+"");

        Img = findViewById(R.id.import_ImageView); // get view of img
        Glide.with(this).load(path).fitCenter().dontAnimate().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.e("IMAGE_EXCEPTION", "Exception " + e.getMessage());
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.e("IMAGE_EXCEPTION","Sometimes the image is not loaded and this text is not displayed");
                return false;
            }
        }).into(Img); // set img

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
