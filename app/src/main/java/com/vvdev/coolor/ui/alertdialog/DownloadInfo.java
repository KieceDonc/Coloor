package com.vvdev.coolor.ui.alertdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.vvdev.coolor.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;

public class DownloadInfo extends Dialog {

    private TextView ok;

    public DownloadInfo(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download_info);

        Window window = getWindow(); // fix bug for match_parent width
        if (window != null) {
            window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT); // fix bug for match_parent width plz refer to https://stackoverflow.com/questions/28513616/android-get-full-width-for-custom-dialog
        }

        ok = findViewById(R.id.download_info_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
