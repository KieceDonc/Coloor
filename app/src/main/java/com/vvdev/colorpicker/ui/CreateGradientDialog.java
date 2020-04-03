package com.vvdev.colorpicker.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.activity.MainActivity;
import com.vvdev.colorpicker.fragment.BottomBar.Palette;
import com.vvdev.colorpicker.interfaces.ColorSpec;
import com.vvdev.colorpicker.interfaces.ColorUtility;
import com.vvdev.colorpicker.interfaces.Gradient;
import com.vvdev.colorpicker.interfaces.Gradients;
import com.vvdev.colorpicker.interfaces.SavedData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

public class CreateGradientDialog extends Dialog {

    private static final String TAG = CreateGradientDialog.class.getName();

    private ColorSpec currentColor;

    private Activity activity;
    private EditText userInput;

    private TextView save;
    private TextView cancel;

    private ArrayList<View> rsltLayout = new ArrayList<>();

    private String lengthError="lengthError";
    private String nameAlreadyInDataBase="nameAlreadyInDataBase";

    public CreateGradientDialog(@NonNull Activity activity,@NonNull ColorSpec currentColor) {
        super(activity);
        this.activity = activity;
        this.currentColor = currentColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_create_gradient);

        userInput = findViewById(R.id.cgInputName);

        userInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(userInput.getTextColors().equals(activity.getResources().getColor(R.color.import_internet_url_text_error))){
                    userInput.setTextColor(Color.BLACK);
                    userInput.setText("");
                }else if(userInput.getText().toString().equals(activity.getResources().getString(R.string.alertdialog_gradient_choose_name))){
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
                    Gradients.addGradient(activity,new Gradient(userInput.getText().toString(),currentColor.getHexa()));
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

    private String canBeSave(){
        String stringUserInput = userInput.getText().toString();
        if(stringUserInput.length()>0&&!stringUserInput.equals((activity.getResources().getString(R.string.alertdialog_gradient_choose_name)))){
            if(Gradients.getGradientValueByName(activity,stringUserInput)==null){
                return null;
            }else{
                return nameAlreadyInDataBase;
            }
        }else{
            return lengthError;
        }
    }

    public void updateSpinnersViewInAdapter(){
        Palette palette = MainActivity.Instance.getPaletteInstance();
        if(palette!=null){
            if(palette.getRecycleView()!=null){
                if(palette.getPaletteRVAdapter()!=null){
                    palette.getPaletteRVAdapter().updateSpinner();
                }
            }
        }
    }


}
