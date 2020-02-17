package com.vvdev.colorpicker.fragment.BottomBar;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.fragment.ImportSelected.Camera;
import com.vvdev.colorpicker.fragment.ImportSelected.Files_IS;
import com.vvdev.colorpicker.fragment.ImportSelected.PDF;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static com.vvdev.colorpicker.fragment.ImportSelected.Files_IS.KEY_ARGUMENT_FILES_PATH;
import static com.vvdev.colorpicker.fragment.ImportSelected.PDF.KEY_ARGUMENT_PDF_PATH;

public class Import extends Fragment implements View.OnClickListener {

    private RelativeLayout mImportCamera;
    private RelativeLayout mImportFile;
    private RelativeLayout mImportPDF;
    private RelativeLayout mImportInternet;

    private static final int REQUEST_CODE_FILE = 15086;
    private static final int REQUEST_CODE_PDF = 15087;
    private static final int REQUEST_CODE_INTERNET =15088;
    private static final int REQUEST_CODE_GDOC =  15089;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_import, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        mImportCamera = view.findViewById(R.id.importCamera);
        mImportFile = view.findViewById(R.id.importFile);
        mImportPDF = view.findViewById(R.id.importPDF);
        mImportInternet = view.findViewById(R.id.importInternet);

        mImportCamera.setOnClickListener(this);
        mImportFile.setOnClickListener(this);
        mImportPDF.setOnClickListener(this);
        mImportInternet.setOnClickListener(this);
        askPermissions();
    }

    @TargetApi(23)
    private void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.importPDF:{
                choosePDF();
                break;
            }
            case R.id.importCamera:{
                loadCamera(); // TODO ask camera permission
                break;
            }
            case R.id.importFile:{
                chooseFile(); // TODO ask write / read external storage permission
                break;
            }
            case R.id.importInternet:{
                chosenByInternet();
                break;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            if(data.getData()!=null){
                //String path = FileUtils.getPath(getContext(),data.getData());
                switch (requestCode) {
                    case REQUEST_CODE_FILE: {// TODO check .mp4 etc ..
                        loadFile(data.getData());
                        break;
                    }
                    case REQUEST_CODE_PDF: {
                        loadPDF(data.getData());
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
                Log.e("Import","Import fragment errort at onActivityResult, data.getData() null.\n data values : "+data);
            }
        }else{
            Log.e("Import","Import fragment errort at onActivityResult, data null");
        }

    }


    private void choosePDF(){
        String type="application/pdf";
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType(type);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType(type);

        Intent chooserIntent = Intent.createChooser(getIntent, "Select a pdf file");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_PDF);
    }

    private void chooseFile(){
        String type="image/* video/*";
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType(type);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType(type);

        Intent chooserIntent = Intent.createChooser(getIntent, "Select a video / image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_FILE);
    }

    private void chosenByInternet(){
    }

    private void loadPDF(Uri path){
        PDF pdfFragment= new PDF();

        Bundle bundle  = new Bundle(); // use to send data to the fragment
        bundle.putString(KEY_ARGUMENT_PDF_PATH,path.toString()); // plz refer to https://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments
        pdfFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, pdfFragment, "findThisFragment")
                .addToBackStack(null)
                .commit(); // https://stackoverflow.com/questions/21028786/how-do-i-open-a-new-fragment-from-another-fragment
    }

    private void loadCamera(){
        Camera cameraFragment= new Camera();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, cameraFragment, "findThisFragment")
                .addToBackStack(null)
                .commit(); // https://stackoverflow.com/questions/21028786/how-do-i-open-a-new-fragment-from-another-fragment
    }
    private void loadFile(Uri path){
        Files_IS filesFragment = new Files_IS();

        Bundle bundle  = new Bundle(); // use to send data to the fragment
        bundle.putString(KEY_ARGUMENT_FILES_PATH,path.toString()); // plz refer to https://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments
        filesFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, filesFragment, "findThisFragment")
                .addToBackStack(null)
                .commit(); // https://stackoverflow.com/questions/21028786/how-do-i-open-a-new-fragment-from-another-fragment

    }


}