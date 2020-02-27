package com.vvdev.colorpicker.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorSpec;
import com.vvdev.colorpicker.interfaces.ColorUtility;
import com.vvdev.colorpicker.interfaces.ColorsData;
import com.vvdev.colorpicker.interfaces.ScreenCapture;
import com.vvdev.colorpicker.services.CirclePickerService;

import static android.content.Context.WINDOW_SERVICE;
import static com.vvdev.colorpicker.activity.CirclePickerActivityStart.wmCirclePickerParams;
import static com.vvdev.colorpicker.activity.CirclePickerActivityStart.wmCirclePickerView;
import static com.vvdev.colorpicker.activity.MainActivity.appNavigationBarHeight;

@SuppressLint("AppCompatCustomView")
public class CirclePickerView extends ImageView {

    private static final String TAG ="CirclePicker";

    private ScreenCapture mScreenCapture;
    private ScreenCapture.OnCaptureListener mCaptureListener = new ScreenCapture.OnCaptureListener() {
        @Override
        public void onScreenCaptureSuccess(Bitmap bitmap) {

            mScreenBitmap = bitmap;
            setVisibility(VISIBLE);
            inUpdatePhoneBitmap=false;
            setupFinalBitmap();

        }

        @Override
        public void onScreenCaptureFailed(String errorMsg) {
            Log.d(TAG, "onScreenCaptureFailed errorMsg:" + errorMsg);
        }
    };
    private BitmapShader mBitmapShader;

    private Bitmap mFinalBitmap;
    private Bitmap mScreenBitmap;
    private Bitmap mBitmap;

    private Path mBorderCircle;

    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private Paint mBorderPaintText;
    private Paint mMiddleSquarePaint;

    private final Matrix mShaderMatrix = new Matrix();

    private RectF mDrawableRect;
    private RectF mBorderRect;

    private Rect mBorderColorNameRect = new Rect();
    private Rect mBorderColorHexRect = new Rect();

    private String mColorName="";
    private String mColorHexa="";

    private float mDrawableRadius;
    private float mBorderRadius;
    private float mPositionBorderColorName;
    private float mPositionBorderHex;

    private int mPhoneWidth;
    private int mPhoneHeight;
    private int mBitmapHeight;
    private int mBitmapWidth;
    private int mBorderWidthDp = 12;
    private int mBorderWidthPx = convertDpToPx(mBorderWidthDp);
    private int mBorderColor;
    private int mMiddleSquareDim;
    private int mMiddleBorderSquareDim;
    private int mStatusBarHeight;
    private int mNavigationBarHeight; // /!\ it include app navigation bar height

    private boolean mReady;
    private boolean mSetupPending=true;
    private boolean inUpdatePhoneBitmap;


    public CirclePickerView(Context context) {
        super(context);
        init();
    }

