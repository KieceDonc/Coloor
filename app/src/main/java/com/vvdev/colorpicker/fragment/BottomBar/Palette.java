package com.vvdev.colorpicker.fragment.BottomBar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorsData;
import com.vvdev.colorpicker.ui.PaletteRVAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Palette extends Fragment {

    public static RecyclerView recyclerView;
    public static FloatingActionMenu actionMenu;

    private FloatingActionButton actionButtonDeleteAll;

    private final static String TAG = Palette.class.getName();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_palette, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actionMenu = view.findViewById(R.id.ActionButtonMenu);
        actionButtonDeleteAll = view.findViewById(R.id.ActionButtonDeleteAll);
        setupPaletteRecycleView(view);
        setupActionButtonListener();
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    actionMenu.hideMenuButton(true);
                } else if (dy < 0) {
                    actionMenu.showMenuButton(true);
                }
            }
        });
    }

    private void setupActionButtonListener(){
        actionButtonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"delete all colors floating button have been clicked");
                new ColorsData(getActivity()).clearColors();
            }
        });
    }
}