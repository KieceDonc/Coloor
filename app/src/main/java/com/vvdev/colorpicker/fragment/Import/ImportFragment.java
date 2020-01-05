package com.vvdev.colorpicker.fragment.Import;

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

public class ImportFragment extends Fragment {

    //https://viewerjs.org/
    //https://www.leadtools.com/sdk/document/document-viewer-html5
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_import, container, false);
    }
}