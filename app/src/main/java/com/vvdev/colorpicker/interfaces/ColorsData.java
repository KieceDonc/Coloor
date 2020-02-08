package com.vvdev.colorpicker.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class ColorsData { // https://stackoverflow.com/questions/7145606/how-android-sharedpreferences-save-store-object

    private ArrayList<ColorSpec> colors;
    private SharedPreferences mPrefs;
    private static final String PREFS_TAG = "SharedPrefs";

    public ColorsData(Activity activity){
        mPrefs = activity.getApplicationContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        colors = getColors();
    }

    /*public ColorsData(Context context){
        mPrefs = context.getSharedPreferences(MODE_PRIVATE);
        colors = new ArrayList<>();
    }*/

    public void addColor(ColorSpec color){
        colors.add(color);
        saveColors();
    }

    public void removeColor(ColorSpec color){
        colors.remove(color);
        saveColors();
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
     * @return ArrayList<ColorSpec> in memory
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

    /**
     * Just used to copy / past variable colors
     */
    private class Copy {
        public ArrayList<ColorSpec> colorsSP;
    }

    /*public void LogENewList(){
        Gson gson = new Gson();
        String json = mPrefs.getString("colorsArrayList", "");
        String s = Arrays.toString(gson.fromJson(json, Copy.class).colorsSP.toArray());
        Log.e("test",s);
    }*/

}