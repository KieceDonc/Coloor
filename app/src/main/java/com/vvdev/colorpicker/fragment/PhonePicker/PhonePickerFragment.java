package com.vvdev.colorpicker.fragment.PhonePicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.CirclePicker;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


public class PhonePickerFragment extends Fragment {

    private ConstraintLayout PhonePickerConstraint;
    private Button StartCirclePicker;
    private Button StopCirclePicker;
    private Button InvalidateCirclePicker;
    private boolean CirclePickerAlreadyAdded = false;
    private View AddCirclePickerView;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_phonepicker, container, false);
        PhonePickerConstraint = root.findViewById(R.id.PhonePickerConstraint);
        StartCirclePicker = root.findViewById(R.id.StartCirclePicker);
        StopCirclePicker = root.findViewById(R.id.StopCirclePicker);
        InvalidateCirclePicker = root.findViewById(R.id.InvalidateCirclePicker);
        StartCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CirclePickerAlreadyAdded){
                    CirclePickerAlreadyAdded=true;
                    AddCirclePickerView = inflater.inflate(R.layout.circlepicker,container,false);
                    PhonePickerConstraint.addView(AddCirclePickerView);
                }
            }
        });
        StopCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CirclePickerAlreadyAdded){
                    CirclePickerAlreadyAdded=false;
                    PhonePickerConstraint.removeView(AddCirclePickerView);

                }
            }
        });
        InvalidateCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CirclePicker CirclePicker= root.findViewById(R.id.CirclePicker);
                CirclePicker._Invalidate();
            }
        });
        return root;
    }
}
