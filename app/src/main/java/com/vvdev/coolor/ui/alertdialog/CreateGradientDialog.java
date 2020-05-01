package com.vvdev.coolor.ui.alertdialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vvdev.coolor.R;
import com.vvdev.coolor.fragment.TabHost.ColorsTab;
import com.vvdev.coolor.interfaces.ColorSpec;
import com.vvdev.coolor.interfaces.ColorUtility;
import com.vvdev.coolor.interfaces.Gradient;
import com.vvdev.coolor.interfaces.Gradients;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class CreateGradientDialog extends Dialog {

    private static final String TAG = CreateGradientDialog.class.getName();

    private setOnGradientSaved listener;

    private ColorSpec currentColor;

    private Activity activity;
    private EditText userInput;
    private View colorView;

    private TextView save;
    private TextView cancel;

    private ArrayList<View> rsltLayout = new ArrayList<>();

    private String lengthError="lengthError";
    private String nameAlreadyInDataBase="nameAlreadyInDataBase";

    public interface setOnGradientSaved{
        void onGradientSaved();
    }

    public CreateGradientDialog(@NonNull Activity activity,@NonNull ColorSpec currentColor) {
        super(activity);
        this.activity = activity;
        this.currentColor = currentColor;
    }

    public CreateGradientDialog(@NonNull Activity activity,@NonNull ColorSpec currentColor,setOnGradientSaved listener) {
        super(activity);
        this.activity = activity;
        this.currentColor = currentColor;
        this.listener=listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_gradient);

        userInput = findViewById(R.id.cgInputName);

        userInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(userInput.getTextColors().equals(activity.getResources().getColor(R.color.import_internet_url_text_error))){
                    userInput.setTextColor(Color.BLACK);
                    userInput.setText("");
                }else if(userInputHaveForbiddenString()){
                    userInput.setText("");
                }
            }
        });


        rsltLayout.add(findViewById(R.id.cgResultFromBlack));
        rsltLayout.add(findViewById(R.id.cgResultFromWhite));

        cancel = findViewById(R.id.cgCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        save = findViewById(R.id.cgSave);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String canBeSave = canBeSave();
                if(canBeSave==null){
                    Gradients.getInstance(activity).add(new Gradient(userInput.getText().toString(),currentColor.getHexa()));
                    if(listener!=null){
                        listener.onGradientSaved();
                    }
                    updateSpinnersViewInAdapter();
                    dismiss();
                }else{
                    if(canBeSave.equals(lengthError)){
                        userInput.setTextColor(activity.getResources().getColor(R.color.import_internet_url_text_error)); // red color to show an error to the user
                        userInput.setText((activity.getResources().getString(R.string.alertdialog_gradient_mustchoose_name)));

                    }else if(canBeSave.equals(nameAlreadyInDataBase)){
                        userInput.setTextColor(activity.getResources().getColor(R.color.import_internet_url_text_error)); // red color to show an error to the user
                        userInput.setText(activity.getResources().getString(R.string.alertdialog_gradient_name_alreadytaken));
                    }
                }
            }
        });

        colorView = findViewById(R.id.cgColorView);
        colorView.setBackgroundColor(Color.parseColor(currentColor.getHexa()));

        showGeneratedResult();

    }

    private void showGeneratedResult(){
        String[] toColor = new String[]{"#000000","#FFFFFF","#FF0000","#00FF00","#0000FF"};
        for(int x=0;x<rsltLayout.size();x++){
            String[] hexResult = ColorUtility.gradientApproximatelyGenerator(currentColor.getHexa(),toColor[x],6);
            View currentRsltRootLayout = rsltLayout.get(x);
            for(int y=0;y<hexResult.length;y++){
                switch (y) {
                    case 0: {
                        currentRsltRootLayout.findViewById(R.id.cgResult0).setBackgroundColor(Color.parseColor(hexResult[y]));
                        break;
                    }
                    case 1: {
                        currentRsltRootLayout.findViewById(R.id.cgResult1).setBackgroundColor(Color.parseColor(hexResult[y]));
                        break;
                    }
                    case 2: {
                        currentRsltRootLayout.findViewById(R.id.cgResult2).setBackgroundColor(Color.parseColor(hexResult[y]));
                        break;
                    }
                    case 3: {
                        currentRsltRootLayout.findViewById(R.id.cgResult3).setBackgroundColor(Color.parseColor(hexResult[y]));
                        break;
                    }
                    case 4: {
                        currentRsltRootLayout.findViewById(R.id.cgResult4).setBackgroundColor(Color.parseColor(hexResult[y]));
                        break;
                    }
                    case 5: {
                        currentRsltRootLayout.findViewById(R.id.cgResult5).setBackgroundColor(Color.parseColor(hexResult[y]));
                        break;
                    }
                }
            }

        }
    }


    /**
     * Check if the name chosen by the user can the save
     * @return error type ( if no error return null )
     */
    private String canBeSave(){
        String stringUserInput = userInput.getText().toString();
        if(Gradients.getInstance(activity).size()>0){
            if(stringUserInput.length()>0&&!userInputHaveForbiddenString()){
                if(Gradients.getInstance(activity).getGradientValueByName(stringUserInput)==null){
                    return null;
                }else{
                    return nameAlreadyInDataBase;
                }
            }else{
                return lengthError;
            }
        }else{
            return null;
        }
    }

    private boolean userInputHaveForbiddenString(){
        final String userInputS = userInput.getText().toString();
        return(userInputS.equals(activity.getResources().getString(R.string.alertdialog_gradient_choose_name))
                ||
                userInputS.equals(activity.getResources().getString(R.string.alertdialog_gradient_mustchoose_name))
                ||
                userInputS.equals(activity.getResources().getString(R.string.alertdialog_gradient_name_alreadytaken)));
    }

    public void updateSpinnersViewInAdapter(){
        ColorsTab colorsTab = ColorsTab.Instance.get();
        if(colorsTab!=null){
            if(colorsTab.getRecycleView()!=null){
                if(colorsTab.getColorsTabRVAdapter()!=null){
                    colorsTab.getColorsTabRVAdapter().updateSpinner();
                }
            }
        }
    }


}
