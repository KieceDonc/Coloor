package com.vvdev.colorpicker.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.services.CirclePickerService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static boolean isCPRunning = false; // is circle picker running

    private ImageView startCirclePickerI;
    private CircleImageView startCirclePickerB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        startCirclePickerB = findViewById(R.id.backgroundPipette);
        startCirclePickerI = findViewById(R.id.pipette);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_import, R.id.navigation_palette).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        startCirclePickerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCPRunning){ // check if circle picker is not running. If not, we start it
                    startCirclePickerService();
                }
            }
        });

        startCirclePickerI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCPRunning){ // check if circle picker is not running. If not, we start it
                    startCirclePickerService();
                }

            }
        });
    }

    private void startCirclePickerService(){
        Activity activity=this;
        Intent CirclePickerServiceIntent = new Intent(activity, CirclePickerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(CirclePickerServiceIntent);
        }else{
            startService(CirclePickerServiceIntent);
        }
    }
}
