package com.vvdev.colorpicker.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.fragment.BottomBar.Import;
import com.vvdev.colorpicker.fragment.BottomBar.Palette;
import com.vvdev.colorpicker.interfaces.ColorUtility;
import com.vvdev.colorpicker.services.CirclePickerService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import static com.vvdev.colorpicker.activity.CirclePickerActivityStart.isCirclePickerActivityRunning;
import static com.vvdev.colorpicker.activity.CirclePickerActivityStart.wmCirclePickerView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener { // TODO signature apk

    public static int appNavigationBarHeight = 0;
    public static boolean isCPRunning = false; // is circle picker running

    private BottomNavigationView navView;
    private ConstraintLayout startCirclePicker;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_palette);
        navView.setOnNavigationItemSelectedListener(this);

        startCirclePicker = findViewById(R.id.pipette);

        startCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCPRunning){ // check if circle picker is not running. If not, we start it
                    startCirclePickerService();
                }else if(isCPRunning&&wmCirclePickerView==null&&!isCirclePickerActivityRunning){
                    isCPRunning=false;
                    startCirclePickerService();
                    Log.e("MainActivity","Bug detected, isCPRunning = true and isCirclePickerActivityRunning = false but no circle view attached.\nisCPRunning have been set to false and startCirPickerService have been started");
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navView = findViewById(R.id.nav_view); // used in CirclePickerView
        appNavigationBarHeight=navView.getHeight(); // used in CirclePickerView
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                fragment = new Import();
                break;
            }
            case R.id.navigation_palette:{
                fragment = new Palette();
            }
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
