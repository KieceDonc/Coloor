package com.vvdev.coolor.ui.alertdialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.SavedData;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class ColorAddDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = ColorAddDialog.class.getName();

    private Activity activity;

    private View preview;

    private ArrayList<View> allTv = new ArrayList<>();

    private String currentHexValue="#000000";

    private int currentIndex=0;


    public ColorAddDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
        Log.i(TAG,"started");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_addcolor);

        preview = findViewById(R.id.AddColorPreview);

        allTv.add(findViewById(R.id.AddColorTv0));
        allTv.add(findViewById(R.id.AddColorTv1));
        allTv.add(findViewById(R.id.AddColorTv2));
        allTv.add(findViewById(R.id.AddColorTv3));
        allTv.add(findViewById(R.id.AddColorTv4));
        allTv.add(findViewById(R.id.AddColorTv5));

        for(int x=0;x<allTv.size();x++){
            final int finalX = x;
            allTv.get(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeCurrentIndex(finalX);
                }
            });
        }

        changeCurrentIndex(0);

        findViewById(R.id.AddColor0).setOnClickListener(this);
        findViewById(R.id.AddColor1).setOnClickListener(this);
        findViewById(R.id.AddColor2).setOnClickListener(this);
        findViewById(R.id.AddColor3).setOnClickListener(this);
        findViewById(R.id.AddColor4).setOnClickListener(this);
        findViewById(R.id.AddColor5).setOnClickListener(this);
        findViewById(R.id.AddColor6).setOnClickListener(this);
        findViewById(R.id.AddColor7).setOnClickListener(this);
        findViewById(R.id.AddColor8).setOnClickListener(this);
        findViewById(R.id.AddColor9).setOnClickListener(this);
        findViewById(R.id.AddColorA).setOnClickListener(this);
        findViewById(R.id.AddColorB).setOnClickListener(this);
        findViewById(R.id.AddColorC).setOnClickListener(this);
        findViewById(R.id.AddColorD).setOnClickListener(this);
        findViewById(R.id.AddColorE).setOnClickListener(this);
        findViewById(R.id.AddColorF).setOnClickListener(this);
        findViewById(R.id.AddColorCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"dismiss");
                dismiss();
            }
        });
        findViewById(R.id.AddColorAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedData.getInstance(activity).addColor(currentHexValue);
                Log.i(TAG,"dismiss");
                dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v.getTag().equals("buttonHexaCode")){
            Button currentB = (Button)v;
            CharSequence currentText = currentB.getText();
            modifyValueCurrentIndex(currentText);
            Log.i(TAG,"button "+currentText.charAt(0)+" clicked");
        }
    }

    private void modifyValueCurrentIndex(CharSequence toModify){
        char[] chars = currentHexValue.toCharArray();
        chars[currentIndex+1] = toModify.charAt(0);

        currentHexValue = String.valueOf(chars);
        for(int x=0;x<allTv.size();x++){
            TextView currentTv = (TextView)allTv.get(x);
            currentTv.setText(String.valueOf(chars[x+1]));
        }
        int newIndex = currentIndex+1;
        newIndex = newIndex==6?0:newIndex;
        changeCurrentIndex(newIndex);
        preview.setBackgroundColor(Color.parseColor(currentHexValue));
    }

    private void changeCurrentIndex(int newIndex){
        Log.i(TAG,"old index ="+currentIndex);
        TextView oldCurrentIndexTv = (TextView)allTv.get(currentIndex);
        oldCurrentIndexTv.setTextColor(Color.BLACK);
        currentIndex = newIndex;
        TextView newCurrentIndexTv = (TextView) allTv.get(currentIndex);
        newCurrentIndexTv.setTextColor(activity.getResources().getColor(R.color.Theme1_Secondary));
        Log.i(TAG,"new index ="+currentIndex);
    }

}
