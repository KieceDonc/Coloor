package com.vvdev.coolor.trash.Settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvdev.coolor.BuildConfig;
import com.vvdev.coolor.R;
import com.vvdev.coolor.fragment.TabHost.GradientsTab;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.fabric.sdk.android.services.common.CommonUtils;

public class SettingsMain extends Fragment {

    private View gradientsListener;
    private View supportListener;
    private View rateListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gradientsListener = view.findViewById(R.id.sttingGradientListener);
        gradientsListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFragmentTransaction(new GradientsTab());
            }
        });

        supportListener = view.findViewById(R.id.sttingContactListener);

        supportListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, getActivity().getResources().getString(R.string.support_email));
                intent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getResources().getString(R.string.settings_support_title));
                String emailContent = getActivity().getResources().getString(R.string.settings_support_emailcontent);
                emailContent+="\n"+userAnonymousInfo();
                intent.putExtra(Intent.EXTRA_TEXT,emailContent);

                startActivity(Intent.createChooser(intent, getActivity().getResources().getString(R.string.settings_support_intenttitle)));
            }
        });

        rateListener = view.findViewById(R.id.sttingRateListener);

        rateListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private String userAnonymousInfo(){
        String toReturn = "Android version : "+android.os.Build.VERSION.SDK_INT;
        toReturn+="\nManufacturer : "+ Build.MANUFACTURER;
        toReturn+="\nBuild : "+ Build.PRODUCT;
        toReturn+="\nDevice name : "+getDeviceName();
        toReturn+="\nRoot : "+ CommonUtils.isRooted(getContext());
        toReturn+="\nApp version : "+ BuildConfig.VERSION_NAME;
        toReturn+="\nCall date : "+new Date().toString();
        return toReturn;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    private void doFragmentTransaction(Fragment fragment){
        //switching fragment
        /*if (fragment != null) {
            String backStateName = fragment.getClass().getName();

            FragmentManager manager = getActivity().getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0); //https://stackoverflow.com/questions/18305945/how-to-resume-fragment-from-backstack-if-exists

            if (!fragmentPopped){ //fragment not in back stack, create it.
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(backStateName)
                        .replace(R.id.nav_host_fragment, fragment)
                        .commit();
            }
        }*/
    }
}
