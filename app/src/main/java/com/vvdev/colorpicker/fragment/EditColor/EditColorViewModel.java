package com.vvdev.colorpicker.fragment.EditColor;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class EditColorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EditColorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}