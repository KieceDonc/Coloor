package com.vvdev.coolor.fragment.TabHost;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.vvdev.coolor.R;
import com.vvdev.coolor.activity.MainActivity;
import com.vvdev.coolor.databinding.FragmentColorTabBinding;
import com.vvdev.coolor.interfaces.SavedData;
import com.vvdev.coolor.ui.alertdialog.AddFromHex;
import com.vvdev.coolor.ui.alertdialog.PickFromWheel;
import com.vvdev.coolor.ui.adapter.ColorsTabRVAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ColorsTab extends Fragment {

    private final static String TAG = ColorsTab.class.getName();

    private FloatingActionButton actionButtonPickFromWheel;
    private FloatingActionButton actionButtonDeleteAll;
    private FloatingActionButton actionButtonAddColor;

    private ConstraintLayout tutorial;
    private RecyclerView recyclerView;
    private ColorsTabRVAdapter colorsTabRVAdapter;
    private FloatingActionMenu actionMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance.set(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentColorTabBinding binding = FragmentColorTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //View view = inflater.inflate(R.layout.fragment_color_tab, container, false);
        actionMenu = binding.PaletteABMenu;
        actionButtonPickFromWheel = binding.ColorTabABPickFromWheel;
        actionButtonDeleteAll = binding.ColorTabABButtonDeleteAll;
        actionButtonAddColor = binding.ColorTabABButtonAdd;
        tutorial = binding.ColorTabTuto.getRoot().findViewById(R.id.ColorTabTuto);
        recyclerView = binding.pRecyclerView;

        setupPaletteRecycleView();
        setupActionButtonListener();

        SavedData temp = SavedData.getInstance(getActivity());
        if(temp.getColorsSize()>0){
            showColors();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Instance.set(this);
    }

    private void setupActionButtonListener(){
        actionButtonPickFromWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Action button pick from wheel clicked");
                PickFromWheel cpfwd = new PickFromWheel(getActivity());
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
                AddFromHex cad = new AddFromHex(getActivity());
                cad.show();
            }
        });
    }

    private void setupPaletteRecycleView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        colorsTabRVAdapter = new ColorsTabRVAdapter(getActivity());

        recyclerView.setAdapter(colorsTabRVAdapter);
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

    public ColorsTabRVAdapter getColorsTabRVAdapter(){
        return this.colorsTabRVAdapter;
    }

    public void setColorsTabRVAdapter(ColorsTabRVAdapter rvAdapter) {
        this.colorsTabRVAdapter = colorsTabRVAdapter;
    }

    private Drawable resizeToActionButton(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    public static class Instance{
        private static ColorsTab colorsTab_;

        public static void set(ColorsTab colorsTab){
            colorsTab_=colorsTab;
        }

        public static ColorsTab get(){
            return colorsTab_;
        }
    }
}