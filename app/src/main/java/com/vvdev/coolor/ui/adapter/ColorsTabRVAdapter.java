package com.vvdev.coolor.ui.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vvdev.coolor.R;
import com.vvdev.coolor.databinding.FragmentColorTabItemrecycleBinding;
import com.vvdev.coolor.databinding.FragmentColorsTabItemrecycleExtendBinding;
import com.vvdev.coolor.fragment.TabHost.ColorsTab;
import com.vvdev.coolor.interfaces.ColorSpec;
import com.vvdev.coolor.interfaces.ColorUtility;
import com.vvdev.coolor.interfaces.Gradients;
import com.vvdev.coolor.interfaces.SavedData;
import com.vvdev.coolor.ui.alertdialog.ColorInfo;
import com.vvdev.coolor.ui.alertdialog.CreateGradientDialog;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import androidx.recyclerview.widget.RecyclerView;

public class ColorsTabRVAdapter extends RecyclerView.Adapter<ColorsTabRVAdapter.MyViewHolderPalette> {

    private static final String TAG = ColorsTabRVAdapter.class.getName();

    private final Activity activity;

    public ArrayList<MyViewHolderPalette> myViewHolderPaletteArrayList = new ArrayList<>();

    public ColorsTabRVAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return new SavedData(activity).getColorsSize();
    }

    @NonNull
    @Override
    public MyViewHolderPalette onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FragmentColorTabItemrecycleBinding binding = FragmentColorTabItemrecycleBinding.inflate(inflater, parent, false);
        return new MyViewHolderPalette(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolderPalette holder, int position) {
        ColorSpec currentColorSpec = new SavedData(activity).getColors().get(position);
        holder.display(currentColorSpec);
    }

    public void updateSpinner(){
        for(int x=0;x<myViewHolderPaletteArrayList.size();x++){
            myViewHolderPaletteArrayList.get(x).updateSpinners();
        }
    }

    public class MyViewHolderPalette extends RecyclerView.ViewHolder {

        private ColorSpec currentColor;

        private CircleImageView colorPreview;
        private ImageView trash;
        private TextView colorName;
//        private TextView hsv;
        private TextView rgb;
        private TextView hexa;
        private TextView more;
        private TextView createGradient;
        private ConstraintLayout piExtend;
        private TextView moreInformation;

        private ArrayList<View> generate = new ArrayList<>();

        private Spinner extendSpinner;
        private ArrayList<FragmentColorsTabItemrecycleExtendBinding> extendInclude = new ArrayList<com.vvdev.coolor.databinding.FragmentColorsTabItemrecycleExtendBinding>();

        private String  spinnerCurrentName="";

        private boolean itemDeleted = false; // used to prevent bug. User can spam click the trash button and it delete multiple colors in colors data.

        public MyViewHolderPalette(final FragmentColorTabItemrecycleBinding binding) { //
            super(binding.getRoot());

            myViewHolderPaletteArrayList.add(this);

            colorPreview = binding.piColorPreview;

            colorName = binding.piColorName;
            rgb = binding.piRGB;
            hexa = binding.piHex;
            more = binding.piMore;
            trash = binding.piTrash;
            moreInformation = binding.piExtendMoreInformation;
            createGradient = binding.piExtendMoreCreateGradient;

            generate.add(binding.piGenerate0);
            generate.add(binding.piGenerate1);
            generate.add(binding.piGenerate2);
            generate.add(binding.piGenerate3);
            generate.add(binding.piGenerate4);
            generate.add(binding.piGenerate5);

            piExtend = binding.piExtend;

            extendSpinner = binding.piSpinner;
            extendInclude.add(binding.piExtendGenerate0);
            extendInclude.add(binding.piExtendGenerate1);
            extendInclude.add(binding.piExtendGenerate2);
            extendInclude.add(binding.piExtendGenerate3);
            extendInclude.add(binding.piExtendGenerate4);
            extendInclude.add(binding.piExtendGenerate5);

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (piExtend.getVisibility() == View.VISIBLE) {
                        piExtend.setVisibility(View.GONE);
                        more.setRotation(0);
                    } else {
                        piExtend.setVisibility(View.VISIBLE);
                        more.setRotation(-90);
                    }
                }
            });


            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "trash imageView clicked");
                    if (!itemDeleted) {
                        itemDeleted = true;
                        Log.i(TAG, "color selected isn't deleted, start to delete");
                        SavedData savedData = new SavedData(activity);
                        int position = getLayoutPosition();
                        savedData.removeColor(position);
                        ColorsTab.Instance.get().getActionMenu().showMenuButton(true);
                        itemDeleted=false;
                    }
                }
            });


            // set the extend spinner on item click listener and change each extend include to the colors propriety of selected item
            extendSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    spinnerCurrentName = Gradients.getInstance(activity).getAllName().get(position);
                    String[] colorsOfItem = currentColor.getAllGeneratedColors().get(position);
                    showNumberExtendInclude(colorsOfItem.length);
                    changeExtendInclude(colorsOfItem);
                    updateListener(colorsOfItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });

            moreInformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"TextView more information clicked");
                    ColorInfo cid = new ColorInfo(activity,currentColor);
                    cid.show();
                }
            });

            createGradient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreateGradientDialog cgd = new CreateGradientDialog(activity,currentColor);
                    cgd.show();
                }
            });

            // show the alert dialog which give more information about the color coding
            colorPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ColorInfo cid = new ColorInfo(activity,currentColor);
                    cid.show();
                }
            });

            addToClipBoardOnClick(rgb,5);
            addToClipBoardOnClick(hexa,5);
        }

        public void display(ColorSpec colorSpec) {
            currentColor = colorSpec;

            // get hexa, hsv, rgb of color
            String hexaFromColorSpec = colorSpec.getHexa();
            int[] rgbFromColorSpec = colorSpec.getRGB();

            // setup preview of color
            Bitmap bitmapOfPreview = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888);
            bitmapOfPreview.eraseColor(Color.parseColor(hexaFromColorSpec));
            colorPreview.setImageBitmap(bitmapOfPreview);

            // setup text
            colorName.setText(ColorUtility.nearestColor(hexaFromColorSpec)[0]);
            String toRGB = "RGB : " + rgbFromColorSpec[0] + ", " + rgbFromColorSpec[1] + ", " + rgbFromColorSpec[2];
            rgb.setText(toRGB);
            String toHexa = "HEX : " + hexaFromColorSpec;
            hexa.setText(toHexa);

            // setup extend spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, Gradients.getInstance(activity).getAllName());
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            extendSpinner.setAdapter(spinnerAdapter);

            showNumberExtendInclude(6); // set the good number of include for generated colors method shades
            String[] toShow; // setup preview generated colors by the method of generation shades
            if (ColorUtility.isNearestFromBlackThanWhite(colorSpec.getHexa())) { // check if the color is closer to black than white
                extendSpinner.setSelection(2); // set spinner to position of Tints ( it will also set extendInclude to Tints mode )
                spinnerCurrentName = Gradients.getInstance(activity).getAllName().get(2);
                toShow = colorSpec.getTints(); // setup preview generated colors by the method of generation Tints
            } else {
                extendSpinner.setSelection(0); // set spinner to position of Shades ( it will also set extendInclude to Shades mode )
                spinnerCurrentName = Gradients.getInstance(activity).getAllName().get(0);
                toShow = colorSpec.getShades(); // setup preview generated colors by the method of generation Shades
            }
            for (int x = 0; x < generate.size(); x++) { // setup preview generated colors by the method of generation ( Shades / Tints )
                generate.get(x).setBackgroundColor(Color.parseColor(toShow[x]));
            }
        }

        /**
         * Change the visibility of extend include depending of generated colors length
         *
         * @param number generated colors length
         */
        private void showNumberExtendInclude(int number) {
            for (int x = 0; x < number; x++) {
                extendInclude.get(x).getRoot().setVisibility(View.VISIBLE);
            }
            for (int x = number; x < extendInclude.size(); x++) {
                extendInclude.get(x).getRoot().setVisibility(View.INVISIBLE);
            }
        }

        /**
         * Change each extend view to the proper colors of generated colors
         *
         * @param colors generated colors
         */
        private void changeExtendInclude(String[] colors) {
            for (int x = 0; x < colors.length; x++) {
                extendInclude.get(x).piExtendView.setBackgroundColor(Color.parseColor(colors[x]));
                extendInclude.get(x).piExtendHex.setText(colors[x]);
                int[] rgbOfColor = ColorUtility.getRGBFromHex(colors[x]);
                String rgbText = rgbOfColor[0] + ", " + rgbOfColor[1] + "," + rgbOfColor[2];
                extendInclude.get(x).piExtendRGB.setText(rgbText);
            }
        }

        /**
         * used to set extend item on listener or not
         *
         * @param colors
         */
        private void updateListener(final String[] colors) {
            for (int x = 0; x < extendInclude.size(); x++) {

                if (extendInclude.get(x).getRoot().getVisibility() == View.VISIBLE) {
                    final String currentColor = colors[x];
                    extendInclude.get(x).getRoot().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new SavedData(activity).addColor(currentColor);
                        }
                    });
                } else {
                    extendInclude.get(x).getRoot().setOnClickListener(null);
                }
            }
        }

        private void addToClipBoardOnClick(final TextView tv, final int colorCodeStartAt){
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textColorCoding = tv.getText().toString();
                    addStringToClipBoard(textColorCoding.substring(colorCodeStartAt));
                    String copiedToClipBoard = activity.getString(R.string.alertdialog_colorInfo_CopyToClipboard);
                    Toast.makeText(activity,copiedToClipBoard,Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void addStringToClipBoard(String toAdd){
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", toAdd);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
            }
        }

        public void updateSpinners(){
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, Gradients.getInstance(activity).getAllName());
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            extendSpinner.setAdapter(spinnerAdapter);
            // we try to set the old gradient selected by the user cuz by updating the adapter it reset the selected gradient
            int cmpt=0;
            boolean founded=false;
            do{
                if(spinnerCurrentName.equals(spinnerAdapter.getItem(cmpt))){
                    founded=true;
                    extendSpinner.setSelection(cmpt);
                }
                cmpt++;
            }while(cmpt<spinnerAdapter.getCount()&&!founded);
        }
    }
}