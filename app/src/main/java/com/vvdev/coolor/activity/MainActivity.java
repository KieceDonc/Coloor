package com.vvdev.coolor.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vvdev.coolor.R;
import com.vvdev.coolor.databinding.ActivityMainBinding;
import com.vvdev.coolor.fragment.ImportFragment.Camera;
import com.vvdev.coolor.fragment.TabHost.ColorsTab;
import com.vvdev.coolor.interfaces.Gradients;
import com.vvdev.coolor.interfaces.PremiumHandler;
import com.vvdev.coolor.interfaces.SavedData;
import com.vvdev.coolor.services.CirclePickerService;
import com.vvdev.coolor.ui.adapter.PagerAdapter;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private PremiumHandler premiumHandler;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


       /* int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }*/ // / hide status and navigation bar

        tabLayout = binding.tabs;
        navView = binding.navHostFragment;
        actionBar = binding.actionBar;
        appIcon = binding.imageViewLogo;
        //goToPro = binding.pro;

        binding.mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, getResources().getString(R.string.support_email));
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.settings_support_title));
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.settings_support_intenttitle)));
            }
        });

        viewPager = binding.viewpager;
        pageAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(1,false);


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
    }
  /*  @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }*/ // hide status and navigation bar

    @Override
    protected void onResume() {
        super.onResume();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if(Gradients.getInstance(Instance.get()).getSavedGradients()==null){
                    Gradients.getInstance(Instance.get()).firstSetup();
                    if(ColorsTab.Instance.get()!=null&&ColorsTab.Instance.get().getColorsTabRVAdapter()!=null){
                        ColorsTab.Instance.get().getColorsTabRVAdapter().updateSpinner();
                    }
                }
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
