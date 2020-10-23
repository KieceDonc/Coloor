package com.vvdev.coolor.fragment.TabHost;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.vvdev.coolor.activity.MainActivity;
import com.vvdev.coolor.databinding.FragmentGradientsBinding;
import com.vvdev.coolor.interfaces.ColorSpec;
import com.vvdev.coolor.interfaces.Gradients;
import com.vvdev.coolor.ui.adapter.GradientsRVAdapter;
import com.vvdev.coolor.ui.alertdialog.PickFromWheel;
import com.vvdev.coolor.ui.alertdialog.CreateGradientDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GradientsTab extends Fragment {

    private FloatingActionMenu actionMenu;
    private FloatingActionButton actionButtonAddDefault;
    private FloatingActionButton actionButtonAddFromColor;
    private FloatingActionButton actionButtonDeleteAll;

    private RecyclerView recyclerView;
    private GradientsRVAdapter gradientsRVAdapter;
    private ConstraintLayout tuto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance.set(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentGradientsBinding binding = FragmentGradientsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        actionMenu = binding.GradientsABMenu;
        actionButtonAddDefault = binding.GradientsABButtonDefaultCustom;
        actionButtonAddFromColor = binding.GradientsABButtonAddFromColor;
        actionButtonDeleteAll = binding.GradientsABButtonDeleteAll;

        recyclerView = binding.gradientsRV;
        tuto = (ConstraintLayout) binding.gradientsTuto.getRoot();

        setupGradientsRecycleView();

        setupActionButtonListener();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Instance.set(null);
    }

    private void setupActionButtonListener(){

        actionButtonAddDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gradients gradients = Gradients.getInstance(MainActivity.Instance.get());
                int startPosition = gradients.size()-1;
                gradients.setupNativeCustomGrad();
                int endPosition = gradients.size()-1;
                getRecycleView().getAdapter().notifyItemRangeChanged(startPosition,endPosition); // used to update recycle view
            }
        });

        actionButtonAddFromColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorSpec[] colorChosenHexValue = {null};

                // Your first calling this Dialog this a listener to save the color chosen
                PickFromWheel pickFromWheel = new PickFromWheel(getActivity(), new PickFromWheel.setOnColorChoose() {
                    @Override
                    public void onColorChoose(ColorSpec colorChosen) {
                        colorChosenHexValue[0] = colorChosen;
                    }
                });

                // When the first dialog is gone your showing CreateGradientDialog
                pickFromWheel.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if(colorChosenHexValue[0]!=null){
                            CreateGradientDialog createGradientDialog = new CreateGradientDialog(getActivity(), colorChosenHexValue[0]);
                            createGradientDialog.show();
                        }
                    }
                });

                pickFromWheel.show();
            }
        });

        actionButtonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gradients.getInstance(getActivity()).removeAll();
                actionMenu.showMenuButton(true);
                showTuto();
            }
        });
    }

    public void setupGradientsRecycleView(){
        if(Gradients.getInstance(getActivity()).getAllCustom().size()>0){
            showRv();
        }else{
            showTuto();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gradientsRVAdapter = new GradientsRVAdapter(getActivity(), recyclerView, new GradientsRVAdapter.setOnGradientDeleted() {
            @Override
            public void onGradientDeleted() {
                if(Gradients.getInstance(getActivity()).getAllCustom().size()==0){
                    showTuto();
                }
                actionMenu.showMenuButton(true);
            }
        });
        recyclerView.setAdapter(gradientsRVAdapter);
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

    public void showTuto(){
        if(tuto.getVisibility()!=View.VISIBLE){
            tuto.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void showRv(){
        if(tuto.getVisibility()!=View.GONE){
            tuto.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public RecyclerView getRecycleView(){
        return recyclerView;
    }

    public static class Instance{
        private static GradientsTab gradientsTab_;

        public static void set(GradientsTab gradientsTab){
            gradientsTab_=gradientsTab;
        }

        public static GradientsTab get(){
            return gradientsTab_;
        }
    }

}
