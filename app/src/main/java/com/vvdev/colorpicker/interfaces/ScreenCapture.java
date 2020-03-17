package com.vvdev.colorpicker.interfaces;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.util.Log;

import java.nio.ByteBuffer;

public class ScreenCapture{ // https://blog.csdn.net/qq_36332133/article/details/96485285

    private static String TAG = ScreenCapture.class.getName();

    private int mWindowWidth;
    private int mWindowHeight;
    private int mScreenDensity;

    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;

    public static MediaProjectionManager mMediaProjectionManager;
    private static int resultCode;
    private static Intent resultData;

    private MediaProjection mMediaProjection;

    private Bitmap mBitmap;

    private OnCaptureListener mCaptureListener = null;


    public interface OnCaptureListener {
        void onScreenCaptureSuccess(Bitmap bitmap);

        void onScreenCaptureFailed(String errorMsg);
    }

    public void setCaptureListener(OnCaptureListener captureListener) {
        this.mCaptureListener = captureListener;
    }

    private Context context;
    public ScreenCapture(int mWindowHeight, int mWindowWidth, int mScreenDensity, Context context) {

        this.mWindowHeight=mWindowHeight;
        this.mWindowWidth=mWindowWidth;
        this.mScreenDensity=mScreenDensity;
        createEnvironment();
        mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, resultData);
        this.context=context;
    }

    private void createEnvironment() {
        mImageReader = ImageReader.newInstance(mWindowWidth, mWindowHeight, PixelFormat.RGBA_8888, 2);
    }

    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            Log.e(TAG, "Image is null.");
            startScreenCapture();
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        mBitmap.copyPixelsFromBuffer(buffer);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height);
        image.close();

        stopScreenCapture();
        if (mBitmap != null) {
            Log.i(TAG, "Bitmap create success");
            mCaptureListener.onScreenCaptureSuccess(mBitmap);
        } else {
            Log.i(TAG, "Bitmap is null");
            if (mCaptureListener != null) {
                mCaptureListener.onScreenCaptureFailed("Get bitmap failed.");
            }
        }
    }

    private void stopScreenCapture() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
    }

    public boolean startScreenCapture() {
        Log.i(TAG, "Start screen capture");
        if (mMediaProjection != null) {
            setUpVirtualDisplay();
            return true;
        } else {
            Log.i(TAG, "Error at startScreenCapture() in ScreenCapture");
            return false;
        }
    }

    private void setUpVirtualDisplay() {
        try{
            mVirtualDisplay = mMediaProjection
                    .createVirtualDisplay("ScreenCapture",
                            mWindowWidth, mWindowHeight, mScreenDensity,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                            mImageReader.getSurface(),null, null);

            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    startCapture();
                }
            },null);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static void setUpMediaProjection(int mResultCode,Intent mResultData) {
        resultCode = mResultCode;
        resultData = mResultData;
    }
}