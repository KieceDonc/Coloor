package com.vvdev.colorpicker.ui.PhonePicker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class PhonePickerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PhonePickerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Phone picker fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
