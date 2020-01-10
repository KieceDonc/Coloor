package com.vvdev.colorpicker.fragment.PhonePicker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.CirclePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class PhonePickerFragment extends Fragment {

    private Button StartCirclePicker;

    View test;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_phonepicker, container, false);
        StartCirclePicker = root.findViewById(R.id.StartCirclePicker);
        StartCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test = inflater.inflate(R.layout.circlepicker,container,true);
            }
        });
        return root;
    }
}
