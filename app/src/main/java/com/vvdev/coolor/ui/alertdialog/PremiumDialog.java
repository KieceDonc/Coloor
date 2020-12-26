package com.vvdev.coolor.ui.alertdialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.PremiumHandler;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PremiumDialog extends Dialog {

    private final ConstraintLayout wantToPurchase;
    private final TextView priceTV;

    public PremiumDialog(@NonNull Context context, final PremiumHandler premiumHandler) {
        super(context);
        setContentView(R.layout.dialog_premium);

        wantToPurchase = findViewById(R.id.premium_purchase);
        priceTV = findViewById(R.id.price);

        /*priceTV.setText(premiumHandler.getPrice());
        wantToPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                premiumHandler.makePurchase();
            }
        });*/
    }


}
