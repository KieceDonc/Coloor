package com.vvdev.coolor.interfaces;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.Purchase;
import com.revenuecat.purchases.EntitlementInfo;
import com.revenuecat.purchases.EntitlementInfos;
import com.revenuecat.purchases.Offerings;
import com.revenuecat.purchases.Package;
import com.revenuecat.purchases.PurchaserInfo;
import com.revenuecat.purchases.Purchases;
import com.revenuecat.purchases.PurchasesError;
import com.revenuecat.purchases.interfaces.Callback;
import com.revenuecat.purchases.interfaces.MakePurchaseListener;
import com.revenuecat.purchases.interfaces.ReceiveOfferingsListener;
import com.revenuecat.purchases.interfaces.ReceivePurchaserInfoListener;
import com.revenuecat.purchases.interfaces.UpdatedPurchaserInfoListener;
import com.vvdev.coolor.ui.alertdialog.PremiumDialog;

import java.util.Map;

import androidx.annotation.NonNull;

public class PremiumHandler{

    private setOnPurchaseListener listener;

    private PremiumHandler _this = this;
    private PremiumDialog premiumDialog;

    private Package lifeTimePackage;

    private final Activity activity;
    private String price="";
    private boolean isPremium=false;
    private boolean isBillingSupported=false;

    public interface setOnPurchaseListener{
        void onPurchaseCompleted();
        void onPurchaseCanceled();
        void onPurchaseError();
    }

    public PremiumHandler(Activity activity,setOnPurchaseListener listener){
        this.activity = activity;
        this.listener = listener;
        setup();
    }

    private void setup(){
        Purchases.isBillingSupported(activity, new Callback<Boolean>() {
            @Override
            public void onReceived(final Boolean isSupported) {
                isBillingSupported = isSupported;
                if(!isBillingSupported){
                    ToastGoogleBillingNotSupport();
                }else{
                    Purchases.getSharedInstance().getPurchaserInfo(new ReceivePurchaserInfoListener() {
                        @Override
                        public void onReceived(@NonNull PurchaserInfo purchaserInfo) {
                            Log.e("test",purchaserInfo.getActiveSubscriptions().toString());
                            EntitlementInfo entitlementInfo = purchaserInfo.getEntitlements().get("pro");
                            if(entitlementInfo!=null){
                                Log.e("test",purchaserInfo.getEntitlements().getAll().toString());
                                isPremium = entitlementInfo.isActive();
                            }
                            if(!isPremium){
                                Purchases.getSharedInstance().getOfferings(new ReceiveOfferingsListener() {
                                    @Override
                                    public void onReceived(@NonNull Offerings offerings) {
                                        if(offerings.getCurrent()!=null){
                                            lifeTimePackage= offerings.getCurrent().getLifetime();
                                            price = lifeTimePackage.getProduct().getPrice();
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull PurchasesError error) {
                                        Toast.makeText(_this.activity,error.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(@NonNull PurchasesError error) {
                            Toast.makeText(_this.activity,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public boolean isPremium(){
        return isPremium;
    }

    public String getPrice(){
        if(price!=null||!isBillingSupported){
            return price;
        }else{
            return "error";
        }
    }

    public void setListener(setOnPurchaseListener listener){
        this.listener = listener;
    }

    public void makePurchase(){
        if(isBillingSupported){
            if(lifeTimePackage!=null){
                Purchases.getSharedInstance().purchasePackage(
                        activity,
                        lifeTimePackage, new MakePurchaseListener() {
                            @Override
                            public void onCompleted(@NonNull Purchase purchase, @NonNull PurchaserInfo purchaserInfo) {
                                EntitlementInfo entitlementInfo = purchaserInfo.getEntitlements().get("pro");
                                if(entitlementInfo!=null){
                                    if (entitlementInfo.isActive()) {
                                        Log.e("test","isActive");
                                        purchaseCompleted();
                                    }
                                }
                            }

                            @Override
                            public void onError(@NonNull PurchasesError error, boolean userCancelled) {
                                if(userCancelled){
                                    purchaseCanceled();
                                }else{
                                    purchaseError(error);
                                }
                            }
                        }
                );
            }
        }else{
            ToastGoogleBillingNotSupport();
        }
    }

    public void showPremiumDialog(){
        premiumDialog = new PremiumDialog(activity,_this);
        premiumDialog.show();
    }

    private void purchaseCompleted(){
        Toast.makeText(activity,"Premium version unlock !",Toast.LENGTH_LONG).show();
        isPremium = true;
        if(premiumDialog!=null){
            premiumDialog.dismiss();
        }
        if(listener!=null){
            listener.onPurchaseCompleted();
        }
    }

    private void purchaseCanceled(){
        Toast.makeText(activity,"Purchase canceled",Toast.LENGTH_LONG).show();
        if(listener!=null){
            listener.onPurchaseCanceled();
        }
    }

    private void purchaseError(PurchasesError error){
        Toast.makeText(activity,error.getMessage(),Toast.LENGTH_LONG).show();
        if(listener!=null){
            listener.onPurchaseError();
        }
    }

    private void ToastGoogleBillingNotSupport(){
        Toast.makeText(activity, "Google play billing service isn't supported", Toast.LENGTH_LONG).show();
    }
}
