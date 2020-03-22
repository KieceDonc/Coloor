package com.vvdev.colorpicker.interfaces;

import android.app.Activity;

import com.vvdev.colorpicker.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Gradients {

    public static void firstSetup(@NonNull Activity activity){
        ArrayList<Gradient> gradients = new ArrayList<>();
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Shades),Gradient.getShadesValue()));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Tones),Gradient.getTonesValue()));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Tints),Gradient.getTintsValue()));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Triadic),null));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Complementary),null));
        new SavedData(activity).saveGradients(gradients);
    }

    public static String getGradientValueByName(@NonNull Activity activity,String name){
        ArrayList<Gradient> gradients = getSavedGradients(activity);
        String value=null;
        int cmpt = 0;
        boolean founded = false;
        do{
            Gradient currentGradient = gradients.get(cmpt);
            if(currentGradient.getName().equals(name)){
                value=currentGradient.getHexaValue();
                founded=true;
            }
            cmpt++;
        }while(cmpt<gradients.size()&&!founded);
        return value;
    }

    public static void addGradient(@NonNull Activity activity, Gradient gradient){
        ArrayList<Gradient> gradients = getSavedGradients(activity);
        gradients.add(gradient);
        new SavedData(activity).saveGradients(gradients);
    }

    public static void deleteGradient(@NonNull Activity activity,Gradient gradient){
        ArrayList<Gradient> gradients = getSavedGradients(activity);
        int cmpt = 0;
        boolean removed = false;
        do{
            Gradient currentGradient = gradients.get(cmpt);
            if(currentGradient.equals(gradient)){
                gradients.remove(cmpt);
                removed=true;
            }
            cmpt++;
        }while(cmpt<gradients.size()&&!removed);
        if(removed){
            new SavedData(activity).saveGradients(gradients);
        }
    }

    public static ArrayList<String> getAllGradientsName(Activity activity){
        ArrayList<Gradient> gradients = getSavedGradients(activity);
        ArrayList<String> gradientsName = new ArrayList<>();
        for(int x=0;x<gradients.size();x++){
            gradientsName.add(gradients.get(x).getName());
        }
        return gradientsName;
    }

    public static ArrayList<Gradient> getAllCustomGradients(Activity activity){
        ArrayList<Gradient> gradients = getSavedGradients(activity);
        ArrayList<Gradient> toReturn = new ArrayList<>();
        for(int x=5;x<gradients.size();x++){
            toReturn.add(gradients.get(x));
        }
        return toReturn;
    }

    public static ArrayList<Gradient> getSavedGradients(Activity activity){
        return new SavedData(activity).getSavedGradients();
    }




}
