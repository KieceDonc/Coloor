package com.vvdev.colorpicker.fragment.PhonePicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvdev.colorpicker.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class PhonePickerFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_phonepicker, container, false);
        return root;
    }
}