    public CirclePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mBitmap == null) {
            updatePhoneBitmap();
            return;
        }

        if(mBorderColor==Color.TRANSPARENT){
            setup();
        }


        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);
        canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint);
        /**
         * first part = we divide the circle in 8 part. we want the position at 7/8. 7/8 = 0.875 * circle perimeter.
         * After that the first part give the starting point but we want to center our text.
         * second part = we get width of text and we divide by 2 to get the offset to delete
         */

        mBorderPaintText.getTextBounds(mColorName,0,mColorName.length(),mBorderColorNameRect);
        mBorderPaintText.getTextBounds(mColorHexa,0,mColorHexa.length(),mBorderColorHexRect);

        mPositionBorderColorName = (float)((2*Math.PI*mBorderRadius*0.875)-mBorderColorNameRect.width()/2);
        mPositionBorderHex = (float)((2*Math.PI*mBorderRadius*0.615)- mBorderColorHexRect.width()/2);

        canvas.drawTextOnPath(mColorName, mBorderCircle, mPositionBorderColorName, mBorderWidthDp, mBorderPaintText); // CUSTOM
        canvas.drawTextOnPath(mColorHexa, mBorderCircle, mPositionBorderHex, mBorderWidthDp, mBorderPaintText); // CUSTOM

        canvas.drawRect(mDrawableRect.centerX()+mMiddleSquareDim,mDrawableRect.centerY()+mMiddleSquareDim,mDrawableRect.centerX()-mMiddleSquareDim,mDrawableRect.centerY()-mMiddleSquareDim,mMiddleSquarePaint);
    }

    private void init() {
        mReady=true;

        if (mSetupPending) {
            Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            mPhoneWidth = size.x;
            mPhoneHeight= size.y;

            mStatusBarHeight = getStatusBarHeight();
            mNavigationBarHeight = getNavigationBarHeight();

            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            mScreenCapture = new ScreenCapture(mPhoneHeight,mPhoneWidth,metrics.densityDpi);
            mScreenCapture.setCaptureListener(mCaptureListener);

            updatePhoneBitmap();
            setup();
            new CirclePickerView.UserInteractionHandler(getContext(), this);
            mSetupPending = false;
        }
    }

    private void setup(){
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }

        if (mBitmap == null) {
            updatePhoneBitmap();
            invalidate();
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect = new RectF();
        mBorderRect.set(calculateBounds());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidthPx) / 2.0f, (mBorderRect.width() - mBorderWidthPx) / 2.0f);

        mDrawableRect = new RectF();
        mDrawableRect.set(mBorderRect);
        mDrawableRect.inset(mBorderWidthPx - 1.0f, mBorderWidthPx - 1.0f);

        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);

        mMiddleSquarePaint=new Paint();
        if(scaleFactor>0.075) {
            mMiddleSquareDim = (int) ((1 / scaleFactor));
            mMiddleBorderSquareDim = (int) (0.5 * (1 / scaleFactor));
        }
        mMiddleSquarePaint.setStyle(Paint.Style.STROKE);
        mMiddleSquarePaint.setStrokeWidth(mMiddleBorderSquareDim);
        int pixel = mBitmap.getPixel(mBitmap.getWidth()/2,mBitmap.getHeight()/2);
        mMiddleSquarePaint.setColor(ColorUtility.pickTextColorBasedOnBackgroundColor(pixel));


        mBorderCircle = new Path();
        mBorderCircle.addCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, Path.Direction.CW);

        Bitmap allPixelInsideCenterSquare;
        int centerDesireWidth= (mMiddleSquareDim*2-mMiddleBorderSquareDim);
        int centerDesireHeight= (mMiddleSquareDim*2-mMiddleBorderSquareDim);
        if(scaleFactor==0.5){
            int centerDesireX= mBitmap.getWidth()/2+mMiddleSquareDim-mMiddleBorderSquareDim;
            int centerDesireY= mBitmap.getHeight()/2+mMiddleSquareDim-mMiddleBorderSquareDim;

            allPixelInsideCenterSquare =Bitmap.createBitmap(mBitmap, centerDesireX, centerDesireY,centerDesireWidth,centerDesireHeight);
            int[] RGB =ColorUtility.getRGBAverageFromBitmap(allPixelInsideCenterSquare);
            mColorHexa = ColorUtility.getHexFromRGB(RGB);
            mColorName=ColorUtility.nearestColor(mColorHexa)[0];
            mBorderColor = Color.parseColor(mColorHexa);
            updateNotificationHex();
        }

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidthPx);

        mBorderPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
        mBorderPaintText.setColor(ColorUtility.pickTextColorBasedOnBackgroundColor(mBorderColor));
        mBorderPaintText.setTextSize(mBorderWidthPx);
        //TODO put it in init()

        updateShaderMatrix();
        invalidate();
    }

    private RectF calculateBounds() {
        int availableWidth  = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    public int convertDpToPx(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    double scaleFactor = 0.5; // for scaleFactor = 1; if scaleFactor -> 0+ it zoom to pixel lvl

    /**
     * update phone bitmap
     */
    public void updatePhoneBitmap(){
        if(!inUpdatePhoneBitmap) {
            inUpdatePhoneBitmap=true;
            setVisibility(INVISIBLE); // set this view invisible so we won't get it in bitmap. It give a basis to work on.
            mScreenCapture.screenCapture();
        }
    }

    private void setupFinalBitmap(){
        mFinalBitmap = Bitmap.createBitmap(mScreenBitmap.getWidth()+getWidth()/2, mScreenBitmap.getHeight()+getHeight()/2, Bitmap.Config.ARGB_8888);
        Log.e("test",mScreenBitmap.getHeight()+" "+mPhoneHeight+" "+getHeight());
        Canvas canvas=new Canvas(mFinalBitmap);
        canvas.drawColor(Color.BLACK);
        int left = getWidth()/4;
        int top = getHeight()/4;
        canvas.drawBitmap(mScreenBitmap, left, top, null);

        showPickerBitmapOutOfBorder(wmCirclePickerParams.x,wmCirclePickerParams.y);
    }

    private void showPickerBitmapOutOfBorder(int wmX,int wmY){
        if(mFinalBitmap!=null){
            int DesireXLocationOnScreen=mFinalBitmap.getWidth()/2+wmX-getWidth()/2;
            int DesireYLocationOnScreen=mFinalBitmap.getHeight()/2+wmY-getHeight()/2;

            int DesireX= (int) (getWidth()-getWidth()*scaleFactor)/2;
            int DesireY= (int) (getHeight()-getHeight()*scaleFactor)/2;
            int DesireWidth= (int) (getWidth()*scaleFactor);
            int DesireHeight= (int) (getHeight()*scaleFactor);
            //Log.e("test","\n\nDesireXLocationOnScreen = "+DesireXLocationOnScreen+"\nDesireYLocationOnscreen"+DesireYLocationOnScreen+"\nDesireX = "+DesireX+"\nDesireY = "+DesireY);

            Matrix matrix = new Matrix();
            matrix.postScale(1, 1);

            mBitmap = Bitmap.createBitmap(mFinalBitmap, DesireXLocationOnScreen+DesireX, DesireYLocationOnScreen+DesireY,DesireWidth,DesireHeight, matrix, true); // TODO make test to know if you need mStatusBarHeight or not. In samsung galaxy s10 e if you must have it but in emulator you mustn't
            if(!inScale&&scaleFactor!=0.5){
                updatePickerBorder();
            }
            setup();
        }else{
            updatePhoneBitmap();
        }
    }

    public void updatePickerBorder() {
        Bitmap mBitmapScreenWithPicker = getBitmapOfView(this);

        int DesireX=  (getWidth()/2+mMiddleSquareDim/2-mMiddleBorderSquareDim);
        int DesireY= (getHeight()/2+mMiddleSquareDim/2-mMiddleBorderSquareDim);
        int DesireWidth= (mMiddleSquareDim-mMiddleBorderSquareDim);
        int DesireHeight= (mMiddleSquareDim-mMiddleBorderSquareDim);

        Bitmap AllPixel =Bitmap.createBitmap(mBitmapScreenWithPicker, DesireX, DesireY,DesireWidth,DesireHeight);

        int[] RGB =ColorUtility.getRGBAverageFromBitmap(AllPixel);
        mColorHexa = ColorUtility.getHexFromRGB(RGB);
        mColorName=ColorUtility.nearestColor(mColorHexa)[0];
        mBorderColor = Color.parseColor(mColorHexa);
        updateNotificationHex();
    }

    /**
     * get bitmap of particular view
     * @param view of which you want bitmap
     * @return bitmap of view
     */
    public Bitmap getBitmapOfView(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        view.buildDrawingCache();

        if(view.getDrawingCache() == null) return null;

        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return snapshot;
    }

    private void updateNotificationHex(){
        CirclePickerService service = CirclePickerService.Instance.getInstance();
        if(service!=null){
            service.updateHexaValue(mColorHexa);
        }else{
            throw new RuntimeException("memory leak in CirclePickerView");
        }
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public int getNavigationBarHeight(){
        int resourceId = getResources().getIdentifier(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape","dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private boolean inScale =false;

    private class UserInteractionHandler implements OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener, OnDragListener {

        private View mCirclePickerView;
        private GestureDetector gesture;
        private ScaleGestureDetector gestureScale;
        private boolean startInCircleArea=false;// use to continue moving even if user isn't in touchable area
        private boolean zeroFingerSinceScale = true; // use to decked circle picker teleports bug
        private int numFinger = 0;  // use to decked circle picker teleports bug

        public UserInteractionHandler(Context c, View v){
            gesture = new GestureDetector(c, this);
            gestureScale = new ScaleGestureDetector(c, this);
            mCirclePickerView = v;
            mCirclePickerView.setOnTouchListener(this);
            mCirclePickerView.setOnDragListener(this);
        }


        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float detectorScaleFactor = detector.getScaleFactor();
            if(detectorScaleFactor>1){ // inverse zoom - in / zoom -out
                detectorScaleFactor=1-(detectorScaleFactor-1);
            }else{
                detectorScaleFactor=1+(1-detectorScaleFactor);
            }
            scaleFactor*=detectorScaleFactor;
            scaleFactor = Math.max(0.01f, Math.min(0.50,scaleFactor));
            showPickerBitmapOutOfBorder(wmCirclePickerParams.x,wmCirclePickerParams.y);
            zeroFingerSinceScale=false;
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            inScale=true;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    inScale=false;
                    updatePickerBorder();
                }
            }, 100);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float x, float y) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            ColorsData colorsData = new ColorsData((Activity) getContext());
            colorsData.addColor(new ColorSpec(mColorHexa));
            Toast.makeText(getContext(), mColorHexa+" "+getContext().getString(R.string.Toast_have_been_added), Toast.LENGTH_LONG).show(); // TODO replace by a dialog message
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        int recViewLastX;
        int recViewLastY;
        int recViewFirstX;
        int recViewFirstY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gesture.onTouchEvent(event);
            gestureScale.onTouchEvent(event);

            numFinger=event.getPointerCount();  // use to decked circle picker teleports bug
            if(event.getAction()==MotionEvent.ACTION_DOWN||numFinger>0){ // use to continue moving even if user isn't in touchable area
                if(inTouchableArea(event.getX(),event.getY())){
                    startInCircleArea=true;
                }
            }
            onTouchEvent(event);

            if(event.getAction()==MotionEvent.ACTION_UP){  // use to decked circle picker teleports bug
                numFinger--;
                if(numFinger==0){// use to stop moving even if user isn't in touchable area
                    startInCircleArea=false;
                }
                if(numFinger==0&&!zeroFingerSinceScale){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            zeroFingerSinceScale=true;
                        }
                    }, 100);
                }
            }
            if(allowToMove(event)){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        updatePhoneBitmap();

                        recViewLastX = (int) event.getRawX();
                        recViewLastY = (int) event.getRawY();
                        recViewFirstX = recViewLastX;
                        recViewFirstY = recViewLastY;
                        break;
                    }
                    case MotionEvent.ACTION_UP:{

                        performClick();

                    }
                    case MotionEvent.ACTION_MOVE: {

                        if (event.getPointerCount() == 1){

                            int deltaX = (int) event.getRawX() - recViewLastX;
                            int deltaY = (int) event.getRawY() - recViewLastY;
                            recViewLastX = (int) event.getRawX();
                            recViewLastY = (int) event.getRawY();
                            wmCirclePickerParams.x += deltaX;
                            wmCirclePickerParams.y += deltaY;


                            int toDeductX= (int) (getWidth()-getWidth()*scaleFactor)/2; // due to zoom
                            int toDeductY= (int) (getHeight()-getHeight()*scaleFactor)/2; // due to zoom

                            int maxWidth = mFinalBitmap.getWidth()/2-mBorderWidthPx-toDeductX;
                            if(wmCirclePickerParams.x<(-1*maxWidth)){
                                wmCirclePickerParams.x=(-1*maxWidth);
                            }else if(wmCirclePickerParams.x>maxWidth){
                                wmCirclePickerParams.x=maxWidth;
                            }

                            int maxHeight = mFinalBitmap.getHeight()/2-mBorderWidthPx-toDeductY;
                            if(wmCirclePickerParams.y<(-1*maxHeight)){
                                wmCirclePickerParams.y=(-1*maxHeight);
                            }
                            if(wmCirclePickerParams.y>maxHeight){
                                wmCirclePickerParams.y=maxHeight;
                            }

                            /*Log.e("test","\nwmX ="+wmCirclePickerParams.x
                                    +"\nwmY="+wmCirclePickerParams.y
                                    +"\nmFinalBitmap width = "+(mFinalBitmap.getWidth())
                                    +"\nmFinalBitmap height = "+(mFinalBitmap.getHeight()));*/

                            WindowManager wm = (WindowManager) (getContext()).getSystemService(WINDOW_SERVICE);
                            wm.updateViewLayout(wmCirclePickerView,wmCirclePickerParams); // https://stackoverflow.com/a/17133350/12577512 we move x and y
                            showPickerBitmapOutOfBorder(wmCirclePickerParams.x,wmCirclePickerParams.y);
                            break;
                        }
                    }
                    default: {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            return true;

        }

        private boolean allowToMove(MotionEvent event){
            return (startInCircleArea||inTouchableArea(event.getX(),event.getY())) // (startInCircleArea||inTouchableArea(event.getX(),event.getY())) used to move circle
                    &&
                    zeroFingerSinceScale;
        }

        private boolean inTouchableArea(float x, float y) {
            if (mBorderRect.isEmpty()) {
                return true;
            }
            return Math.pow(x - mBorderRect.centerX(), 2) + Math.pow(y - mBorderRect.centerY(), 2) <= Math.pow(mBorderRadius, 2);
        }


    }

}


