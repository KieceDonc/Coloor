package com.vvdev.colorpicker.fragment.BottomBar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.activity.MainActivity;
import com.vvdev.colorpicker.interfaces.SavedData;
import com.vvdev.colorpicker.ui.alertdialog.ColorAddDialog;
import com.vvdev.colorpicker.ui.alertdialog.ColorPickFromWheelDialog;
import com.vvdev.colorpicker.ui.adapter.PaletteRVAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Palette extends Fragment {

    private final static String TAG = Palette.class.getName();

    private FloatingActionButton actionButtonPickFromWheel;
    private FloatingActionButton actionButtonDeleteAll;
    private FloatingActionButton actionButtonAddColor;

    private ConstraintLayout tutorial;
    private RecyclerView recyclerView;
    private PaletteRVAdapter paletteRVAdapter;
    private FloatingActionMenu actionMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.Instance.setPaletteInstance(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_palette, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actionMenu = view.findViewById(R.id.ActionButtonMenu);
        actionButtonPickFromWheel = view.findViewById(R.id.ActionButtonPickFromWheel);
        actionButtonDeleteAll = view.findViewById(R.id.ActionButtonDeleteAll);
        actionButtonAddColor = view.findViewById(R.id.ActionButtonAdd);
        tutorial = view.findViewById(R.id.PaletteTuto);
        recyclerView = view.findViewById(R.id.pRecyclerView);

        setupPaletteRecycleView();
        setupActionButtonListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        SavedData temp = new SavedData(getActivity());
        if(temp.getColorsSize()>0){
            showColors();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.Instance.setPaletteInstance(null);
    }

    private void setupPaletteRecycleView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        paletteRVAdapter = new PaletteRVAdapter(getActivity());

        recyclerView.setAdapter(paletteRVAdapter);
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
        actionButtonPickFromWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Action button pick from wheel clicked");
                ColorPickFromWheelDialog cpfwd = new ColorPickFromWheelDialog(getActivity());
                cpfwd.show();
            }
        });

        actionButtonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"delete all colors floating button have been clicked");
                new SavedData(getActivity()).clearColors();
            }
        });

        actionButtonAddColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Action button add color clicked");
                ColorAddDialog cad = new ColorAddDialog(getActivity());
                cad.show();
            }
        });
    }

    public void showTutorial(){
        tutorial.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void showColors(){
        tutorial.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public RecyclerView getRecycleView(){
        return this.recyclerView;
    }

    public FloatingActionMenu getActionMenu(){
        return actionMenu;
    }

    public PaletteRVAdapter getPaletteRVAdapter(){
        return this.paletteRVAdapter;
    }

    public void setPaletteRVAdapter(PaletteRVAdapter rvAdapter){
        this.paletteRVAdapter = paletteRVAdapter;
    }
}