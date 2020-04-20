package com.vvdev.coolor.interfaces;

import android.app.Activity;

import com.vvdev.coolor.R;

import java.util.ArrayList;

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

    public void add(Gradient gradient){
        ArrayList<Gradient> gradients = getSavedGradients();
        gradients.add(gradient);
        SavedData.getInstance(activity).saveGradients(gradients);
    }

    public void remove(Gradient gradient){
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
            SavedData.getInstance(activity).saveGradients(gradients);
        }
    }

    public void removeAll(){
        SavedData.getInstance(activity).saveGradients(new ArrayList<Gradient>()); // we create a new instance and save it so the old one got deleted;
        firstSetup();
    }

    public ArrayList<String> getAllName(){
        ArrayList<Gradient> gradients = getSavedGradients();
        ArrayList<String> gradientsName = new ArrayList<>();
        for(int x=0;x<gradients.size();x++){
            gradientsName.add(gradients.get(x).getName());
        }
        return gradientsName;
    }

    public ArrayList<Gradient> getAllCustom(){
        ArrayList<Gradient> gradients = getSavedGradients();
        ArrayList<Gradient> toReturn = new ArrayList<>();
        for(int x=5;x<gradients.size();x++){
            toReturn.add(gradients.get(x));
        }
        return toReturn;
    }

    public int size(){
        ArrayList<Gradient> gradients = getSavedGradients();
        if(gradients==null){
            return -1;
        }else{
            return gradients.size();
        }
    }

    public ArrayList<Gradient> getSavedGradients(){
        return SavedData.getInstance(activity).getSavedGradients();
    }

    public static Gradients getInstance(Activity activity){
        return new Gradients(activity);
    }
}
