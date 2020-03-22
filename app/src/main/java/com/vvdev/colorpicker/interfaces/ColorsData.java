package com.vvdev.colorpicker.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.fragment.BottomBar.Palette;
import com.vvdev.colorpicker.ui.ColorAddedToast;
import com.vvdev.colorpicker.ui.PaletteRVAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ColorsData { // https://stackoverflow.com/questions/7145606/how-android-sharedpreferences-save-store-object

    private static final String TAG = ColorsData.class.getName();

    private ArrayList<ColorSpec> colors;
    private SharedPreferences mPrefs;
    private Activity activity;
    private static Palette palette;
    private static final String PREFS_TAG = "SharedPrefs";

    public ColorsData(Activity activity){
        mPrefs = activity.getApplicationContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        this.activity=activity;
        colors = getColors();
    }

    public void addColor(String color){
        colors.add(new ColorSpec(getGeneratedMethodName(),color));
        saveColors();
        ColorAddedToast.show(activity,activity.getLayoutInflater(),color);
        if(palette!=null){
            palette.getRecycleView().getAdapter().notifyItemInserted(getSize()-1);
            if(getSize()==1){
                palette.showColors();
            }
        }
        Log.i(TAG,"color added. Hexa value = "+color);
    }


    public void removeColor(int position){
        if(position<=getSize()-1){
            colors.remove(position);
            saveColors();
            if(palette!=null){
                palette.getRecycleView().getAdapter().notifyItemRemoved(position);
                palette.getRecycleView().getAdapter().notifyItemRangeChanged(position,getSize());
                if(getSize()==0){
                    palette.showTutorial();
                }
            }
            Log.i(TAG,"color deleted at position :"+position+". Size list ="+getSize());
        }else{
            Log.e(TAG,"trying to delete a color who isn't in the list. List size :"+getSize()+" position trying to delete : "+position);
        }
    }

    public void clearColors(){
        colors.clear();
        saveColors();
        if(palette!=null){
            PaletteRVAdapter PaletteRVAdapter = new PaletteRVAdapter(activity);
            palette.getRecycleView().setAdapter(PaletteRVAdapter);
            palette.getActionMenu().showMenuButton(true);
            palette.showTutorial();
        }
        Log.i(TAG,"all colors deleted ( clearColors() ) ");
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
        Log.i(TAG,"New color list saved");
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

    private ArrayList<String> getGeneratedMethodName(){
        ArrayList<String> methodName = new ArrayList<>();
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Shades));
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Tones));
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Tints));
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Triadic));
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Complementary));
        return methodName;
    }

    public static ArrayList<String> getGeneratedMethodName(Activity activity){
        ArrayList<String> methodName = new ArrayList<>();
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Shades));
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Tones));
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Tints));
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Triadic));
        methodName.add(activity.getResources().getString(R.string.ColorSpec_Complementary));
        return methodName;
    }

    public void setInstancePalette(Palette palette){
        this.palette = palette;
    }

    public String toString(){
        StringBuilder toReturn= new StringBuilder("ColorsData list of ColorSpec :");
        for(int x=0;x<colors.size();x++){
            toReturn.append(colors.get(x).toString()+"\n");
        }
        return toReturn.toString();
    }

    /**
     * Just used to copy / past variable colors
     */
    private class Copy {
        public ArrayList<ColorSpec> colorsSP;
    }


}