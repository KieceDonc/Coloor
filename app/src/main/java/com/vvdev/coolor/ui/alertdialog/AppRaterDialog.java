package com.vvdev.coolor.ui.alertdialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.SavedData;

import androidx.constraintlayout.widget.Constraints;

public class AppRaterDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = AppRaterDialog.class.getName();
    private final static String APP_TITLE = "Coloor";// App Name
    private final static String APP_PNAME = SavedData.getNormalPackageName();// Package Name

    private Context mContext = null;
    private SharedPreferences.Editor editor = null;

    public AppRaterDialog(final Context mContext, final SharedPreferences.Editor editor) {
        super(mContext);
        this.mContext = mContext;
        this.editor = editor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rate);

        Window window = getWindow(); // fix bug for match_parent width
        if (window != null) {
            window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT); // fix bug for match_parent width plz refer to https://stackoverflow.com/questions/28513616/android-get-full-width-for-custom-dialog
        }

        findViewById(R.id.RateButton).setOnClickListener(this);
        findViewById(R.id.RateButton).setOnClickListener(this);
        findViewById(R.id.RemindMe).setOnClickListener(this);
        findViewById(R.id.No).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.RateTitle:{
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                this.dismiss();
                break;
            }
            case R.id.RateButton:{
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                this.dismiss();
                break;
            }
            case R.id.RemindMe:{
                this.dismiss();
                break;
            }
            case R.id.No:{
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                this.dismiss();
                break;
            }
        }
    }

}
