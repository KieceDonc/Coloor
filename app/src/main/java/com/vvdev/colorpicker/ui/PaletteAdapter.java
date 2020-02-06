package com.vvdev.colorpicker.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorSpec;
import com.vvdev.colorpicker.interfaces.ColorUtility;
import com.vvdev.colorpicker.interfaces.ColorsData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import androidx.recyclerview.widget.RecyclerView;

public class PaletteAdapter extends RecyclerView.Adapter<PaletteAdapter.MyViewHolder> {

    private ArrayList<ColorSpec> colors;

    public PaletteAdapter(ArrayList<ColorSpec> colors){
        this.colors=colors;
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

        private ColorSpec colorSpec;
        private CircleImageView colorPreview;
        private TextView colorName;
        private TextView hsv;
        private TextView rgb;
        private TextView hexa;
        private TextView more;
        private View generate0;
        private View generate1;
        private View generate2;
        private View generate3;
        private View generate4;
        private View generate5;

        public MyViewHolder(final View itemView) {
            super(itemView);

            colorPreview = itemView.findViewById(R.id.piColorPreview);
            colorName = itemView.findViewById(R.id.piColorName);
            hsv = itemView.findViewById(R.id.piHSV);
            rgb = itemView.findViewById(R.id.piRGB);
            hexa = itemView.findViewById(R.id.piHex);
            more = itemView.findViewById(R.id.piMore);
            generate0 = itemView.findViewById(R.id.piGenerate0);
            generate1 = itemView.findViewById(R.id.piGenerate1);
            generate2 = itemView.findViewById(R.id.piGenerate2);
            generate3 = itemView.findViewById(R.id.piGenerate3);
            generate4 = itemView.findViewById(R.id.piGenerate4);
            generate5 = itemView.findViewById(R.id.piGenerate5);

        }

        public void display(ColorSpec colorSpec) {
            this.colorSpec=colorSpec;

            String hexaFromColorSpec = colorSpec.getHexa();
            int[] hsvFromColorSpec = colorSpec.getHSV();
            int[] rgbFromColorSpec = colorSpec.getRGB();

            Bitmap bitmapOfPreview = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888);
            bitmapOfPreview.eraseColor(Color.parseColor(hexaFromColorSpec));
            colorPreview.setImageBitmap(bitmapOfPreview);
            colorName.setText(ColorUtility.nearestColor(hexaFromColorSpec)[0]);
            String toHSV = "HSV : "+hsvFromColorSpec[0]+", "+hsvFromColorSpec[1]+", "+hsvFromColorSpec[2];
            hsv.setText(toHSV);
            String toRGB = "RGB : "+rgbFromColorSpec[0]+", "+rgbFromColorSpec[1]+", "+rgbFromColorSpec[2];
            rgb.setText(toRGB);
            String toHexa = "Hexa : "+hexaFromColorSpec;
            hexa.setText(toHexa);

            String[] shades = colorSpec.getShades();
            generate0.setBackgroundColor(Color.parseColor(shades[0]));
            generate1.setBackgroundColor(Color.parseColor(shades[1]));
            generate2.setBackgroundColor(Color.parseColor(shades[2]));
            generate3.setBackgroundColor(Color.parseColor(shades[3]));
            generate4.setBackgroundColor(Color.parseColor(shades[4]));
            generate5.setBackgroundColor(Color.parseColor(shades[5]));
        }
    }

}