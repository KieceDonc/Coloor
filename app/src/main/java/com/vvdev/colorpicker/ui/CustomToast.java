package com.vvdev.colorpicker.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vvdev.colorpicker.R;

import androidx.cardview.widget.CardView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomToast { // used to create custom layout of toast

    public static void show(Activity activity, LayoutInflater inflater, String colorAdded){
        View cstmToast = inflater.inflate(R.layout.custom_toast, (CardView) activity.findViewById(R.id.cstmToastRoot));

        CardView root = cstmToast.findViewById(R.id.cstmToastRoot);
        root.setCardBackgroundColor(Color.parseColor(colorAdded));

        CircleImageView colorPreview = cstmToast.findViewById(R.id.cstmToastPreview);
        Bitmap colorToShow = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(colorToShow);
        canvas.drawColor(Color.parseColor(colorAdded));
        colorPreview.setImageBitmap(colorToShow);

        TextView information = cstmToast.findViewById(R.id.cstmToastTextView);
        String haveBeenAdded = information.getText().toString();
        String toSet = colorAdded+" "+haveBeenAdded;
        information.setText(toSet);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(cstmToast);
        toast.show();
    }
}
