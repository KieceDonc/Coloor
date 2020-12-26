package com.vvdev.coolor.interfaces;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/*import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;*/
import com.vvdev.coolor.activity.MainActivity;
import com.vvdev.coolor.ui.alertdialog.PremiumDialog;

import java.util.ArrayList;

public class PremiumHandler /*implements BillingProcessor.IBillingHandler*/ {

    private static final ArrayList<setOnPurchaseListener> listenerList=new ArrayList<>();

    private final PremiumHandler _this = this;
    private PremiumDialog premiumDialog;

    //private BillingProcessor bp;

    private final Activity activity;
    private final String productName="inapppurchasev1";
    private final String price="";
    private final boolean isPremium=false;
    private final boolean isBillingSupported=false;

    public interface setOnPurchaseListener{
        void onPurchaseCompleted();
        void onPurchaseCanceled();
        void onPurchaseError();
        void onPurchaseRestored();
    }


    public PremiumHandler(Activity activity) {
        this.activity = activity;
        /*bp = new BillingProcessor(activity, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjmmDHWbIXA3ZTgfFRQVUfx8XhrfYR2hyXMTsOO0prPlNvclwWPCAWsoCrvAd7PTaLntzNTQXjzNkYumRoj1BxzepiikTxYCpRLzy9LsqkuM6mDPvVVJsLifw0LIcK0qwe2/2A/IzDs59kUkzNyxzEAhpecDc3cTKYwWkYWFgQr0b/HI0IcQbAcXMD+mjDJoKn//yhSWIGxg5EeoZ4hJ+VboYrKeO6PnWeGGwOAJbgVfXuW7+9mPZwfaINVFtEPhMel1C8g400Q0/2MRRe3a3khEfaMht/uYYS7vUUwKL4FeRQ2V3/WBXNTCNi8O3eyzADtXI6DUL0H3LfTilLFZ1uQIDAQAB", this);
        bp.initialize();*/
    }


    /*@Override
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
        if(bp.loadOwnedPurchasesFromGoogle()){
            if(!isPremium()){
                MainActivity.Instance.get().showGoToPro();
                for(int x=0;x<listenerList.size();x++){
                    setOnPurchaseListener currentListener = listenerList.get(x);
                    if(currentListener!=null){
                        currentListener.onPurchaseRestored();
                    }
                }
            }
        }
    }

    public boolean isPremium(){

        if(!isPremium){
            isPremium =  bp.isPurchased(productName);
        }
        return bp.isPurchased(productName);
        return true;
    }

    public void googlePlayServiceError(){
        isPremium=true;
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

    public static void addListener(setOnPurchaseListener listener){
        listenerList.add(listener);
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
        for(int x=0;x<listenerList.size();x++){
            setOnPurchaseListener currentListener = listenerList.get(x);
            if(currentListener!=null){
                currentListener.onPurchaseCompleted();
            }
        }
        MainActivity.Instance.get().hideGoToPro();
        bp.release();
    }

    private void purchaseCanceled(){
        Toast.makeText(activity,"Purchase canceled",Toast.LENGTH_LONG).show();
        if(premiumDialog!=null){
            premiumDialog.dismiss();
        }
        for(int x=0;x<listenerList.size();x++){
            setOnPurchaseListener currentListener = listenerList.get(x);
            if(currentListener!=null){
                currentListener.onPurchaseCanceled();
            }
        }

    }

    private void purchaseError(String error){
        Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
        for(int x=0;x<listenerList.size();x++){
            setOnPurchaseListener currentListener = listenerList.get(x);
            if(currentListener!=null){
                currentListener.onPurchaseError();
            }
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

    public boolean isInitialized(){
        return bp.isInitialized();
    }*/
}
