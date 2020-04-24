package com.vvdev.coolor.ui.alertdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vvdev.coolor.R;
import com.vvdev.coolor.activity.MainActivity;
import com.vvdev.coolor.interfaces.PremiumHandler;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PremiumDialog extends Dialog {

    private ConstraintLayout wantToPurchase;
    private TextView priceTV;

    public PremiumDialog(@NonNull Context context, final PremiumHandler premiumHandler) {
        super(context);
        setContentView(R.layout.dialog_premium);

        wantToPurchase = findViewById(R.id.premium_purchase);
        priceTV = findViewById(R.id.price);

        priceTV.setText(premiumHandler.getPrice());
        wantToPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                premiumHandler.makePurchase();
            }
        });
    }


}