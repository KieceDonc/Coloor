package com.vvdev.colorpicker.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.vvdev.colorpicker.R;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Constraints;

public class DownloadFileAlertDialog extends Dialog implements android.view.View.OnClickListener {

    public interface setOnListener {
        void onFileDownloaded(String filePath);
    }

    private Context context;
    private Activity activity;

    private EditText url;
    private TextView cancel;
    private TextView download;
    private TextView pause;
    private TextView resume;
    private ProgressBar progressBar;

    private setOnListener listener;

    private int currentDownload;

    public DownloadFileAlertDialog(@NonNull Context context, @NonNull Activity activity, setOnListener listener) {
        super(context);
        this.context=context;
        this.activity=activity;
        this.listener=listener;

        PRDownloader.initialize(activity.getApplicationContext());
        PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_internet);

        Window window = getWindow(); // fix bug for match_parent width
        window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT); // fix bug for match_parent width plz refer to https://stackoverflow.com/questions/28513616/android-get-full-width-for-custom-dialog

        url = findViewById(R.id.internet_url);
        cancel = findViewById(R.id.internet_cancel);
        download = findViewById(R.id.internet_download);
        pause = findViewById(R.id.internet_pause);
        resume = findViewById(R.id.internet_resume);
        progressBar = findViewById(R.id.internet_progressBar);

        cancel.setOnClickListener(this);
        download.setOnClickListener(this);
        pause.setOnClickListener(this);
        resume.setOnClickListener(this);
        setupUrlListeners();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.internet_download:{
                wantToDownload();
                break;
            }
            case R.id.internet_cancel:{
                dismiss();
                break;
            }
            case R.id.internet_pause:{
                pauseDownload();
                break;
            }
            case R.id.internet_resume:{
                resumeDownload();
            }
        }
    }

    private void wantToDownload(){
        String urlText = url.getText().toString();

        String defaultText = activity.getResources().getString(R.string.import_internet_url_text);
        if(urlText.length()!=0&&!urlText.contains(" ")&&!urlText.equals(defaultText)){
            String pathFile = context.getCacheDir().getAbsolutePath();
            String fileName = createFileName()+urlText.substring(urlText.lastIndexOf("/")+1);
            currentDownload = newDownload(urlText,pathFile,fileName);
        }else{
            url.setTextColor(activity.getResources().getColor(R.color.import_internet_url_text_error)); // red color to show an error to the user
            url.setText("Unable to connect to this address"); // TODO to translate
        }
    }

    private int newDownload(String desireUrl, final String dirPath, final String fileName){
        return PRDownloader.download(desireUrl, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        download.setVisibility(View.INVISIBLE);
                        pause.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new com.downloader.OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        progressBar.setMax((int) progress.totalBytes);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressBar.setProgress((int)progress.currentBytes,true);
                        }else{
                            progressBar.setProgress((int)progress.currentBytes);
                        }
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        String pathFile = dirPath+"/"+fileName;
                        listener.onFileDownloaded(pathFile);
                        dismiss();
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e("DownloadFileAlertDialog","Is a connection error ? : "+error.isConnectionError()
                        +"\nconnection error message : "+error.getConnectionException().toString()
                        +"\nis a server error ? : "+error.isServerError()
                        +"\nserver error message : "+error.getServerErrorMessage()
                        +"\nerror toString() : "+error.toString());
                        url.setTextColor(activity.getResources().getColor(R.color.import_internet_url_text_error)); // red color to show an error to the user

                        if(error.getConnectionException().toString().contains("java.net.MalformedURLException")){
                            url.setText("Url address invalid"); // TODO to translate
                        }else{
                            url.setText("Error while downloading, please retry"); // TODO to translate
                        }

                        currentDownload=-1;
                        url.clearFocus();
                    }
                });
    }

    private void pauseDownload(){
        if(currentDownload!=-1){
            PRDownloader.pause(currentDownload);
            pause.setVisibility(View.INVISIBLE);
            resume.setVisibility(View.VISIBLE);
        }else{
            Log.e("DownloadFileAlertDialog","Trying to pause download but this download have been cancel due to an error");
        }
    }

    private void resumeDownload(){
        if(currentDownload!=-1){
            PRDownloader.resume(currentDownload);
            resume.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
        }else{
            Log.e("DownloadFileAlertDialog","Trying to resume download but this download have been cancel due to an error");
        }
    }

    private void setupUrlListeners(){
        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        url.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String defaultText = activity.getResources().getString(R.string.import_internet_url_text);
                if(hasFocus&&url.getCurrentTextColor()==activity.getResources().getColor(R.color.import_internet_url_text_error)){ // deleting error when user wants to type a new url
                    int darkBlueColor = activity.getResources().getColor(R.color.import_internet_url_text);
                    url.setTextColor(darkBlueColor);
                    url.setText("");
                }else if(hasFocus&&url.getText().toString().equals(defaultText)){// deleting default text when user wants to type a new url
                    url.setText("");
                }else if(!hasFocus){ // clear focus
                    url.clearFocus();
                }
            }
        });

        url.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                if (url.isEnabled() && url.isFocusable()) {
                    url.post(new Runnable() {
                        @Override
                        public void run() {
                            final InputMethodManager imm =
                                    (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }
        });
    }

    private String createFileName(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyhhmmss");
        return formatter.format(date);
    }

}
