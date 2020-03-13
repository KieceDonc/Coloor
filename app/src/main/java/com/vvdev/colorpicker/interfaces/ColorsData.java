package com.vvdev.colorpicker.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;
import com.vvdev.colorpicker.fragment.BottomBar.Palette;
import com.vvdev.colorpicker.ui.CustomToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ColorsData { // https://stackoverflow.com/questions/7145606/how-android-sharedpreferences-save-store-object

    private static final String TAG =ColorsData.class.getName();

    private ArrayList<ColorSpec> colors;
    private SharedPreferences mPrefs;
    private Activity activity;
    private static final String PREFS_TAG = "SharedPrefs";

    public ColorsData(Activity activity){
        mPrefs = activity.getApplicationContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        this.activity=activity;
        colors = getColors();
    }

    public void addColor(String color){
        colors.add(new ColorSpec(color));
        saveColors();
        CustomToast.show(activity,activity.getLayoutInflater(),color);
        if(Palette.recyclerView!=null){
            Palette.recyclerView.getAdapter().notifyItemInserted(getSize()-1);
        }
    }


    public void removeColor(int position){
        if(position<=getSize()-1){
            colors.remove(position);
            saveColors();
        }else{
            Log.e(TAG,"trying to delete a color who isn't in the list. List size :"+getSize()+" position trying to delete : "+position);
        }
    }

    public void clearColors(){
        colors.clear();
        saveColors();
    }

    public int getSize(){
        return colors.size();
    }

    /**
     * save colors to memory
     */
    private void saveColors(){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        Copy copy = new Copy();
        copy.colorsSP = new ArrayList<>(colors);
        String json = gson.toJson(copy);
        prefsEditor.putString("colorsArrayList", json);
        prefsEditor.commit();
    }

    /**
     * @return ArrayList<ColorSpec> from memory
     */
    public ArrayList<ColorSpec> getColors(){
        Gson gson = new Gson();
        String json = mPrefs.getString("colorsArrayList", "");
        Copy copy = gson.fromJson(json,Copy.class);
        if(copy==null){
            return new ArrayList<>();
        }else{
            return copy.colorsSP;
        }
    }

    public ArrayList<ColorSpec> getShortedColors(){
        ArrayList<ColorSpec> colors = getColors();
        Collections.sort(colors, new Comparator<ColorSpec>() {
            @Override
            public int compare(ColorSpec o1, ColorSpec o2) {
                int androido1 = Color.parseColor(o1.getHexa());
                int androido2 = Color.parseColor(o2.getHexa());
                double calculo1 = ((Color.red(androido1) * 0.299) + (Color.green(androido1) * 0.587) + (Color.blue(androido1) * 0.114));
                double calculo2 = ((Color.red(androido2) * 0.299) + (Color.green(androido2) * 0.587) + (Color.blue(androido2) * 0.114));
                return Double.compare(calculo1, calculo2);
            }
        });
        return colors;
    }

    /**
     * Just used to copy / past variable colors
     */
    private class Copy {
        public ArrayList<ColorSpec> colorsSP;
    }


}