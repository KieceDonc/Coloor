package com.vvdev.colorpicker.fragment.Palette;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorSpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class EditColorFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        return inflater.inflate(R.layout.fragment_palette, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Button[] c = new Button[6];
        c[0] = getView().findViewById(R.id.color0);
        c[1] = getView().findViewById(R.id.color1);
        c[2] = getView().findViewById(R.id.color2);
        c[3] = getView().findViewById(R.id.color3);
        c[4] = getView().findViewById(R.id.color4);
        c[5] = getView().findViewById(R.id.color5);

        ColorSpec blue = new ColorSpec("#26c1c8");

        for(int x =0;x<c.length;x++){
            c[x].setBackgroundColor(Color.parseColor(blue.getShades()[x]));
        }
        Log.e("test",blue.toString());
    }
}