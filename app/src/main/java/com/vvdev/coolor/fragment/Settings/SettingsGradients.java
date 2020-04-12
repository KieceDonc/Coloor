package com.vvdev.coolor.fragment.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.Gradients;
import com.vvdev.coolor.ui.adapter.GradientsRVAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SettingsGradients extends Fragment {

    private RecyclerView recyclerView;
    private GradientsRVAdapter gradientsRVAdapter;
    private ConstraintLayout tuto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_gradients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.sttingsGradientsRV);
        tuto = view.findViewById(R.id.sttingsGradientsTuto);

        if(Gradients.getInstance(getActivity()).getAllCustomGradients().size()>0){
            setupGradientsRecycleView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupGradientsRecycleView(){
        tuto.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gradientsRVAdapter = new GradientsRVAdapter(getActivity(), recyclerView, new GradientsRVAdapter.setOnGradientDeleted() {
            @Override
            public void onGradientDeleted() {
                if(Gradients.getInstance(getActivity()).getAllCustomGradients().size()==0){
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

}
