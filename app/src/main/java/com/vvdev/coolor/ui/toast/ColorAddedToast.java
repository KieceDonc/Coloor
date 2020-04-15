package com.vvdev.coolor.ui.toast;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.vvdev.coolor.R;

import androidx.cardview.widget.CardView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ColorAddedToast { // used to create custom layout of toast

    public static void show(Activity activity, LayoutInflater inflater, String colorAdded){
        View cstmToast = inflater.inflate(R.layout.added_color_toast, (CardView) activity.findViewById(R.id.cstmToastRoot));

        CardView root = cstmToast.findViewById(R.id.cstmToastRoot);

        CircleImageView colorPreview = cstmToast.findViewById(R.id.cstmToastPreview);
        Bitmap colorToShow = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(colorToShow);
        canvas.drawColor(Color.parseColor(colorAdded));
        colorPreview.setImageBitmap(colorToShow);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(cstmToast);
        toast.show();
    }
}
