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
import com.vvdev.colorpicker.activity.Import_DefaultViewer;
import com.vvdev.colorpicker.activity.Import_WebViewer;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ImportFragment extends Fragment implements View.OnClickListener {

    //https://viewerjs.org/
    // load from internet
    // http://bumptech.github.io/glide/doc/getting-started.html

    private FrameLayout mImportImg;
    private FrameLayout mImportVid;
    private FrameLayout mImportDoc;
    private FrameLayout mImportInternet;

    public final static String IntentExtraImgPath = "path";
    public static final int REQUEST_CODE_IMG = 15085;
    public static final int REQUEST_CODE_VID = 15086;
    public static final int REQUEST_CODE_DOC = 15087;
    public static final int REQUEST_CODE_INTERNET =15088;
    public static final int REQUEST_CODE_GDOC =  15089;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_import, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mImportImg = view.findViewById(R.id.ImportImg);
        mImportVid = view.findViewById(R.id.ImportVid);
        mImportDoc = view.findViewById(R.id.ImportDoc);
        mImportInternet = view.findViewById(R.id.ImportInternet);

        mImportImg.setOnClickListener(this);
        mImportVid.setOnClickListener(this);
        mImportDoc.setOnClickListener(this);
        mImportInternet.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            if(data.getData()!=null){
                String path = getRealPathFromURI(data.getData());
                switch (requestCode) {
                    case REQUEST_CODE_IMG: {
                        loadDefaultViewer(path);
                        break;
                    }
                    case REQUEST_CODE_VID: {// TODO check .mp4 etc ..
                        loadDefaultViewer(path);
                        break;
                    }
                    case REQUEST_CODE_DOC: {
                        loadWebViewer(path);
                        break;
                    }
                    case REQUEST_CODE_INTERNET: {
                        break;
                    }
                    case REQUEST_CODE_GDOC: {
                        break;
                    }
                }
            }else{
                Log.e("ImportFragment","Import fragment errort at onActivityResult, data.getData() null.\n data values : "+data);
            }
        }else{
            Log.e("ImportFragment","Import fragment errort at onActivityResult, data null");
        }

    }

    private String getRealPathFromURI(Uri contentUri) { // https://stackoverflow.com/questions/12714701/deprecated-managedquery-issue
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ImportImg:{
                chooseImg();
                break;
            }
            case R.id.ImportVid:{
                chooseVid();
                break;
            }
            case R.id.ImportDoc:{
                chooseDoc();
                break;
            }
            case R.id.ImportInternet:{
                choosenByInternet();
                break;
            }
        }
    }

    private void chooseImg(){
        String type="image/*";
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType(type);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType(type);

        Intent chooserIntent = Intent.createChooser(getIntent, "Select an Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_IMG);
    }

    private void chooseVid(){
        String type="video/*";
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType(type);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType(type);

        Intent chooserIntent = Intent.createChooser(getIntent, "Select a video");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_VID);
    }

    private void chooseDoc(){
        String type="application/pdf";
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType(type);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType(type);

        Intent chooserIntent = Intent.createChooser(getIntent, "Select a pdf file");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_DOC);
    }

    private void choosenByInternet(){

    }

    private void loadDefaultViewer(String path){
        Intent startPreview = new Intent(getActivity(), Import_DefaultViewer.class);
        startPreview.putExtra(IntentExtraImgPath, path);
        startActivity(startPreview);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }

    private void loadWebViewer(String path){
        Intent startPreview = new Intent(getActivity(), Import_WebViewer.class);
        startPreview.putExtra(IntentExtraImgPath, path);
        startActivity(startPreview);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }
}