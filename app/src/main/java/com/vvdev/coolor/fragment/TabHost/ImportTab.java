package com.vvdev.coolor.fragment.TabHost;

import android.Manifest;
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
import android.widget.Toast;

import com.vvdev.coolor.R;
import com.vvdev.coolor.activity.MainActivity;
import com.vvdev.coolor.fragment.ImportFragment.Camera;
import com.vvdev.coolor.fragment.ImportFragment.Files_IS;
import com.vvdev.coolor.fragment.ImportFragment.PDF;
import com.vvdev.coolor.interfaces.FilesExtensionType;
import com.vvdev.coolor.services.CirclePickerService;
import com.vvdev.coolor.ui.alertdialog.DownloadFile;
import com.vvdev.coolor.ui.alertdialog.DownloadInfo;

import java.io.File;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.vvdev.coolor.fragment.ImportFragment.Files_IS.KEY_ARGUMENT_FILES_PATH;
import static com.vvdev.coolor.fragment.ImportFragment.PDF.KEY_ARGUMENT_PDF_PATH;

public class ImportTab extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_FILE = 15086;
    private static final int REQUEST_CODE_PDF = 15087;
    private static final int REQUEST_CODE_GDOC =  15089;

    private static final int REQUEST_CODE_PERM_PDF = 535;
    private static final int REQUEST_CODE_PERM_CAMERA = 536;
    private static final int REQUEST_CODE_PERM_FILES = 567;
    private static final int REQUEST_CODE_PERM_INTERNET = 568;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_import, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        view.findViewById(R.id.importCamera).setOnClickListener(this);  // set camera rectangle on click listener
        view.findViewById(R.id.importFile).setOnClickListener(this);    // set file rectangle on click listener
        view.findViewById(R.id.importPDF).setOnClickListener(this);     // set pdf rectangle on click listener
        view.findViewById(R.id.importInternet).setOnClickListener(this);// set internet rectangle on click listener
        view.findViewById(R.id.importInternet).setOnClickListener(this);
        view.findViewById(R.id.importTabABCirclePicker).setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.importIntentInfo:{
                DownloadInfo downloadInfo = new DownloadInfo(getContext());
                downloadInfo.show();
                break;
            }
            case R.id.importPDF:{
                if(isWriteAndWritePermissionGiven()){
                    choosePDF();
                }else{
                    askReadAndWritePermissions(REQUEST_CODE_PERM_PDF);
                }
                break;
            }
            case R.id.importCamera:{
                if(isReadPermissionGiven()){
                    loadCamera();
                }else {
                    askCameraPermission(REQUEST_CODE_PERM_CAMERA);
                }
                break;
            }
            case R.id.importFile:{
                if(isWriteAndWritePermissionGiven()){
                    chooseFile();
                }else{
                    askReadAndWritePermissions(REQUEST_CODE_PERM_FILES);
                }
                break;
            }
            case R.id.importInternet:{
                if(isWriteAndWritePermissionGiven()){
                    chooseInternet();
                }else{
                    askReadAndWritePermissions(REQUEST_CODE_PERM_INTERNET);
                }
                break;
            }
            case R.id.importTabABCirclePicker:{
                CirclePickerService.start(getContext());
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            if(data.getData()!=null){
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_PERM_PDF :{
                if(isWriteAndWritePermissionGiven()){
                    choosePDF();
                }else{
                    permissionDeniedShowToast();
                }
                break;
            }
            case REQUEST_CODE_PERM_CAMERA:{
                if(isCameraPermissionGiven()){
                    loadCamera();
                }else{
                    permissionDeniedShowToast();
                }
                break;
            }
            case REQUEST_CODE_PERM_FILES:{
                if(isWriteAndWritePermissionGiven()){
                    chooseFile();
                }else{
                    permissionDeniedShowToast();
                }
                break;
            }
            case REQUEST_CODE_PERM_INTERNET:{
                if(isWriteAndWritePermissionGiven()){
                    chooseInternet();
                }else{
                    permissionDeniedShowToast();
                }
            }
        }
    }

    private void permissionDeniedShowToast(){
        Toast.makeText(getContext(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
    }

    private void choosePDF(){
        String type="application/pdf";
        String title=getResources().getString(R.string.import_pdf_select);

        startChooser(type,title,REQUEST_CODE_PDF);
    }

    private void chooseFile(){
        String type="image/* video/*";
        String title=getResources().getString(R.string.import_files_select);
        startChooser(type,title,REQUEST_CODE_FILE);
    }

    private void startChooser(String type, String title,int requestCode){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType(type);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType(type);

        Intent chooserIntent = Intent.createChooser(getIntent, title);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, requestCode);
    }

    private void chooseInternet(){
        final Context c = getContext();
        new DownloadFile(getContext(), getActivity(), new DownloadFile.setOnListener() {
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
                Toast.makeText(c,getActivity().getResources().getString(R.string.import_error_file),Toast.LENGTH_LONG).show();
            }
        }
        }).show();
    }

    private void loadPDF(Uri path){
        PDF pdfFragment= new PDF();

        Bundle bundle  = new Bundle(); // use to send data to the fragment
        bundle.putString(KEY_ARGUMENT_PDF_PATH,path.toString()); // plz refer to https://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments
        pdfFragment.setArguments(bundle);

        doFragmentTransaction(pdfFragment);
    }

    private void loadCamera(){
        try{
            Camera cameraFragment= new Camera();
            doFragmentTransaction(cameraFragment);
        }catch (RuntimeException ex){
            Toast.makeText(getContext(),"Error while loading camera",Toast.LENGTH_LONG).show();
        }
    }

    private void loadFile(Uri path){
        Files_IS filesFragment = new Files_IS();

        Bundle bundle  = new Bundle(); // use to send data to the fragment
        bundle.putString(KEY_ARGUMENT_FILES_PATH,path.toString()); // plz refer to https://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments
        filesFragment.setArguments(bundle);

        doFragmentTransaction(filesFragment);
    }

    private void doFragmentTransaction(Fragment fragment){
        //switching fragment
        if (fragment != null) {
            String backStateName = fragment.getClass().getName();

            FragmentManager manager = getActivity().getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0); //https://stackoverflow.com/questions/18305945/how-to-resume-fragment-from-backstack-if-exists

            if (!fragmentPopped){ //fragment not in back stack, create it.
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(backStateName)
                        .replace(R.id.nav_host_fragment, fragment)
                        .commit();
            }
        }
    }

    private void askReadAndWritePermissions(int requestCode){
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
    }

    private void askCameraPermission(int requestCode){
        requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
    }

    private boolean isWriteAndWritePermissionGiven(){
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isReadPermissionGiven(){
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isCameraPermissionGiven(){
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED;
    }


}