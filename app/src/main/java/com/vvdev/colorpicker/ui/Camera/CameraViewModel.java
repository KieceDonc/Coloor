package com.vvdev.colorpicker.ui.Camera;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class CameraViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CameraViewModel() {
       /* mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");*/
    }

    public LiveData<String> getText() {
        return mText;
    }
}