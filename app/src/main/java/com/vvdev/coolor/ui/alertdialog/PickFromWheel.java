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
import com.vvdev.coolor.interfaces.ColorSpec;
import com.vvdev.coolor.interfaces.ColorUtility;
import com.vvdev.coolor.interfaces.SavedData;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;

public class PickFromWheel extends Dialog {

    private final Activity activity;

    private setOnColorChoose listener;

    private ColorPickerView wheel;
    private BrightnessSlideBar brightnessSlideBar;
    private TextView hexValue;
    private TextView ok;
    private TextView cancel;
    private View preview;

    private String currentHex = "";

    public interface setOnColorChoose{
        void onColorChoose(ColorSpec colorChosen);
    }

    public PickFromWheel(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public PickFromWheel(@NonNull Activity activity, setOnColorChoose listener) {
        super(activity);
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pick_from_wheel);

        Window window = getWindow(); // fix bug for match_parent width
        if (window != null) {
            window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT); // fix bug for match_parent width plz refer to https://stackoverflow.com/questions/28513616/android-get-full-width-for-custom-dialog
        }

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
                // first plz refer to https://stackoverflow.com/questions/12139335/what-is-difference-between-dialoginterface-dismiss-and-dialoginterface-can
                // Your first calling listener.onColorChoose() so you can get back the color chosen. Then you call cancel() so it call DialogInterface.OnCancelListener and you know when
                // your dialog been destroy
                // you can not do listener.onColorChoose() and then dismiss()
                if(listener!=null){
                    listener.onColorChoose(new ColorSpec(currentHex));
                    cancel();
                }else{
                    SavedData.getInstance(activity).addColor(currentHex);
                    dismiss();
                }
            }
        });
    }
}
