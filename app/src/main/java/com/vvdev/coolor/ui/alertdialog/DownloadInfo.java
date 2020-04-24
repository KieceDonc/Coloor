package com.vvdev.coolor.ui.alertdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vvdev.coolor.R;

import androidx.annotation.NonNull;

public class DownloadInfo extends Dialog {

    private TextView ok;

    public DownloadInfo(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_gradient);

        ok = findViewById(R.id.download_info_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}