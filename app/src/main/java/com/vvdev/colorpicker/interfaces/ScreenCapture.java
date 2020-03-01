package com.vvdev.colorpicker.interfaces;

import android.content.Intent;
import android.graphics.Bitmap;
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
    private static MediaProjection mMediaProjection;

    private Bitmap mBitmap;

    private OnCaptureListener mCaptureListener = null;


    public interface OnCaptureListener {
        void onScreenCaptureSuccess(Bitmap bitmap);

        void onScreenCaptureFailed(String errorMsg);
    }

    public void setCaptureListener(OnCaptureListener captureListener) {
        this.mCaptureListener = captureListener;
    }

    public ScreenCapture(int mWindowHeight,int mWindowWidth,int mScreenDensity) {

        this.mWindowHeight=mWindowHeight;
        this.mWindowWidth=mWindowWidth;
        this.mScreenDensity=mScreenDensity;
        createEnvironment();
    }

    private void createEnvironment() {
        mImageReader = ImageReader.newInstance(mWindowWidth, mWindowHeight, 0x1, 2);
    }

    public void screenCapture() {
        if (startScreenCapture()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "start startCapture");
                    startCapture();
                }
            }, 100);
        }
    }

    private int cmptAntiLoop=0;
    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            Log.e(TAG, "image is null.");
            if(cmptAntiLoop<10){ // TODO check if it's fix something
                cmptAntiLoop++;
                screenCapture();
            }else{
                Log.e(TAG,"cmptAntiLoop = 10");
            }
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
            Log.d(TAG, "bitmap create success");
            saveToFile();
        } else {
            Log.d(TAG, "bitmap is null");
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

    private boolean startScreenCapture() {
        Log.d(TAG, "startScreenCapture");
        if (mMediaProjection != null) {
            setUpVirtualDisplay();
            return true;
        } else {
            Log.d(TAG, "Error at startScreenCapture() in ScreenCapture");
            return false;
        }
    }

    private void setUpVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mWindowWidth, mWindowHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    public static void setUpMediaProjection(int mResultCode,Intent mResultData) {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    private void saveToFile() {
            if (mCaptureListener != null) {
                mCaptureListener.onScreenCaptureSuccess(mBitmap);
            }
    }
}