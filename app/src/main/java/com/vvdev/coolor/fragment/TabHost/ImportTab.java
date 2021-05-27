package com.vvdev.coolor.fragment.TabHost;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vvdev.coolor.R;
import com.vvdev.coolor.databinding.FragmentImportBinding;
import com.vvdev.coolor.fragment.ImportFragment.Camera;
import com.vvdev.coolor.fragment.ImportFragment.Files_IS;
import com.vvdev.coolor.fragment.ImportFragment.PDF;
import com.vvdev.coolor.ui.alertdialog.DownloadFile;
import com.vvdev.coolor.ui.alertdialog.DownloadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.vvdev.coolor.fragment.ImportFragment.Files_IS.KEY_ARGUMENT_FILES_PATH;
import static com.vvdev.coolor.fragment.ImportFragment.PDF.KEY_ARGUMENT_PDF_PATH;

public class ImportTab extends Fragment implements View.OnClickListener {

    private final ActivityResultLauncher<String> cameraPerm = registerForActivityResult(
        new ActivityResultContracts.RequestPermission(),
        (Boolean result)->{
            if (result) {
                loadCamera();
            }else{
                Toast.makeText(getContext(),R.string.permission_denied,Toast.LENGTH_LONG).show();
            }
    });

    private final ActivityResultLauncher<String[]> chooseFilePerm = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(),
        (Map<String, Boolean> result)->{
            boolean allPermissionGranted = true;
            Iterator<Boolean> iterator = result.values().iterator();

            while(iterator.hasNext() && allPermissionGranted){
                allPermissionGranted = iterator.next();
            }

            if (allPermissionGranted) {
                chooseFile();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
    });

    private final ActivityResultLauncher<String[]> chooseInternetPerm = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(),
        (Map<String, Boolean> result)->{
            boolean allPermissionGranted = true;
            Iterator<Boolean> iterator = result.values().iterator();

            while(iterator.hasNext() && allPermissionGranted){
                allPermissionGranted = iterator.next();
            }

            if (allPermissionGranted) {
                chooseInternet();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
    });

    private final ActivityResultLauncher<String[]> choosePDFPerm = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(),
        (Map<String, Boolean> result)->{
            boolean allPermissionGranted = true;
            Iterator<Boolean> iterator = result.values().iterator();

            while(iterator.hasNext() && allPermissionGranted){
                allPermissionGranted = iterator.next();
            }

            if (allPermissionGranted) {
                choosePDF();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
    });

    private final ActivityResultLauncher<Intent> FileChooserLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (result) -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadFile(result.getData().getData());
                }
            });

    private final ActivityResultLauncher<Intent> PDFChooserLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (result) -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadPDF(result.getData().getData());
                }
            });


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        FragmentImportBinding binding = FragmentImportBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.importCamera.setOnClickListener(this);  // set camera rectangle on click listener
        binding.importFile.setOnClickListener(this);    // set file rectangle on click listener
        binding.importPDF.setOnClickListener(this);     // set pdf rectangle on click listener
        binding.importInternetInfoListener.setOnClickListener(this);// set internet rectangle on click listener
        binding.importInternetListener.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int currentViewID = v.getId();
        if(currentViewID==R.id.importInternetInfoListener){
            DownloadInfo downloadInfo = new DownloadInfo(getActivity());
            downloadInfo.show();
        }else if(currentViewID==R.id.importPDF){
            if (checkWriteAndReadPerms(choosePDFPerm)) {
                choosePDF();
            }
        }else if(currentViewID==R.id.importCamera){
            if (isCameraPermissionGiven()) {
                loadCamera();
            } else {
                cameraPerm.launch(Manifest.permission.CAMERA);
            }
        }else if(currentViewID==R.id.importFile){
            if (checkWriteAndReadPerms(chooseFilePerm)) {
                chooseFile();
            }
        }else if(currentViewID==R.id.importInternetListener){
            if (checkWriteAndReadPerms(chooseInternetPerm)) {
                chooseInternet();
            }
        }
    }

    /*
    * return true if all perms are granted
    * if perms aren't granted it will launch perms request throw the launcher you pass in params
    */
    private boolean checkWriteAndReadPerms(ActivityResultLauncher permsLauncher){
        boolean resultWriteExternal = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean resultReadExternal = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(!resultReadExternal && !resultWriteExternal){
            permsLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
        }else if(!resultReadExternal){
            permsLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        }else if(!resultWriteExternal){
            permsLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }else{
            return true;
        }
        return false;
    }

    private void choosePDF(){
        String type="application/pdf";
        String title=getResources().getString(R.string.import_pdf_select);

        startChooserFile(PDFChooserLauncher,type,title);
    }

    private void chooseFile(){
        String type="image/* video/*";
        String title=getResources().getString(R.string.import_files_select);
        startChooserFile(FileChooserLauncher,type,title);
    }

    private void startChooserFile(ActivityResultLauncher launcher,String type, String title){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType(type);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType(type);

        Intent chooserIntent = Intent.createChooser(getIntent, title);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        launcher.launch(chooserIntent);
    }

    private void chooseInternet(){
        final Context context = getContext();
        final Activity activity = (Activity)context;
        new DownloadFile(context, activity, new DownloadFile.setOnListener() {
            @Override
            public void onFileDownloaded(String filePath) {

            File downloadedFile = new File(filePath); // we get the file downloaded
            Uri uriDownloadedFile = Uri.fromFile(downloadedFile); // we get his uri

            ContentResolver contentResolver = context.getContentResolver();
            String type = contentResolver.getType(uriDownloadedFile);

            if(type!=null){
                if (type.contains("image") || type.contains("video")) {
                    loadFile(uriDownloadedFile);
                } else if (type.contains("pdf")){
                    loadPDF(uriDownloadedFile);
                }
            }else if(checkIsImage(context,uriDownloadedFile)){
                loadFile(uriDownloadedFile);
            }else{
                invalidFileExtension();
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
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(backStateName)
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
        }
    }

    private void invalidFileExtension(){
        Toast.makeText(getContext(),getActivity().getResources().getString(R.string.import_error_file),Toast.LENGTH_LONG).show();
    }

    private boolean isCameraPermissionGiven(){
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkIsImage(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        // try to decode as image (bounds only)
        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            if (inputStream != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                return options.outWidth > 0 && options.outHeight > 0;
            }
        } catch (IOException e) {
            // ignore
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                FileUtils.closeQuietly(inputStream);
            }
        }

        // default outcome if image not confirmed
        return false;
    }
}