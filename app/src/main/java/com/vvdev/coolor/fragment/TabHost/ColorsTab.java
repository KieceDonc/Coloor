package com.vvdev.coolor.fragment.TabHost;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.vvdev.coolor.R;
import com.vvdev.coolor.activity.MainActivity;
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

    private FloatingActionButton actionButtonCirclePicker;
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

        View view = inflater.inflate(R.layout.fragment_color_tab, container, false);
        actionButtonCirclePicker = view.findViewById(R.id.ColorTabABCirclePicker);
        actionMenu = view.findViewById(R.id.PaletteABMenu);
        actionButtonPickFromWheel = view.findViewById(R.id.ColorTabABPickFromWheel);
        actionButtonDeleteAll = view.findViewById(R.id.ColorTabABButtonDeleteAll);
        actionButtonAddColor = view.findViewById(R.id.ColorTabABButtonAdd);
        tutorial = view.findViewById(R.id.ColorTabTuto);
        recyclerView = view.findViewById(R.id.pRecyclerView);

        setupPaletteRecycleView();
        setupActionButtonListener();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        SavedData temp = SavedData.getInstance(getActivity());
        if(temp.getColorsSize()>0){
            showColors();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Instance.set(this);
    }

    private void setupActionButtonListener(){

        actionButtonCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.startCirclePickerService(getContext());
            }
        });
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

    private void doFragmentTransaction(Fragment fragment){
        //switching fragment
        if (fragment != null) {
            String backStateName = fragment.getClass().getName();

            FragmentManager manager = getActivity().getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0); //https://stackoverflow.com/questions/18305945/how-to-resume-fragment-from-backstack-if-exists

            if (!fragmentPopped){ //fragment not in back stack, create it.
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(backStateName)
                        .replace(R.id.nav_host_fragment, fragment)
                        .commit();
            }
        }
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