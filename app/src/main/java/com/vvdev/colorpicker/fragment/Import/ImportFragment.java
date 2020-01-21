package com.vvdev.colorpicker.fragment.Import;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.activity.ImportImg;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ImportFragment extends Fragment {

    //https://viewerjs.org/
    // load from internet
    // http://bumptech.github.io/glide/doc/getting-started.html

    private FrameLayout mFrameLayoutImg;

    public static final int REQUEST_CODE_IMG = 15085;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_import, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mFrameLayoutImg = view.findViewById(R.id.frameLayoutImg);
        mFrameLayoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, REQUEST_CODE_IMG);
            }
        });
    }

    public static String IntentExtraImgPath = "imgPath";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IMG) {
            if(data!=null&&data.getData() != null){
               String imgPath = getRealPathFromURI(data.getData());
               Intent startPreview = new Intent(getActivity(), ImportImg.class);
               startPreview.putExtra(IntentExtraImgPath, imgPath);
               startActivity(startPreview);
               Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            }else{
                Log.e("ImportFragment","Import fragment error at onActivityResult, null");
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) { // https://stackoverflow.com/questions/12714701/deprecated-managedquery-issue
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(contentUri, proj, null, null, null);
        if(Objects.requireNonNull(cursor).moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

}