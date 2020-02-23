package com.vvdev.colorpicker.interfaces;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

public class FilesExtensionType {

    public static String getFileExtension(@NonNull Context context,@NonNull Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
