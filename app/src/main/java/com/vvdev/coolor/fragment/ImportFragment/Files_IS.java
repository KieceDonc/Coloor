package com.vvdev.coolor.fragment.ImportFragment;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.vvdev.coolor.R;
import com.vvdev.coolor.activity.MainActivity;
import com.vvdev.coolor.fragment.TabHost.ImportTab;
import com.vvdev.coolor.services.CirclePickerService;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


public class Files_IS extends Fragment {

    public static final String KEY_ARGUMENT_FILES_PATH ="PathToAFile";

    private static String TAG = Files_IS.class.getName();

    private ConstraintLayout startCirclePicker;

    private ImageView Img;
    private VideoView Vid;
    private Uri pathToFile;

    /**
     * use to prevent the call of MainActivity.Instance.get().showViewPager() in OnPause() when starting CirclePickerActivityStart
     */
    private boolean shouldShowViewPager = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity.Instance.get().showFragmentHost();

        View view = inflater.inflate(R.layout.import_files, container, false);
        String toCheck = getArguments().getString(KEY_ARGUMENT_FILES_PATH);
        pathToFile = Uri.parse(toCheck);

        Img = view.findViewById(R.id.import_ImageView); // get view of img
        Vid = view.findViewById(R.id.import_VidView); // get view of img
        startCirclePicker = view.findViewById(R.id.circlePickerStart);
        startCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldShowViewPager=false;
                CirclePickerService.start(getContext());
            }
        });

        ContentResolver contentResolver = getContext().getContentResolver();
        String type = contentResolver.getType(pathToFile);

        if(type!=null){
            if (type.contains("image")) {
                //handle image
                setupImg();
            }else  if(type.contains("video")) {
                //handle video
                setupVid();
            }
        }else{
            setupImg();
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if(shouldShowViewPager){
            MainActivity.Instance.get().showViewPager();
        }else{
            shouldShowViewPager=true;
        }
    }

    private void setupImg() {
        Vid.setVisibility(View.GONE);
        Img.setVisibility(View.VISIBLE);

        Glide.with(this).load(pathToFile).fitCenter().dontAnimate().into(Img); // set img
    }

    private void setupVid(){
        Vid.setVisibility(View.VISIBLE);
        Img.setVisibility(View.GONE);

        Vid.setVideoURI(pathToFile);
    }
}
