package com.vvdev.colorpicker.fragment.Import;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.activity.Import_Img;
import com.vvdev.colorpicker.activity.Import_PDF;
import com.vvdev.colorpicker.interfaces.FileUtils;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ImportFragment extends Fragment implements View.OnClickListener {

    //https://viewerjs.org/
    // load from internet
    // http://bumptech.github.io/glide/doc/getting-started.html

    private RelativeLayout mImportImg;
    private RelativeLayout mImportVid;
    private RelativeLayout mImportDoc;
    private RelativeLayout mImportInternet;

    public final static String IntentExtraPath = "path";
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        mImportImg = view.findViewById(R.id.importImg);
        mImportVid = view.findViewById(R.id.importVid);
        mImportDoc = view.findViewById(R.id.importDoc);
        mImportInternet = view.findViewById(R.id.importInternet);

        mImportImg.setOnClickListener(this);
        mImportVid.setOnClickListener(this);
        mImportDoc.setOnClickListener(this);
        mImportInternet.setOnClickListener(this);
        askPermissions();
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            if(data.getData()!=null){
                String path = FileUtils.getPath(getContext(),data.getData());
                switch (requestCode) {
                    case REQUEST_CODE_IMG: {
                        loadImgView(path);
                        break;
                    }
                    case REQUEST_CODE_VID: {// TODO check .mp4 etc ..
                        loadImgView(path);
                        break;
                    }
                    case REQUEST_CODE_DOC: {
                        loadPDFView(data.getData());

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.importImg:{
                chooseImg();
                break;
            }
            case R.id.importVid:{
                chooseVid();
                break;
            }
            case R.id.importDoc:{
                chooseDoc();
                break;
            }
            case R.id.importInternet:{
                chosenByInternet();
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

    private void chosenByInternet(){
    }

    private void loadImgView(String path){
        Intent startPreview = new Intent(getActivity(), Import_Img.class);
        startPreview.putExtra(IntentExtraPath, path);
        startActivity(startPreview);
    }

    private void loadPDFView(Uri path){
        Intent startPreview = new Intent(getActivity(), Import_PDF.class);
        startPreview.putExtra(IntentExtraPath, path.toString());
        startActivity(startPreview);
    }


}