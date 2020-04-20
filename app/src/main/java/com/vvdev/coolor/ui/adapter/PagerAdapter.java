package com.vvdev.coolor.ui.adapter;

import com.vvdev.coolor.fragment.TabHost.ColorsTab;
import com.vvdev.coolor.fragment.TabHost.GradientsTab;
import com.vvdev.coolor.fragment.TabHost.ImportTab;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {

    private static int NUM_ITEMS = 3;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:{
                return new ImportTab();
            }
            case 1:{
                return new ColorsTab();
            }
            case 2:{
                return new GradientsTab();
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_ITEMS;
    }
}
