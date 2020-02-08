package com.vvdev.colorpicker.interfaces;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ColorsData { // https://stackoverflow.com/questions/7145606/how-android-sharedpreferences-save-store-object

    private ArrayList<ColorSpec> colors;
    private SharedPreferences mPrefs;

    public ColorsData(Activity activity){
        mPrefs = activity.getPreferences(MODE_PRIVATE);
        colors = new ArrayList<>();
    }

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
        copy.colorsSP=colors;
        String json = gson.toJson(copy);
        prefsEditor.putString("colorsArrayList", json);
        prefsEditor.apply();
    }

    /**
     * @return ArrayList<ColorSpec> in memory
     */
    public ArrayList<ColorSpec> getColors(){
        Gson gson = new Gson();
        String json = mPrefs.getString("colorsArrayList", "");
        return gson.fromJson(json,Copy.class).colorsSP;
    }

    /**
     * Just used to copy / past variable colors
     */
    private class Copy {
        private ArrayList<ColorSpec> colorsSP;
    }

}