package com.vvdev.coolor.ui.alertdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.ColorSpec;
import com.vvdev.coolor.interfaces.ColorUtility;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;

public class ColorInfo extends Dialog implements android.view.View.OnClickListener {

    private static final String TAG = ColorInfo.class.getName();

    private ColorSpec currentColor;

    private TextView TVColorName; // TV = TextView
    private TextView TVHexa;
    private TextView TVRgb;
    private TextView TVHsv;
    private TextView TVHsl;
    private TextView TVCMYK;
    private TextView TVCieLAB;
    private ImageView preview; // used to show color

    private ImageView generate0;
    private ImageView generate1;
    private ImageView generate2;
    private ImageView generate3;
    private ImageView generate4;
    private ImageView generate5;

    private ArrayList<ImageView> generate = new ArrayList<>();

    private String[] currentGenerateColor;
    private Activity activity;

    public ColorInfo(@NonNull Activity activity, ColorSpec toParse) {
        super(activity);
        this.activity=activity;
        this.currentColor = toParse;
        Log.i(TAG,"init ColorInfoDialog with :"+currentColor.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION); // not showing status & navigation bar*/
        setContentView(R.layout.dialog_moreinformation_main);

        Window window = getWindow(); // fix bug for match_parent width
        window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT); // fix bug for match_parent width plz refer to https://stackoverflow.com/questions/28513616/android-get-full-width-for-custom-dialog

        TVColorName = findViewById(R.id.MoreIColorName);
        TVHexa = findViewById(R.id.MoreIHexa);
        TVRgb = findViewById(R.id.MoreIRGB);
        TVHsv = findViewById(R.id.MoreIHSV);
        TVHsl = findViewById(R.id.MoreIHSL);
        TVCMYK = findViewById(R.id.MoreICMYK);
        TVCieLAB = findViewById(R.id.MoreICIELAB);
        preview = findViewById(R.id.MoreIPreview);
        generate0 = findViewById(R.id.MoreIPreviewGenerate0);
        generate1 = findViewById(R.id.MoreIPreviewGenerate1);
        generate2 = findViewById(R.id.MoreIPreviewGenerate2);
        generate3 = findViewById(R.id.MoreIPreviewGenerate3);
        generate4 = findViewById(R.id.MoreIPreviewGenerate4);
        generate5 = findViewById(R.id.MoreIPreviewGenerate5);

        findViewById(R.id.MoreIOK).setOnClickListener(this);
        generate0.setOnClickListener(this);
        generate1.setOnClickListener(this);
        generate2.setOnClickListener(this);
        generate3.setOnClickListener(this);
        generate4.setOnClickListener(this);
        generate5.setOnClickListener(this);

        generate.add(generate0);generate.add(generate1);generate.add(generate2);generate.add(generate3);generate.add(generate4);generate.add(generate5);

        init(currentColor);
    }

    private void init(ColorSpec currentColor){
        TVColorName.setText(ColorUtility.nearestColor(currentColor.getHexa())[0]);

        String forHexa = "HEX : "+currentColor.getHexa();
        initTextViewColorCoding(TVHexa,forHexa,5);

        String forRGB = "RGB : "+currentColor.getRGB()[0]+", "+currentColor.getRGB()[1]+", "+currentColor.getRGB()[2];
        initTextViewColorCoding(TVRgb,forRGB,5);

        String forHSV = "HSV : "+currentColor.getHSV()[0]+"°, "+currentColor.getHSV()[1]+"%, "+currentColor.getHSV()[2]+"%";
        initTextViewColorCoding(TVHsv,forHSV,5);

        String forHSL = "HSL : "+currentColor.getHSL()[0]+"°, "+currentColor.getHSL()[1]+"%, "+currentColor.getHSL()[2]+"%";
        initTextViewColorCoding(TVHsl,forHSL,5);

        String forCMYK = "CMYK : "+currentColor.getCmyk()[0]+"%, "+currentColor.getCmyk()[1]+"%, "+currentColor.getCmyk()[2]+"%, "+currentColor.getCmyk()[3]+"%";
        initTextViewColorCoding(TVCMYK,forCMYK,6);

        String forCieLAB = "CIE LAB : "+currentColor.getCielab()[0]+", "+currentColor.getCielab()[1]+", "+currentColor.getCielab()[2];
        initTextViewColorCoding(TVCieLAB,forCieLAB,9);

        preview.setBackgroundColor(Color.parseColor(currentColor.getHexa()));

        if (ColorUtility.isNearestFromBlackThanWhite(currentColor.getHexa())) { // check if the color is closer to black than white
            currentGenerateColor = currentColor.getTints(); // setup preview generated colors by the method of generation Tints
        } else {
            currentGenerateColor = currentColor.getShades(); // setup preview generated colors by the method of generation Shades
        }

        for (int x = 0; x < generate.size(); x++) { // setup preview generated colors by the method of generation ( Shades / Tints )
            generate.get(x).setBackgroundColor(Color.parseColor(currentGenerateColor[x]));
        }
    }

    /**
     * init the text inside all color code ( like hex : #112233 ) and also set on click listener to copy the text to the text clipboard
     * @param textView
     * @param textColorCoding
     * @param colorCodeStartAt
     */
    private void initTextViewColorCoding(TextView textView, final String textColorCoding, final int colorCodeStartAt){
        textView.setText(textColorCoding);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStringToClipBoard(textColorCoding.substring(colorCodeStartAt));
                String copiedToClipBoard = activity.getString(R.string.alertdialog_colorInfo_CopyToClipboard);
                Toast.makeText(activity,copiedToClipBoard,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initNewColor(String hexaValue){
        ColorSpec color = new ColorSpec(hexaValue);
        init(color);
    }

    private void addStringToClipBoard(String toAdd){
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", toAdd);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.MoreIOK:{
                Log.i(TAG,"Closing ColorInfoDialog with color"+currentColor.getHexa());
                dismiss();
                break;
            }
            case R.id.MoreIPreviewGenerate1:{
                initNewColor(currentGenerateColor[1]);
                break;
            }
            case R.id.MoreIPreviewGenerate2:{
                initNewColor(currentGenerateColor[2]);
                break;
            }
            case R.id.MoreIPreviewGenerate3:{
                initNewColor(currentGenerateColor[3]);
                break;
            }
            case R.id.MoreIPreviewGenerate4:{
                initNewColor(currentGenerateColor[4]);
                break;
            }
            case R.id.MoreIPreviewGenerate5:{
                initNewColor(currentGenerateColor[5]);
                break;
            }
        }
    }
}
