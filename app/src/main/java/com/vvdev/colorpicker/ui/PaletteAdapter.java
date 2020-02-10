package com.vvdev.colorpicker.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorSpec;
import com.vvdev.colorpicker.interfaces.ColorUtility;
import com.vvdev.colorpicker.interfaces.ColorsData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import androidx.recyclerview.widget.RecyclerView;

public class PaletteAdapter extends RecyclerView.Adapter<PaletteAdapter.MyViewHolder> {

    private ArrayList<ColorSpec> colors;
    private final Activity activity;

    public PaletteAdapter(ArrayList<ColorSpec> colors,Activity activity){
        this.colors=colors;
        this.activity=activity;
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.palette_itemrecycle, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ColorSpec currentColorSpec = colors.get(position);
        holder.display(currentColorSpec);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ArrayList<String[]> allGeneratedColors;

        private CircleImageView colorPreview;
        private TextView colorName;
        private TextView hsv;
        private TextView rgb;
        private TextView hexa;
        private TextView more;
        private ConstraintLayout piExtend;

        private ArrayList<View> generate = new ArrayList<>();

        private Spinner extendSpinner;
        private ArrayList<ConstraintLayout> extendInclude = new ArrayList<>();
        private ArrayList<View> extendView = new ArrayList<>();
        private ArrayList<TextView> extendHex = new ArrayList<>();
        private ArrayList<TextView> extendRGB = new ArrayList<>();

        public MyViewHolder(final View itemView) { //
            super(itemView);

            colorPreview = itemView.findViewById(R.id.piColorPreview);

            colorName = itemView.findViewById(R.id.piColorName);
            hsv = itemView.findViewById(R.id.piHSV);
            rgb = itemView.findViewById(R.id.piRGB);
            hexa = itemView.findViewById(R.id.piHex);
            more = itemView.findViewById(R.id.piMore);

            generate.add(itemView.findViewById(R.id.piGenerate0));
            generate.add(itemView.findViewById(R.id.piGenerate1));
            generate.add(itemView.findViewById(R.id.piGenerate2));
            generate.add(itemView.findViewById(R.id.piGenerate3));
            generate.add(itemView.findViewById(R.id.piGenerate4));
            generate.add(itemView.findViewById(R.id.piGenerate5));

            piExtend = itemView.findViewById(R.id.piExtend);

            extendSpinner = itemView.findViewById(R.id.piSpinner);
            extendInclude.add((ConstraintLayout)itemView.findViewById(R.id.piExtendGenerate0));
            extendInclude.add((ConstraintLayout)itemView.findViewById(R.id.piExtendGenerate1));
            extendInclude.add((ConstraintLayout)itemView.findViewById(R.id.piExtendGenerate2));
            extendInclude.add((ConstraintLayout)itemView.findViewById(R.id.piExtendGenerate3));
            extendInclude.add((ConstraintLayout)itemView.findViewById(R.id.piExtendGenerate4));
            extendInclude.add((ConstraintLayout)itemView.findViewById(R.id.piExtendGenerate5));

            for(int x=0;x<extendInclude.size();x++){
                extendView.add(extendInclude.get(x).findViewById(R.id.piExtendView));
                extendHex.add((TextView)extendInclude.get(x).findViewById(R.id.piExtendHex));
                extendRGB.add((TextView)extendInclude.get(x).findViewById(R.id.piExtendRGB));
            }

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(piExtend.getVisibility()==View.VISIBLE){
                        piExtend.setVisibility(View.GONE);
                    }else{
                        piExtend.setVisibility(View.VISIBLE);
                    }
                }
            });

            // set the extend spinner on item click listener and change each extend include to the colors propriety of selected item
            extendSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    int selectedItem = parentView.getSelectedItemPosition();
                    String[] colorsOfItem = allGeneratedColors.get(selectedItem);
                    showNumberExtendInclude(colorsOfItem.length);
                    changeExtendInclude(colorsOfItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }

        public void display(ColorSpec colorSpec) {

            // get hexa, hsv, rgb of color
            String hexaFromColorSpec = colorSpec.getHexa();
            int[] hsvFromColorSpec = colorSpec.getHSV();
            int[] rgbFromColorSpec = colorSpec.getRGB();

            // setup preview of color
            Bitmap bitmapOfPreview = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888);
            bitmapOfPreview.eraseColor(Color.parseColor(hexaFromColorSpec));
            colorPreview.setImageBitmap(bitmapOfPreview);

            // setup text
            colorName.setText(ColorUtility.nearestColor(hexaFromColorSpec)[0]);
            String toHSV = "HSV : "+hsvFromColorSpec[0]+", "+hsvFromColorSpec[1]+", "+hsvFromColorSpec[2];
            hsv.setText(toHSV);
            String toRGB = "RGB : "+rgbFromColorSpec[0]+", "+rgbFromColorSpec[1]+", "+rgbFromColorSpec[2];
            rgb.setText(toRGB);
            String toHexa = "Hexa : "+hexaFromColorSpec;
            hexa.setText(toHexa);

            // setup all generated colors
            allGeneratedColors=colorSpec.getAllGeneratedColors();


            // setup extend spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item,colorSpec.getGenerateMethod());
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            extendSpinner.setAdapter(spinnerAdapter);

            showNumberExtendInclude(6); // set the good number of include for generated colors method shades
            String[] toShow; // setup preview generated colors by the method of generation shades
            if(ColorUtility.isNearestFromBlackThanWhite(colorSpec.getHexa())){ // check if the color is closer to black than white
                extendSpinner.setSelection(2); // set spinner to position of Tints ( it will also set extendInclude to Tints mode )
                toShow = colorSpec.getTines(); // setup preview generated colors by the method of generation Tints

            }else{
                extendSpinner.setSelection(0); // set spinner to position of Shades ( it will also set extendInclude to Shades mode )
                toShow = colorSpec.getShades(); // setup preview generated colors by the method of generation Shades
                /*int totalRGB = rgbFromColorSpec[0]+rgbFromColorSpec[1]+rgbFromColorSpec[2];
                if(totalRGB>720){ // if color is very close to white, we add black border
                    colorPreview.setBorderColor(Color.BLACK);
                    colorPreview.setBorderWidth(4);
                }*/
            }
            for(int x=0;x<generate.size();x++){ // setup preview generated colors by the method of generation ( Shades / Tints )
                generate.get(x).setBackgroundColor(Color.parseColor(toShow[x]));
            }
        }

        /**
         * Change the visibility of extend include depending of generated colors length
         * @param number generated colors length
         */
        private void showNumberExtendInclude(int number){
            for(int x=0;x<number;x++){
                extendInclude.get(x).setVisibility(View.VISIBLE);
            }
            for(int x=number;x<extendInclude.size();x++){
                extendInclude.get(x).setVisibility(View.INVISIBLE);
            }
            //piExtend.setLayoutParams(new ConstraintLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        /**
         * Change each extend view to the proper colors of generated colors
         * @param colors generated colors
         */
        private void changeExtendInclude(String[] colors){
            for(int x=0;x<colors.length;x++){
                extendView.get(x).setBackgroundColor(Color.parseColor(colors[x]));
                extendHex.get(x).setText(colors[x]);
                int[] rgbOfColor = ColorUtility.getRGBFromHex(colors[x]);
                String rgbText = rgbOfColor[0]+", "+rgbOfColor[1]+","+rgbOfColor[2];
                extendRGB.get(x).setText(rgbText);
            }
        }
    }

}