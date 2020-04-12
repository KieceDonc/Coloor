package com.vvdev.coolor.ui.alertdialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.vvdev.coolor.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PremiumDialog extends Dialog {

    private ConstraintLayout wantToPurchase;
    private TextView price;

    public PremiumDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.alertdialog_premium);

        wantToPurchase = findViewById(R.id.premium_purchase);
        price = findViewById(R.id.price);
    }
}
