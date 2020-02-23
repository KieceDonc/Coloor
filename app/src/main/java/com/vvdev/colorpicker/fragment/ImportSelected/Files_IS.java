package com.vvdev.colorpicker.fragment.ImportSelected;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.vvdev.colorpicker.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Files_IS extends Fragment {

    public static final String KEY_ARGUMENT_FILES_PATH ="PathToAFile";

    private ImageView Img;
    private VideoView Vid;
    private Uri pathToFile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.import_files, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        String toCheck = getArguments().getString(KEY_ARGUMENT_FILES_PATH);
        pathToFile = Uri.parse(toCheck);

        Img = view.findViewById(R.id.import_ImageView); // get view of img
        Vid = view.findViewById(R.id.import_VidView); // get view of img

        String extension = MimeTypeMap.getFileExtensionFromUrl(pathToFile.toString()); // string extension of the file
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension); // get the type of the file
        if (mimeType.contains("image")) {
            //handle image
            setupImg();
        }else  if(mimeType.contains("video")) {
            //handle video
            setupVid();
        }else{
            Log.e("Files_IS","Error trying to load an recognized type file. File path :"+pathToFile.toString()+"\nExtension : "+mimeType);
        }

       /* Intent receiveData = getIntent(); // get intent
        String path = receiveData.getStringExtra(IntentExtraPath); // get img path from intent*/
    }

    private void setupImg(){
        Vid.setVisibility(View.GONE);
        Img.setVisibility(View.VISIBLE);

        Glide.with(this).load(pathToFile).fitCenter().dontAnimate().listener(new RequestListener<Drawable>() {
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
    }

    private void setupVid(){
        Vid.setVisibility(View.VISIBLE);
        Img.setVisibility(View.GONE);

        Vid.setVideoURI(pathToFile);
    }
}
