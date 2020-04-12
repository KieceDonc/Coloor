package com.vvdev.coolor.ui.alertdialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;
import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.ColorUtility;
import com.vvdev.coolor.interfaces.SavedData;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;

public class ColorPickFromWheelDialog extends Dialog {

    private Activity activity;

    private ColorPickerView wheel;
    private BrightnessSlideBar brightnessSlideBar;
    private TextView hexValue;
    private TextView ok;
    private TextView cancel;
    private View preview;

    private String currentHex = "";
    public ColorPickFromWheelDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_pick_from_wheel);

        Window window = getWindow(); // fix bug for match_parent width
        window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT); // fix bug for match_parent width plz refer to https://stackoverflow.com/questions/28513616/android-get-full-width-for-custom-dialog

        wheel = findViewById(R.id.PickFromWheelPickerView);
        brightnessSlideBar = findViewById(R.id.PickFromWheelBrightnessSlide);
        hexValue = findViewById(R.id.PickFromWheelHexValue);
        ok = findViewById(R.id.PickFromWheelOk);
        cancel = findViewById(R.id.PickFromWheelCancel);
        preview = findViewById(R.id.PickFromWheelPreview);

        wheel.attachBrightnessSlider(brightnessSlideBar);

        wheel.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color, boolean fromUser) {
                preview.setBackgroundColor(color);
                int[] RGB = new int[]{Color.red(color),Color.green(color),Color.blue(color)};
                String hex = ColorUtility.getHexFromRGB(RGB);
                hexValue.setText(hex);
                currentHex = hex;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SavedData(activity).addColor(currentHex);
                dismiss();
            }
        });
    }
}
