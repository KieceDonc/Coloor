package com.vvdev.colorpicker.fragment.Palette;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorSpec;
import com.vvdev.colorpicker.interfaces.ColorsData;
import com.vvdev.colorpicker.ui.PaletteAdapter;
import com.vvdev.colorpicker.ui.PaletteSwipeController;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaletteFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        return inflater.inflate(R.layout.fragment_palette, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ColorsData colorsData = new ColorsData(getActivity());
        colorsData.addColor(new ColorSpec("#050505"));
        colorsData.addColor(new ColorSpec("#26c1c8"));
        colorsData.addColor(new ColorSpec("#a326c8"));
        colorsData.addColor(new ColorSpec("#c82683"));
        colorsData.addColor(new ColorSpec("#c3c826"));
        colorsData.addColor(new ColorSpec("#c82626"));
        colorsData.addColor(new ColorSpec("#26c89c"));
        colorsData.addColor(new ColorSpec("#5cc826"));
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.pRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new PaletteAdapter(colorsData.getColors(),getActivity()));

        PaletteSwipeController paletteSwipeController = new PaletteSwipeController();
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(paletteSwipeController);
        itemTouchhelper.attachToRecyclerView(rv);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        ColorsData colorsData = new ColorsData(Objects.requireNonNull(getActivity()));
        colorsData.clearColors();
        super.onDestroy();
    }
}