package com.vvdev.colorpicker.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

public class PDFEditText extends androidx.appcompat.widget.AppCompatEditText {

    private EditTextOnBackPress listenerBackPress;

    public interface EditTextOnBackPress {
        void editTextOnBackPress();
    }

    public PDFEditText(Context context) {
        super(context);
    }

    public PDFEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PDFEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            listenerBackPress.editTextOnBackPress();
        }
        return super.onKeyPreIme(keyCode,event);
    }

    public void setEditTextOnBackPressListener(EditTextOnBackPress listenerBackPress){
        this.listenerBackPress=listenerBackPress;
    }
}
