package com.vvdev.colorpicker.activity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorSpec;
import com.vvdev.colorpicker.interfaces.ColorUtility;


import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_camera, R.id.navigation_import, R.id.navigation_palette)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);




        ColorSpec black = new ColorSpec("#000000");
        black.getRGB();


    }

}
