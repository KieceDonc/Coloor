package com.vvdev.coolor.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vvdev.coolor.R;
import com.vvdev.coolor.fragment.ImportFragment.Camera;
import com.vvdev.coolor.interfaces.Gradients;
import com.vvdev.coolor.interfaces.PremiumHandler;
import com.vvdev.coolor.services.CirclePickerService;
import com.vvdev.coolor.ui.adapter.PagerAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

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
        setContentView(R.layout.activity_main);
        Instance.set(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        premiumHandler = new PremiumHandler(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(Gradients.getInstance(this).getSavedGradients()==null){
            Gradients.getInstance(this).firstSetup();
        }

        tabLayout = findViewById(R.id.tabs);
        navView = findViewById(R.id.nav_host_fragment);
        actionBar = findViewById(R.id.actionBar);
        appIcon = findViewById(R.id.imageViewLogo);

        viewPager = findViewById(R.id.viewpager);
        pageAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(0,true);

        titles = new String[]{getResources().getString(R.string.nav_menu_Import),getResources().getString(R.string.tab_colors),getResources().getString(R.string.tab_gradients)};
        icons = new Drawable[]{getResources().getDrawable(R.drawable.importimgvideo,null),getResources().getDrawable(R.drawable.editcolor,null),getResources().getDrawable(R.drawable.gradient_icon,null)};

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(titles[position]);
                        tab.setIcon(icons[position]);
                    }
                }
        ).attach();

        Glide.with(this).load(R.drawable.ic_launcher).into(appIcon);

        if(!getApplicationContext().getPackageName().equals("com.vvdev.colorpicker")){
            Object[] o = null;

            while (true) {
                o = new Object[] {o};
            }
        }
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
        Instance.set(null);
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

    public static class Instance{
        private static MainActivity mainActivity_;

        public static void set(MainActivity mainActivity){
            mainActivity_=mainActivity;
        }

        public static MainActivity get(){
            return mainActivity_;
        }
    }
}
