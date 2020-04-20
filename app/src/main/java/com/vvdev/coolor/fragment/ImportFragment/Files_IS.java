package com.vvdev.coolor.fragment.ImportFragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.vvdev.coolor.R;
import com.vvdev.coolor.activity.MainActivity;
import com.vvdev.coolor.interfaces.FilesExtensionType;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_files, container, false);
        String toCheck = getArguments().getString(KEY_ARGUMENT_FILES_PATH);
        pathToFile = Uri.parse(toCheck);

        Img = view.findViewById(R.id.import_ImageView); // get view of img
        Vid = view.findViewById(R.id.import_VidView); // get view of img
        startCirclePicker = view.findViewById(R.id.circlePickerStart);
        startCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.startCirclePickerService(getContext());
            }
        });

        String fileExtension = FilesExtensionType.getFileExtension(getContext(),pathToFile);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension); // get the type of the file*/
        if(mimeType!=null){
            if (mimeType.contains("image")) {
                //handle image
                setupImg();
            }else  if(mimeType.contains("video")) {
                //handle video
                setupVid();
            }
        }else{
            try{
                setupImg();
            }catch (Exception e1){
                try {
                    setupVid();
                }catch (Exception e2){
                    e1.printStackTrace();
                    e2.printStackTrace();
                }
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

    }

    private void setupImg(){
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
