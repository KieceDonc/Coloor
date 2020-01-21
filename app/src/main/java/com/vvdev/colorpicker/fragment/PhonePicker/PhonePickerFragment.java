package com.vvdev.colorpicker.fragment.PhonePicker;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.CirclePicker;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


public class PhonePickerFragment extends Fragment {

    private RelativeLayout PhonePickerRelativeView;
    private Button StartCirclePicker;
    private Button StopCirclePicker;
    private boolean CirclePickerAlreadyAdded = false;
    private View CirclePickerView;

    Rect PhonePickerRect = new Rect(); // use to save coordinates location relative to the screen/display of PhonePicker . We use this variable to disable user to move CirclePickerView out of screen

    private LayoutInflater mInflater = null;
    private ViewGroup mContainer = null;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        mInflater=inflater;
        mContainer = container;
        return inflater.inflate(R.layout.fragment_phonepicker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PhonePickerRelativeView = view.findViewById(R.id.PhonePickerRelativeView);
        StartCirclePicker = view.findViewById(R.id.StartCirclePicker);
        StopCirclePicker = view.findViewById(R.id.StopCirclePicker);

        StartCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CirclePickerAlreadyAdded){ // we use this to deny user to add too many view
                    CirclePickerAlreadyAdded=true;
                    CirclePickerView = mInflater.inflate(R.layout.circlepicker,mContainer,false);
                    PhonePickerRelativeView.addView(CirclePickerView);
                    PhonePickerRelativeView.bringChildToFront(CirclePickerView);// make view to first plan
                    final CirclePicker mCirclePicker= view.findViewById(R.id.CirclePicker);

                    mCirclePicker.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            PhonePickerRect = new Rect();
                            PhonePickerRelativeView.getGlobalVisibleRect(PhonePickerRect);
                            mCirclePicker.setMovableDimension(PhonePickerRect); // give dimension
                    }
                    });
                }
            }
        });
        StopCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CirclePickerAlreadyAdded){
                    CirclePickerAlreadyAdded=false;
                    PhonePickerRelativeView.removeView(CirclePickerView);

                }
            }
        });
    }
}
