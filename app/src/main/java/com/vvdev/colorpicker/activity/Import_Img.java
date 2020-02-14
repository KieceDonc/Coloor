package com.vvdev.colorpicker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.services.CirclePickerService;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.vvdev.colorpicker.fragment.Import.ImportFragment.IntentExtraPath;
import static com.vvdev.colorpicker.ui.CirclePicker.timeUpdateCirclePicker;

public class Import_Img extends AppCompatActivity {

    private static int LAUNCH_SECOND_ACTIVITY = 1546;
    private View rootView;
    private ImageView Img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.import_img);
        rootView = findViewById(R.id.import_DefaultViewerConstraint).getRootView();

        Intent receiveData = getIntent(); // get intent
        String path = receiveData.getStringExtra(IntentExtraPath); // get img path from intent

        Img = findViewById(R.id.import_ImageView); // get view of img
        Glide.with(this).load(path).fitCenter().dontAnimate().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.e("IMAGE_EXCEPTION", "Exception " + e.getMessage());
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(Img); // set img

        //TODO request both permission ( write / read external storage )
        handleButtonCirclePicker();
    }

    private boolean circlePickerAlreadyAdded = false;
    private void handleButtonCirclePicker(){
        final AppCompatActivity activity = this;
        final Context c = this;
        findViewById(R.id.startCirclePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!circlePickerAlreadyAdded){
                    circlePickerAlreadyAdded=true;
                    Intent CirclePickerServiceIntent = new Intent(activity, CirclePickerService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        c.startForegroundService(CirclePickerServiceIntent);
                    }else{
                        startService(CirclePickerServiceIntent);
                    }
                }/*else if(rootView.findViewById(R.layout.circlepicker)){
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
                }*/
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                circlePickerAlreadyAdded=true;
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
