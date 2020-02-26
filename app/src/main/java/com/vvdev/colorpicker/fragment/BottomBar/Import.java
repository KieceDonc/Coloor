package com.vvdev.colorpicker.fragment.BottomBar;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.fragment.ImportSelected.Camera;
import com.vvdev.colorpicker.fragment.ImportSelected.Files_IS;
import com.vvdev.colorpicker.fragment.ImportSelected.PDF;
import com.vvdev.colorpicker.interfaces.FilesExtensionType;
import com.vvdev.colorpicker.interfaces.PermissionCustom;
import com.vvdev.colorpicker.ui.DownloadFileAlertDialog;

import java.io.File;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static com.vvdev.colorpicker.fragment.ImportSelected.Files_IS.KEY_ARGUMENT_FILES_PATH;
import static com.vvdev.colorpicker.fragment.ImportSelected.PDF.KEY_ARGUMENT_PDF_PATH;

public class Import extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_FILE = 15086;
    private static final int REQUEST_CODE_PDF = 15087;
    private static final int REQUEST_CODE_GDOC =  15089;

    private static final int REQUEST_CODE_PERM_PDF = 535;
    private static final int REQUEST_CODE_PERM_CAMERA = 536;
    private static final int REQUEST_CODE_PERM_FILES = 567;
    private static final int REQUEST_CODE_PERM_INTERNET = 568;

    private RelativeLayout mImportCamera;
    private RelativeLayout mImportFile;
    private RelativeLayout mImportPDF;
    private RelativeLayout mImportInternet;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.importPDF:{
                if(PermissionCustom.isWriteAndWritePermissionGiven(getContext())){
                    choosePDF();
                }else{
                    PermissionCustom.askReadAndWritePermissions(getActivity(),REQUEST_CODE_PERM_PDF);
                }
                break;
            }
            case R.id.importCamera:{
                if(PermissionCustom.isReadPermissionGiven(getContext())){
                    loadCamera();
                }else {
                    PermissionCustom.askCameraPermission(getActivity(),REQUEST_CODE_PERM_CAMERA);
                }
                break;
            }
            case R.id.importFile:{
                if(PermissionCustom.isWriteAndWritePermissionGiven(getContext())){
                    chooseFile();
                }else{
                    PermissionCustom.askReadAndWritePermissions(getActivity(),REQUEST_CODE_PERM_FILES);
                }
                break;
            }
            case R.id.importInternet:{
                if(PermissionCustom.isWriteAndWritePermissionGiven(getContext())){
                    chooseInternet();
                }else{
                    PermissionCustom.askReadAndWritePermissions(getActivity(),REQUEST_CODE_PERM_INTERNET);
                }
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
                    case REQUEST_CODE_FILE: {
                        loadFile(data.getData());
                        break;
                    }
                    case REQUEST_CODE_PDF: {
                        loadPDF(data.getData());
                        break;
                    }
                    case REQUEST_CODE_GDOC: {
                        break;
                    }
                }
            }else{
                Log.e("Import","Import fragment error at onActivityResult, data.getData() null.\nData values : "+data);
            }
        }else if(resultCode==PackageManager.PERMISSION_GRANTED){
            switch (requestCode){
                case REQUEST_CODE_PERM_PDF :{
                    choosePDF();
                    break;
                }
                case REQUEST_CODE_PERM_CAMERA:{
                    loadCamera();
                    break;
                }
                case REQUEST_CODE_PERM_FILES:{
                    chooseFile();
                    break;
                }
                case REQUEST_CODE_PERM_INTERNET:{
                    chooseInternet();
                }
            }
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

    private void chooseInternet(){
        final Context c = getContext();
        new DownloadFileAlertDialog(getContext(), getActivity(), new DownloadFileAlertDialog.setOnListener() {
            @Override
            public void onFileDownloaded(String filePath) {

            File downloadedFile = new File(filePath); // we get the file downloaded
            Uri uriDownloadedFile = Uri.fromFile(downloadedFile); // we get his uri

            String extension = FilesExtensionType.getFileExtension(getContext(),uriDownloadedFile); // string extension of downloaded file
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension); // get the type of the file
            if(mimeType!=null) {
                if (mimeType.contains("image") || mimeType.contains("video")) {
                    loadFile(uriDownloadedFile);
                } else if (mimeType.contains("pdf")){
                    loadPDF(uriDownloadedFile);
                }
            }else{
                Log.e("Import","onFileDownloaded extension error\nFile path : "+filePath+"\nExtension : "+extension+"\nMimeType : null of course");
                Toast.makeText(c,"Error, invalid file",Toast.LENGTH_LONG).show();
            }
        }
        }).show();
    }

    private void loadPDF(Uri path){
        PDF pdfFragment= new PDF();

        Bundle bundle  = new Bundle(); // use to send data to the fragment
        bundle.putString(KEY_ARGUMENT_PDF_PATH,path.toString()); // plz refer to https://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments
        pdfFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, pdfFragment)
                .disallowAddToBackStack()
                .commit(); // https://stackoverflow.com/questions/21028786/how-do-i-open-a-new-fragment-from-another-fragment
    }

    private void loadCamera(){
        Camera cameraFragment= new Camera();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, cameraFragment)
                .disallowAddToBackStack()
                .commit(); // https://stackoverflow.com/questions/21028786/how-do-i-open-a-new-fragment-from-another-fragment
    }
    private void loadFile(Uri path){
        Files_IS filesFragment = new Files_IS();

        Bundle bundle  = new Bundle(); // use to send data to the fragment
        bundle.putString(KEY_ARGUMENT_FILES_PATH,path.toString()); // plz refer to https://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments
        filesFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, filesFragment)
                .disallowAddToBackStack()
                .commit(); // https://stackoverflow.com/questions/21028786/how-do-i-open-a-new-fragment-from-another-fragment
    }


}