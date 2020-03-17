package com.vvdev.colorpicker.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorSpec;
import com.vvdev.colorpicker.interfaces.ColorUtility;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;

public class ColorInfoDialog extends Dialog implements android.view.View.OnClickListener {

    private ColorSpec currentColor;

    private TextView TVColorName; // TV = TextView
    private TextView TVHexa;
    private TextView TVRgb;
    private TextView TVHsv;
    private TextView TVHsl;
    private TextView TVCieLAB;
    private TextView TVCieXYZ;
    private ImageView preview; // used to show color

    private ImageView generate0;
    private ImageView generate1;
    private ImageView generate2;
    private ImageView generate3;
    private ImageView generate4;
    private ImageView generate5;

    private ArrayList<ImageView> generate = new ArrayList<>();

    private static final String TAG = ColorInfoDialog.class.getName();

    public ColorInfoDialog(@NonNull Activity activity, ColorSpec toParse) {
        super(activity);
        this.currentColor = toParse;
        Log.i(TAG,"init ColorInfoDialog with :"+currentColor.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_moreinformation_main);

        Window window = getWindow(); // fix bug for match_parent width
        window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT); // fix bug for match_parent width plz refer to https://stackoverflow.com/questions/28513616/android-get-full-width-for-custom-dialog

        TVColorName = findViewById(R.id.MoreIColorName);
        TVHexa = findViewById(R.id.MoreIHexa);
        TVRgb = findViewById(R.id.MoreIRGB);
        TVHsv = findViewById(R.id.MoreIHSV);
        TVHsl = findViewById(R.id.MoreIHSL);
        TVCieLAB = findViewById(R.id.MoreICIELAB);
        TVCieXYZ = findViewById(R.id.MoreICIEXYZ);
        preview = findViewById(R.id.MoreIPreview);
        generate0 = findViewById(R.id.MoreIPreviewGenerate0);
        generate1 = findViewById(R.id.MoreIPreviewGenerate1);
        generate2 = findViewById(R.id.MoreIPreviewGenerate2);
        generate3 = findViewById(R.id.MoreIPreviewGenerate3);
        generate4 = findViewById(R.id.MoreIPreviewGenerate4);
        generate5 = findViewById(R.id.MoreIPreviewGenerate5);

        generate.add(generate0);generate.add(generate1);generate.add(generate2);generate.add(generate3);generate.add(generate4);generate.add(generate5);


        TVColorName.setText(ColorUtility.nearestColor(currentColor.getHexa())[0]);

        String forHexa = "Hexa : "+currentColor.getHexa();
        TVHexa.setText(forHexa);

        String forRGB = "RGB : "+currentColor.getRGB()[0]+", "+currentColor.getRGB()[1]+", "+currentColor.getRGB()[2];
        TVRgb.setText(forRGB);

        String forHSV = "HSV / HSB : "+currentColor.getHSV()[0]+"°, "+currentColor.getHSV()[1]+"%, "+currentColor.getHSV()[2]+"%";
        TVHsv.setText(forHSV);

        String forHSL ="HSL : WIP"; // TODO implement HSL
        TVHsl.setText(forHSL);

        String forCieLAB = "CIE LAB = WIP";
        TVCieLAB.setText(forCieLAB);

        String forCieXYZ = "CIE XYZ = WIP";
        TVCieXYZ.setText(forCieXYZ);

        preview.setBackgroundColor(Color.parseColor(currentColor.getHexa()));

        String[] toShow;
        if (ColorUtility.isNearestFromBlackThanWhite(currentColor.getHexa())) { // check if the color is closer to black than white
            toShow = currentColor.getTints(); // setup preview generated colors by the method of generation Tints
        } else {
            toShow = currentColor.getShades(); // setup preview generated colors by the method of generation Shades
        }

        for (int x = 0; x < generate.size(); x++) { // setup preview generated colors by the method of generation ( Shades / Tints )
            generate.get(x).setBackgroundColor(Color.parseColor(toShow[x]));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.MoreIOK:{ // TODO make it work
                Log.i(TAG,"Closing ColorInfoDialog with color"+currentColor.getHexa());
                dismiss();
                break;
            }
        }
    }
}
