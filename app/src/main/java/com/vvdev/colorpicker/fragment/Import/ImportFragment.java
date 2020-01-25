package com.vvdev.colorpicker.fragment.Import;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.activity.Import_Img;
import com.vvdev.colorpicker.activity.Import_PDF;
import com.vvdev.colorpicker.interfaces.FileUtils;

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
                String path = FileUtils.getPath(getContext(),data.getData());
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
                        loadWebViewer(data.getData());
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

    private void loadDefaultViewer(String path){
        Intent startPreview = new Intent(getActivity(), Import_Img.class);
        startPreview.putExtra(IntentExtraPath, path);
        startActivity(startPreview);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }

    private void loadWebViewer(Uri path){
        Intent startPreview = new Intent(getActivity(), Import_PDF.class);
        startPreview.putExtra(IntentExtraPath, path.toString());
        startActivity(startPreview);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }
}