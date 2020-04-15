package com.vvdev.coolor.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.ColorSpec;
import com.vvdev.coolor.interfaces.Gradients;
import com.vvdev.coolor.interfaces.SavedData;
import com.vvdev.coolor.ui.adapter.GradientsRVAdapter;
import com.vvdev.coolor.ui.alertdialog.ColorPickFromWheelDialog;
import com.vvdev.coolor.ui.alertdialog.CreateGradientDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GradientsUserManager extends Fragment {

    private FloatingActionButton actionButtonDeleteAll;
    private FloatingActionButton actionButtonAddPremium;
    private FloatingActionButton actionButtonAddFromColor;

    private RecyclerView recyclerView;
    private GradientsRVAdapter gradientsRVAdapter;
    private ConstraintLayout tuto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gradients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actionButtonDeleteAll = view.findViewById(R.id.GradientsABButtonDeleteAll);
        actionButtonAddPremium = view.findViewById(R.id.GradientsABAddFromPremium);
        actionButtonAddFromColor = view.findViewById(R.id.GradientsABButtonAddFromColor);

        recyclerView = view.findViewById(R.id.gradientsRV);
        tuto = view.findViewById(R.id.gradientsTuto);

        if(Gradients.getInstance(getActivity()).getAllCustom().size()>0){
            setupGradientsRecycleView();
        }

        setupActionButtonListener();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupActionButtonListener(){
        actionButtonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gradients.getInstance(getActivity()).removeAll();
                setupTuto();
            }
        });

        actionButtonAddFromColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorSpec[] colorChosenHexValue = {null};

                // Your first calling this Dialog this a listener to save the color chosen
                ColorPickFromWheelDialog colorPickFromWheelDialog = new ColorPickFromWheelDialog(getActivity(), new ColorPickFromWheelDialog.setOnColorChoose() {
                    @Override
                    public void onColorChoose(ColorSpec colorChosen) {
                        colorChosenHexValue[0] = colorChosen;
                    }
                });

                // When the first dialog is gone your showing CreateGradientDialog
                colorPickFromWheelDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if(colorChosenHexValue[0]!=null){
                            CreateGradientDialog createGradientDialog = new CreateGradientDialog(getActivity(), colorChosenHexValue[0], new CreateGradientDialog.setOnGradientSaved() {
                                @Override
                                public void onGradientSaved() {
                                    setupGradientsRecycleView();
                                }
                            });
                            createGradientDialog.show();
                        }
                    }
                });

                colorPickFromWheelDialog.show();
            }
        });
    }

    private void setupGradientsRecycleView(){
        tuto.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gradientsRVAdapter = new GradientsRVAdapter(getActivity(), recyclerView, new GradientsRVAdapter.setOnGradientDeleted() {
            @Override
            public void onGradientDeleted() {
                if(Gradients.getInstance(getActivity()).getAllCustom().size()==0){
                    setupTuto();
                }
            }
        });
        recyclerView.setAdapter(gradientsRVAdapter);
    }

    private void setupTuto(){
        tuto.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public RecyclerView getRecycleView(){
        return recyclerView;
    }

}
