package com.vvdev.colorpicker.ui.PhonePicker;

import android.os.Bundle;
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

public class PhonePickerFragment extends Fragment {

    private PhonePickerViewModel phonePickerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        phonePickerViewModel =
                ViewModelProviders.of(this).get(PhonePickerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_phonepicker, container, false);
        final TextView textView = root.findViewById(R.id.text_phonepicker);
        phonePickerViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
