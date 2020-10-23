package com.vvdev.coolor.interfaces;

import android.app.Activity;
import android.widget.Toast;

import com.vvdev.coolor.R;
import com.vvdev.coolor.fragment.TabHost.GradientsTab;

import java.util.ArrayList;

public class Gradients {

    private Activity activity;
    public static final int NUM_NATIVE_GRAD = 7; // number of total native gradients

    public Gradients(Activity activity){
        this.activity = activity;
    }

    public void firstSetup(){
        ArrayList<Gradient> gradients = new ArrayList<>();
        int startPosition = gradients.size()-1;
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Shades),Gradient.getShadesValue()));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Tones),Gradient.getTonesValue()));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Tints),Gradient.getTintsValue()));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Triadic),null));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Complementary),null));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Compound),null));
        gradients.add(new Gradient(activity.getResources().getString(R.string.ColorSpec_Analogous),null));
        int endPosition = gradients.size()-1;
        new SavedData(activity).saveGradients(gradients);
        GradientsTab gradientsTab = GradientsTab.Instance.get();// used to update gradient recycle view
        if(gradientsTab!=null){
            gradientsTab.getRecycleView().getAdapter().notifyItemRangeChanged(startPosition,endPosition);
            gradientsTab.showRv();
        }
        if(!isNativeCustomGradSetup()){
            setupNativeCustomGrad();
        }
    }

    public void setupNativeCustomGrad(){
        ArrayList<Gradient> toAdd = new ArrayList<>();
        toAdd.add(new Gradient(activity.getString(R.string.gradients_to_blue),"#0000FF"));
        toAdd.add(new Gradient(activity.getString(R.string.gradients_to_green),"#00FF00"));
        toAdd.add(new Gradient(activity.getString(R.string.gradients_to_red),"#FF0000"));
        toAdd.add(new Gradient(activity.getString(R.string.gradients_to_orange),"#FFA500"));
        toAdd.add(new Gradient(activity.getString(R.string.gradients_to_cyan),"#00FFFF"));
        toAdd.add(new Gradient(activity.getString(R.string.gradients_to_purple),"#800080"));

        ArrayList<Gradient> gradients = getSavedGradients();
        int startPosition = gradients.size()-1;
        boolean atLeastOneAdded = false; // boolean to say if one gradient have been added
        for(int x=0;x<toAdd.size();x++){
            if(canBeAdd(toAdd.get(x))){
                gradients.add(toAdd.get(x));
                atLeastOneAdded=true;
            }
        }

        if(atLeastOneAdded){
            int endPosition = gradients.size()-1;
            new SavedData(activity).saveGradients(gradients).updateNativeCustomAlreadySetup(true);
            GradientsTab gradientsTab = GradientsTab.Instance.get();// used to update gradient recycle view
            if(gradientsTab!=null){
                gradientsTab.getRecycleView().getAdapter().notifyItemRangeChanged(startPosition,endPosition);
                gradientsTab.showRv();
            }
        }else{
            Toast.makeText(activity,activity.getString(R.string.gradients_native_custom_already_added),Toast.LENGTH_LONG).show();
        }
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
        GradientsTab gradientsTab = GradientsTab.Instance.get();// used to update gradient recycle view
        if(gradientsTab!=null){
            gradientsTab.getRecycleView().getAdapter().notifyItemInserted(gradients.size()-1);
            gradientsTab.showRv();
        }
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
            GradientsTab gradientsTab = GradientsTab.Instance.get(); // used to update gradient recycle view
            if(gradientsTab!=null){
                gradientsTab.getRecycleView().getAdapter().notifyItemRemoved(cmpt);
                gradientsTab.showRv();
            }
        }
    }

    public void removeAll(){
        SavedData.getInstance(activity).saveGradients(new ArrayList<Gradient>()); // we create a new instance and save it so the old one got deleted;
        GradientsTab.Instance.get().setupGradientsRecycleView(); // used to update gradient recycle view
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
        for(int x=NUM_NATIVE_GRAD;x<gradients.size();x++){
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

    /**
     * Check if native custom grad have already been setup once
     * @return true / false
     */
    public boolean isNativeCustomGradSetup(){
        return new SavedData(activity).isNativeCustomAlreadySetup();
    }

    public boolean canBeAdd(Gradient gradient){
        ArrayList<Gradient> gradients = getSavedGradients();
        int cmpt = 0;
        boolean founded = false;
        do{
            Gradient currentGradient = gradients.get(cmpt);
            if(currentGradient.equals(gradient)){
                gradients.remove(cmpt);
                founded=true;
            }
            cmpt++;
        }while(cmpt<gradients.size()&&!founded);
        return !founded;
    }

    public static Gradients getInstance(Activity activity){
        return new Gradients(activity);
    }
}
