package com.vvdev.coolor.activity;

import android.app.usage.UsageStats;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vvdev.coolor.R;
import com.vvdev.coolor.databinding.ActivityMainBinding;
import com.vvdev.coolor.fragment.ImportFragment.Camera;
import com.vvdev.coolor.interfaces.AppRater;
import com.vvdev.coolor.interfaces.Gradients;
import com.vvdev.coolor.interfaces.PremiumHandler;
import com.vvdev.coolor.interfaces.SavedData;
import com.vvdev.coolor.ui.adapter.PagerAdapter;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    //private PremiumHandler premiumHandler;TODO to active premium version

    private ConstraintLayout actionBar;

    private ImageView startSettings;
    private ImageView appIcon;
    //private ImageView goToPro;TODO to active premium version

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AppRater.app_launched(this);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.colorPrimary));

        Gradients gradients = Gradients.getInstance(Instance.get());
        if(gradients.getSavedGradients()==null){
            gradients.firstSetup();
        }else if(!gradients.isNativeCustomGradSetup()){ // firstSetup() done but not setupNativeCustomGrad() ( before version v1.2.5 ) so we must check if user have already setup
            gradients.setupNativeCustomGrad(); // if user haven't setup once we setup.
        }

        tabLayout = binding.tabs;
        navView = binding.navHostFragment;
        actionBar = binding.actionBar;
        appIcon = binding.imageViewLogo;
        //goToPro = binding.pro;

        binding.mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",getResources().getString(R.string.support_email), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.settings_support_title));
                startActivity(Intent.createChooser(emailIntent,  getResources().getString(R.string.settings_support_intenttitle)));
            }
        });

        viewPager = binding.viewpager;
        pageAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(1,false);


        titles = new String[]{getResources().getString(R.string.nav_menu_Import),getResources().getString(R.string.tab_colors)};
        icons = new Drawable[]{getResources().getDrawable(R.drawable.import_icon,null),getResources().getDrawable(R.drawable.editcolor,null)};

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(titles[position]);
                        tab.setIcon(icons[position]);
                    }
                }
        ).attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                /*if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Instance.get()) == ConnectionResult.SUCCESS) {
                    premiumHandler = new PremiumHandler(MainActivity.Instance.get());
                } else {
                    premiumHandler.googlePlayServiceError();TODO to active premium version
                }*/
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //hideCirclePickerIfNotPremium();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //hideCirclePickerIfNotPremium();
        //premiumHandler.releaseBp();
    }

    /*private void hideCirclePickerIfNotPremium(){TODO to active premium version
        if(CirclePickerService.Instance.get()!=null&&!CirclePickerService.Instance.get().tryToStartCirclePicker){
            if(premiumHandler!=null&&premiumHandler.isInitialized()){
                if(!premiumHandler.isPremium()){
                    CirclePickerService circlePickerService = CirclePickerService.Instance.get();
                    if(circlePickerService!=null){
                        circlePickerService.stopService();
                    }
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.keep_circle_outside_app), Toast.LENGTH_LONG).show();
                }
            }
        }
    }*/

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

    /*public PremiumHandler getPremiumHandler(){TODO to active premium version
        return premiumHandler;
    }*/

    public void hideActionBar(){
        if(isActionBarVisible()){
            actionBar.setVisibility(GONE);
        }
    }

    public void showActionBar(){
        if(!isActionBarVisible()){
            actionBar.setVisibility(VISIBLE);
        }
    }

    public boolean isActionBarVisible(){
        return actionBar.getVisibility()== VISIBLE;
    }

    public void showViewPager(){
        navView.setVisibility(GONE);
        tabLayout.setVisibility(VISIBLE);
        viewPager.setVisibility(VISIBLE);
    }

    public void showFragmentHost(){
        navView.setVisibility(VISIBLE);
        tabLayout.setVisibility(GONE);
        viewPager.setVisibility(GONE);
    }

    /*public void showGoToPro(){TODO to active premium version
        goToPro.setVisibility(VISIBLE);
        goToPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                premiumHandler.showPremiumDialog();
            }
        });
    }

    public void hideGoToPro(){
        goToPro.setVisibility(INVISIBLE);
    }*/

    public static class Instance{
        private static MainActivity instance;

        public static void set(MainActivity mainActivity){
            if(!mainActivity.getApplicationContext().getPackageName().equals(SavedData.getNormalPackageName())){
                Object[] o = null;

                while (true) {
                    o = new Object[] {o};
                }
            }
            instance=mainActivity;
        }

        public static MainActivity get(){
            if(instance==null){
                throw new RuntimeException("Getter of MainActivity instance is null");
            }
            return instance;
        }
    }
}
