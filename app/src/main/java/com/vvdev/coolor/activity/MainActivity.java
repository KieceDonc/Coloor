package com.vvdev.coolor.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.revenuecat.purchases.Purchases;
import com.vvdev.coolor.BuildConfig;
import com.vvdev.coolor.R;
import com.vvdev.coolor.databinding.ActivityMainBinding;
import com.vvdev.coolor.fragment.ImportFragment.Camera;
import com.vvdev.coolor.interfaces.Gradients;
import com.vvdev.coolor.interfaces.PremiumHandler;
import com.vvdev.coolor.interfaces.SavedData;
import com.vvdev.coolor.services.CirclePickerService;
import com.vvdev.coolor.ui.adapter.PagerAdapter;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import io.fabric.sdk.android.services.common.CommonUtils;

import static com.vvdev.coolor.activity.CirclePickerActivityStart.isCirclePickerActivityRunning;
import static com.vvdev.coolor.activity.CirclePickerActivityStart.wmCirclePickerView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private PremiumHandler premiumHandler;

    private ConstraintLayout actionBar;

    private ImageView startSettings;
    private ImageView appIcon;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FragmentContainerView navView;
    private FragmentStateAdapter pageAdapter;

    private String[] titles;
    private Drawable[] icons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Instance.set(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Purchases.setDebugLogsEnabled(true);
        Purchases.configure(this, "BLOCZLbOXzjIfVvpvHlUiUscMbcslGPn");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(Gradients.getInstance(this).getSavedGradients()==null){
            Gradients.getInstance(this).firstSetup();
        }


        tabLayout = binding.tabs;
        navView = binding.navHostFragment;
        actionBar = binding.actionBar;
        appIcon = binding.imageViewLogo;

        binding.mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, getResources().getString(R.string.support_email));
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.settings_support_title));
                String emailContent = getResources().getString(R.string.settings_support_emailcontent);
                emailContent+="\n"+userAnonymousInfo();
                intent.putExtra(Intent.EXTRA_TEXT,emailContent);

                startActivity(Intent.createChooser(intent, getResources().getString(R.string.settings_support_intenttitle)));
            }
        });

        viewPager = binding.viewpager;
        pageAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(0,true);


        titles = new String[]{getResources().getString(R.string.nav_menu_Import),getResources().getString(R.string.tab_colors),getResources().getString(R.string.tab_gradients)};
        icons = new Drawable[]{getResources().getDrawable(R.drawable.import_icon,null),getResources().getDrawable(R.drawable.editcolor,null),getResources().getDrawable(R.drawable.gradient_icon,null)};

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(titles[position]);
                        tab.setIcon(icons[position]);
                    }
                }
        ).attach();

        viewPager.setCurrentItem(1); // color tab position

        try{
            Glide.with(this).load(R.drawable.ic_launcher).into(appIcon);
        }catch (RuntimeException ex){
            Bundle error = new Bundle();
            error.putString("MainActivity","AppIconDraw");
            mFirebaseAnalytics.logEvent("Error",error);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        premiumHandler = new PremiumHandler(this,null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!premiumHandler.isPremium()){
            CirclePickerService circlePickerService = CirclePickerService.Instance.get();
            if(circlePickerService!=null){
                circlePickerService.stopService();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public Camera.setOnCameraLifeCycleListener getCameraListener(){
        return new Camera.setOnCameraLifeCycleListener() {
            @Override
            public void onFragmentResume() {
                hideActionBar();
            }

            @Override
            public void onFragmentPause() {
                showActionBar();
            }
        };
    }

    public PremiumHandler getPremiumHandler(){
        return premiumHandler;
    }

    public void hideActionBar(){
        if(isActionBarVisible()){
            actionBar.setVisibility(View.GONE);
        }
    }

    public void showActionBar(){
        if(!isActionBarVisible()){
            actionBar.setVisibility(View.VISIBLE);
        }
    }

    public boolean isActionBarVisible(){
        return actionBar.getVisibility()==View.VISIBLE;
    }

    private void showViewPager(){
        navView.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
    }

    private void showFragmentHost(){
        navView.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.INVISIBLE);
    }

    public static void startCirclePickerService(Context context){
        if(wmCirclePickerView==null&&!isCirclePickerActivityRunning){
            Intent CirclePickerServiceIntent = new Intent(context, CirclePickerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(CirclePickerServiceIntent);
            }else{
                context.startService(CirclePickerServiceIntent);
            }
        }
    }


    private String userAnonymousInfo(){
        String toReturn = "Android version : "+android.os.Build.VERSION.SDK_INT;
        toReturn+="\nManufacturer : "+ Build.MANUFACTURER;
        toReturn+="\nBuild : "+ Build.PRODUCT;
        toReturn+="\nDevice name : "+getDeviceName();
        toReturn+="\nRoot : "+ CommonUtils.isRooted(this);
        toReturn+="\nApp version : "+ BuildConfig.VERSION_NAME;
        toReturn+="\nCall date : "+new Date().toString();
        return toReturn;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }


    public static class Instance{
        private static MainActivity mainActivity_;

        public static void set(MainActivity mainActivity){
            if(!mainActivity.getApplicationContext().getPackageName().equals(SavedData.getNormalPackageName())){
                Object[] o = null;

                while (true) {
                    o = new Object[] {o};
                }
            }
            mainActivity_=mainActivity;
        }

        public static MainActivity get(){
            return mainActivity_;
        }
    }
}
