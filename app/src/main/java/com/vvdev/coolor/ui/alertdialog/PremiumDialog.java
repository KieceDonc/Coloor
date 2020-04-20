package com.vvdev.coolor.ui.alertdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.vvdev.coolor.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PremiumDialog extends Dialog {

    private ConstraintLayout wantToPurchase;
    private TextView priceTV;

    public PremiumDialog(@NonNull Context context, final BillingClient billingClient, final List<SkuDetails> skuDetailsList, final Activity activity) {
        super(context);
        setContentView(R.layout.dialog_premium);

        wantToPurchase = findViewById(R.id.premium_purchase);
        priceTV = findViewById(R.id.price);

        priceTV.setText(skuDetailsList.get(0).getPrice());
        wantToPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList.get(0))
                        .build();
                billingClient.launchBillingFlow(activity,flowParams);
            }
        });
    }


}
