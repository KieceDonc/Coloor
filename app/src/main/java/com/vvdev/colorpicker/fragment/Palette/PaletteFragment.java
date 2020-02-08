package com.vvdev.colorpicker.fragment.Palette;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorSpec;
import com.vvdev.colorpicker.interfaces.ColorsData;
import com.vvdev.colorpicker.ui.PaletteAdapter;
import com.vvdev.colorpicker.ui.PaletteControllerActions;
import com.vvdev.colorpicker.ui.PaletteSwipeController;
import com.vvdev.colorpicker.ui.PaletteSwipeController.*;

import java.util.ArrayList;
import java.util.Arrays;
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
        super.onViewCreated(view, savedInstanceState);

        setupPaletteRecycleView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setupPaletteRecycleView(View view){
        final ColorsData colorsData = new ColorsData(getActivity());

        final RecyclerView rv = view.findViewById(R.id.pRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        PaletteAdapter PaletteAdapter = new PaletteAdapter(colorsData.getColors(),getActivity());
        rv.setAdapter(PaletteAdapter);


        final PaletteSwipeController paletteSwipeController = new PaletteSwipeController(new PaletteControllerActions(){
            @Override
            public void onRightClicked(int position) {
                rv.removeViewAt(position);
                colorsData.removeColor(position);
                PaletteAdapter PaletteAdapter = new PaletteAdapter(colorsData.getColors(),getActivity());
                rv.setAdapter(PaletteAdapter);
                PaletteAdapter.notifyDataSetChanged();
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(paletteSwipeController);
        itemTouchhelper.attachToRecyclerView(rv);

        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                paletteSwipeController.onDraw(c);
            }
        });
    }
}