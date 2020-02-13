package com.vvdev.colorpicker.interfaces;

import android.view.View;

public class CirclePickerViewHandler {

    private View viewOfCirclePicker;
    private boolean isDisplay = false;


    public View getViewOfCirclePicker() {
        return viewOfCirclePicker;
    }

    public void setViewOfCirclePicker(View viewOfCirclePicker) {
        this.viewOfCirclePicker = viewOfCirclePicker;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public void setDisplay(boolean display) {
        isDisplay = display;
    }
}
