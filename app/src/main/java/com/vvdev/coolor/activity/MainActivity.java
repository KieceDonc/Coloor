package com.vvdev.coolor.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vvdev.coolor.R;
import com.vvdev.coolor.fragment.BottomBar.Import;
import com.vvdev.coolor.fragment.BottomBar.Palette;
import com.vvdev.coolor.fragment.ImportSelected.Camera;
import com.vvdev.coolor.fragment.Settings.SettingsMain;
import com.vvdev.coolor.interfaces.Gradients;
import com.vvdev.coolor.services.CirclePickerService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.vvdev.coolor.activity.CirclePickerActivityStart.isCirclePickerActivityRunning;
import static com.vvdev.coolor.activity.CirclePickerActivityStart.wmCirclePickerView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener { // TODO signature apk

    private FirebaseAnalytics mFirebaseAnalytics;

    private BottomNavigationView navView;
    private ConstraintLayout actionBar;

    private ImageView startSettings;
    private ImageView appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance.setMainActivityInstance(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(Gradients.getInstance(this).getSavedGradients()==null){
            Gradients.getInstance(this).firstSetup();
        }

        navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_palette);
        navView.setOnNavigationItemSelectedListener(this);

        actionBar = findViewById(R.id.actionBar);
        startSettings = findViewById(R.id.settings);

        startSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SettingsMain());
            }
        });

        appIcon = findViewById(R.id.imageViewLogo);
        Glide.with(this).load(R.drawable.ic_launcher).into(appIcon);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Instance.setMainActivityInstance(null);
    }

    private void startCirclePickerService(){
        Intent CirclePickerServiceIntent = new Intent(this, CirclePickerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(CirclePickerServiceIntent);
        }else{
            startService(CirclePickerServiceIntent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.navigation_import:{
                Import importInstance = Instance.getImportInstance();
                if(Instance.getImportInstance()==null){
                    fragment = new Import();
                }else{
                    fragment = importInstance;
                }
                break;
            }
            case R.id.navigation_palette:{
                Palette paletteInstance = Instance.getPaletteInstance();
                if(paletteInstance==null){
                    fragment = new Palette();
                }else{
                    fragment = paletteInstance;
                }
                break;
            }
            case R.id.navigation_circle_picker:{
                if(wmCirclePickerView==null&&!isCirclePickerActivityRunning){
                    startCirclePickerService();
                    Log.e("MainActivity","Bug detected, isCPRunning = true and isCirclePickerActivityRunning = false but no circle view attached.\nisCPRunning have been set to false and startCirPickerService have been started");
                }
            }
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            String backStateName = fragment.getClass().getName();

            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0); //https://stackoverflow.com/questions/18305945/how-to-resume-fragment-from-backstack-if-exists

            if (!fragmentPopped){ //fragment not in back stack, create it.
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(backStateName)
                        .replace(R.id.nav_host_fragment, fragment)
                        .commit();
                return true;
            }
            return false;
        }
        return false;
    }

    public Camera.setOnCameraLifeCycleListener getCameraListener(){
        return new Camera.setOnCameraLifeCycleListener() {
            @Override
            public void onFragmentResume() {
                hideActionBar();
                hideCirclePickerStartButton();
            }

            @Override
            public void onFragmentPause() {
                showActionBar();
                showCirclePickerStartButton();
            }
        };
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

    public void showCirclePickerStartButton(){
        if(!isCirclePickerStartButtonVisible()){
            navView.getMenu().getItem(1).getActionView().setVisibility(View.INVISIBLE);
        }
    }

    public void hideCirclePickerStartButton(){
        if(isCirclePickerStartButtonVisible()){
            navView.getMenu().getItem(1).getActionView().setVisibility(View.VISIBLE);
        }
    }

    public boolean isCirclePickerStartButtonVisible(){
        return navView.getVisibility()==View.VISIBLE&&navView.getMenu().getItem(2).getActionView().getVisibility()==View.VISIBLE;
    }

    public static class Instance{
        private static MainActivity mainActivityInstance;
        private static Import importInstance;
        private static Palette paletteInstance;

        public static MainActivity getMainActivityInstance() {
            return mainActivityInstance;
        }

        public static void setMainActivityInstance(MainActivity mainActivityInstance) {
            Instance.mainActivityInstance = mainActivityInstance;
        }

        public static Import getImportInstance() {
            return importInstance;
        }

        public static void setImportInstance(Import importInstance) {
            Instance.importInstance = importInstance;
        }

        public static Palette getPaletteInstance() {
            return paletteInstance;
        }

        public static void setPaletteInstance(Palette paletteInstance) {
            Instance.paletteInstance = paletteInstance;
        }
    }
}
