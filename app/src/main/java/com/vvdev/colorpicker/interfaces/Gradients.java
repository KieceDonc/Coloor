package com.vvdev.colorpicker.interfaces;

import android.app.Activity;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.activity.MainActivity;
import com.vvdev.colorpicker.fragment.BottomBar.Import;
import com.vvdev.colorpicker.fragment.BottomBar.Palette;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Gradients {

    private Activity activity;

    public Gradients(Activity activity){
        this.activity = activity;
    }

    public void firstSetup(){
        ArrayList<Gradient> gradients = new ArrayList<>();
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Shades),Gradient.getShadesValue()));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Tones),Gradient.getTonesValue()));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Tints),Gradient.getTintsValue()));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Triadic),null));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Complementary),null));
        new SavedData(activity).saveGradients(gradients);
    }

    public String getGradientValueByName(String name){
        ArrayList<Gradient> gradients = getSavedGradients();
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

    public void addGradient( Gradient gradient){
        ArrayList<Gradient> gradients = getSavedGradients();
        gradients.add(gradient);
        new SavedData(activity).saveGradients(gradients);
    }

    public void removeGradient(Gradient gradient){
        ArrayList<Gradient> gradients = getSavedGradients();
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

    public ArrayList<String> getAllGradientsName(){
        ArrayList<Gradient> gradients = getSavedGradients();
        ArrayList<String> gradientsName = new ArrayList<>();
        for(int x=0;x<gradients.size();x++){
            gradientsName.add(gradients.get(x).getName());
        }
        return gradientsName;
    }

    public ArrayList<Gradient> getAllCustomGradients(){
        ArrayList<Gradient> gradients = getSavedGradients();
        ArrayList<Gradient> toReturn = new ArrayList<>();
        for(int x=5;x<gradients.size();x++){
            toReturn.add(gradients.get(x));
        }
        return toReturn;
    }

    public ArrayList<Gradient> getSavedGradients(){
        return new SavedData(activity).getSavedGradients();
    }

    public static Gradients getInstance(Activity activity){
        return new Gradients(activity);
    }
}
