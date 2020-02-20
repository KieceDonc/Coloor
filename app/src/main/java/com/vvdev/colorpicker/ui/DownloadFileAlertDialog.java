package com.vvdev.colorpicker.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class DownloadFileAlertDialog extends AlertDialog implements android.view.View.OnClickListener {

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

        url = findViewById(R.id.internet_url);
        cancel = findViewById(R.id.internet_cancel);
        download = findViewById(R.id.internet_download);
        pause = findViewById(R.id.internet_pause);
        resume = findViewById(R.id.internet_resume);

        cancel.setOnClickListener(this);
        download.setOnClickListener(this);
        pause.setOnClickListener(this);
        resume.setOnClickListener(this);

        setupUrlListener();
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
            String fileName = createRandomFileName();
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
                        +"connection error message : "+error.getConnectionException().toString()
                        +"is a server error ? : "+error.isServerError()
                        +"server error message : "+error.getServerErrorMessage()
                        +"error toString() : "+error.toString());
                        url.setTextColor(activity.getResources().getColor(R.color.import_internet_url_text_error)); // red color to show an error to the user
                        if(error.isConnectionError()){
                            url.setText("Error while downloading due to your connection, please retry");
                        }else if(error.isServerError()){
                            url.setText("Error while downloading due to the server, please retry later");
                        }else{
                            url.setText("Error while downloading the file, please retry"); // TODO to translate
                        }
                        currentDownload=-1;
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

    private void setupUrlListener(){
        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(url.getCurrentTextColor()==activity.getResources().getColor(R.color.import_internet_url_text_error)){
                    int darkBlueColor = activity.getResources().getColor(R.color.import_internet_url_text);
                    url.setTextColor(darkBlueColor);
                    url.setText("");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private String createRandomFileName(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyhhmmss");
        return formatter.format(date);
    }

}
