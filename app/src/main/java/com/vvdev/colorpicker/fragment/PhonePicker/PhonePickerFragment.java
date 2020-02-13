package com.vvdev.colorpicker.fragment.PhonePicker;

import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.CirclePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;


public class PhonePickerFragment extends Fragment {

    private RelativeLayout PhonePickerRelativeView;
    private Button StartCirclePicker;
    private Button StopCirclePicker;
    private boolean CirclePickerAlreadyAdded = false;
    private CirclePicker CirclePickerView;

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

                    WindowManager wm = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE); // TODO ask permission to draw over other app
                    int LAYOUT_FLAG;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    } else {
                        LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                    }
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            LAYOUT_FLAG,
                            4564564,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;

                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View myView = inflater.inflate(R.layout.circlepicker, null);
                    /*CirclePickerView.getLayoutParams().width=(int) convertDpToPx(getContext(),220);
                    CirclePickerView.getLayoutParams().height=(int) convertDpToPx(getContext(),220);*/
                    //CirclePickerView = myView.findViewById(R.id.startCirclePicker);
                    wm.addView(myView,params);
                    CirclePickerView = myView.findViewById(R.id.CirclePicker);
                    /*CirclePickerView.getLayoutParams().width=(int) convertDpToPx(getContext(),220);
                    CirclePickerView.getLayoutParams().height=(int) convertDpToPx(getContext(),220);
                    /*CirclePickerView.setLayoutParams(new WindowManager.LayoutParams(
                            (int) convertDpToPx(getContext(),220),
                            (int) convertDpToPx(getContext(),220),
                            LAYOUT_FLAG,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                    |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                    |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                            PixelFormat.TRANSLUCENT));*/
                }
            }
        });
        StopCirclePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CirclePickerAlreadyAdded){
                    CirclePickerAlreadyAdded=false;
                    CirclePickerView.updatePhoneBitmap();
                }
            }
        });
    }

}
