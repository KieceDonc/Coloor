package com.vvdev.coolor.interfaces;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.vvdev.coolor.ui.alertdialog.PremiumDialog;

public class PremiumHandler implements BillingProcessor.IBillingHandler {

    private setOnPurchaseListener listener;

    private PremiumHandler _this = this;
    private PremiumDialog premiumDialog;

    private BillingProcessor bp;

    private final Activity activity;
    private String productName="inapppurchasev1";
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
        bp = new BillingProcessor(activity, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjmmDHWbIXA3ZTgfFRQVUfx8XhrfYR2hyXMTsOO0prPlNvclwWPCAWsoCrvAd7PTaLntzNTQXjzNkYumRoj1BxzepiikTxYCpRLzy9LsqkuM6mDPvVVJsLifw0LIcK0qwe2/2A/IzDs59kUkzNyxzEAhpecDc3cTKYwWkYWFgQr0b/HI0IcQbAcXMD+mjDJoKn//yhSWIGxg5EeoZ4hJ+VboYrKeO6PnWeGGwOAJbgVfXuW7+9mPZwfaINVFtEPhMel1C8g400Q0/2MRRe3a3khEfaMht/uYYS7vUUwKL4FeRQ2V3/WBXNTCNi8O3eyzADtXI6DUL0H3LfTilLFZ1uQIDAQAB", this);
        bp.initialize();
        bp.loadOwnedPurchasesFromGoogle();
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        if(productId.equals(productName)){
            purchaseCompleted();
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        switch (errorCode){
            case Constants.BILLING_RESPONSE_RESULT_USER_CANCELED:{
                purchaseCanceled();
                break;
            }
            case Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:{
                purchaseCompleted();
                break;
            }
            default:{
                purchaseError("Error code : "+errorCode);
            }
        }
    }

    @Override
    public void onBillingInitialized() {
    }

    public boolean isPremium(){
        if(!isPremium){
            isPremium =  bp.isPurchased(productName);
        }
        return isPremium;
    }

    public String getPrice(){
        SkuDetails skuDetails = bp.getPurchaseListingDetails(productName);
        if(skuDetails!=null) {
            price = skuDetails.priceText;
            if (price != null || !isBillingSupported) {
                return price;
            }else {
                return "error";
            }
        }else{
            return "error";
        }
    }

    public void setListener(setOnPurchaseListener listener){
        this.listener = listener;
    }

    public void makePurchase(){
        bp.purchase(activity, productName);
    }

    public void showPremiumDialog(){
        if(BillingProcessor.isIabServiceAvailable(activity)){
            premiumDialog = new PremiumDialog(activity,_this);
            premiumDialog.show();
        }else{
            ToastGoogleBillingNotSupport();
        }
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
        bp.release();
    }

    private void purchaseCanceled(){
        Toast.makeText(activity,"Purchase canceled",Toast.LENGTH_LONG).show();
        if(premiumDialog!=null){
            premiumDialog.dismiss();
        }
        if(listener!=null){
            listener.onPurchaseCanceled();
        }
    }

    private void purchaseError(String error){
        Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
        if(listener!=null){
            listener.onPurchaseError();
        }
    }

    private void ToastGoogleBillingNotSupport(){
        Toast.makeText(activity, "Google play billing service isn't supported", Toast.LENGTH_LONG).show();
    }

    public void releaseBp(){
        if (bp != null) {
            bp.release();
        }

    }
}
