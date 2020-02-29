package com.vvdev.colorpicker.fragment.BottomBar;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.PaletteRVAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Palette extends Fragment {

    public static RecyclerView recyclerView;

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
        recyclerView=null;
    }

    private void setupPaletteRecycleView(View view){
        recyclerView = view.findViewById(R.id.pRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PaletteRVAdapter PaletteRVAdapter = new PaletteRVAdapter(getActivity());
        recyclerView.setAdapter(PaletteRVAdapter);
    }
}