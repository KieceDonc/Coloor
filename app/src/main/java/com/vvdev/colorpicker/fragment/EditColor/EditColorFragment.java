package com.vvdev.colorpicker.fragment.EditColor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import com.vvdev.colorpicker.R;

public class EditColorFragment extends Fragment {

    private EditColorViewModel editColorViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        editColorViewModel =
                ViewModelProviders.of(this).get(EditColorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_editcolor, container, false);
        final TextView textView = root.findViewById(R.id.text_editcolor);
        editColorViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}