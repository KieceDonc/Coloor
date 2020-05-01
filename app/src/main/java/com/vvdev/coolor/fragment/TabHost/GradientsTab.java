package com.vvdev.coolor.fragment.TabHost;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.vvdev.coolor.R;
import com.vvdev.coolor.databinding.FragmentColorTabBinding;
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

    private FloatingActionButton actionButtonDeleteAll;
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
        FragmentGradientsBinding binding = FragmentGradientsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        actionButtonDeleteAll = binding.GradientsABButtonDeleteAll;
        actionButtonAddFromColor = binding.GradientsABButtonAddFromColor;

        recyclerView = binding.gradientsRV;
        tuto = (ConstraintLayout)binding.gradientsTuto.getRoot();

        if(Gradients.getInstance(getActivity()).getAllCustom().size()>0){
            setupGradientsRecycleView();
        }

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

                pickFromWheel.show();
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
