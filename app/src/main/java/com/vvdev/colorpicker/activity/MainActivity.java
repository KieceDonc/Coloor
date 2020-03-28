package com.vvdev.colorpicker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.fragment.BottomBar.Import;
import com.vvdev.colorpicker.fragment.BottomBar.Palette;
import com.vvdev.colorpicker.fragment.ImportSelected.Camera;
import com.vvdev.colorpicker.fragment.Settings;
import com.vvdev.colorpicker.interfaces.Gradients;
import com.vvdev.colorpicker.services.CirclePickerService;

import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import static com.vvdev.colorpicker.activity.CirclePickerActivityStart.isCirclePickerActivityRunning;
import static com.vvdev.colorpicker.activity.CirclePickerActivityStart.wmCirclePickerView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener { // TODO signature apk

    private FirebaseAnalytics mFirebaseAnalytics;

    private BottomNavigationView navView;
    private ConstraintLayout actionBar;
    private ConstraintLayout circlePickerStartConstraintLayout;

    private ImageView startSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance.setMainActivityInstance(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(Gradients.getSavedGradients(this)==null){
            Gradients.firstSetup(this);
        }

        navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_palette);
        navView.setOnNavigationItemSelectedListener(this);

        circlePickerStartConstraintLayout = findViewById(R.id.circlePickerStartConstraintLayout);
        actionBar = findViewById(R.id.actionBar);
        startSettings = findViewById(R.id.settings);

        startSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = Instance.getSettingsInstance();
                if(fragment==null){
                    fragment = new Settings();
                }
                loadFragment(fragment);
            }
        });
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
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
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
            navView.getMenu().getItem(1).setVisible(true);
            circlePickerStartConstraintLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideCirclePickerStartButton(){
        if(isCirclePickerStartButtonVisible()){
            navView.getMenu().getItem(1).setVisible(false);
            circlePickerStartConstraintLayout.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isCirclePickerStartButtonVisible(){
        return navView.getVisibility()==View.VISIBLE&&circlePickerStartConstraintLayout.getVisibility()==View.VISIBLE;
    }

    public static class Instance{
        private static MainActivity mainActivityInstance;
        private static Import importInstance;
        private static Palette paletteInstance;
        private static Settings settingsInstance;

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

        public static Settings getSettingsInstance() {
            return settingsInstance;
        }

        public static void setSettingsInstance(Settings settingsInstance) {
            Instance.settingsInstance = settingsInstance;
        }
    }
}
