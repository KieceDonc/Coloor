package com.vvdev.coolor.ui.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

import com.vvdev.coolor.R;
import com.vvdev.coolor.interfaces.ColorUtility;
import com.vvdev.coolor.interfaces.SavedData;
import com.vvdev.coolor.interfaces.ScreenCapture;
import com.vvdev.coolor.services.CirclePickerService;

import static android.content.Context.WINDOW_SERVICE;
import static com.vvdev.coolor.activity.CirclePickerActivityStart.wmCirclePickerParams;
import static com.vvdev.coolor.activity.CirclePickerActivityStart.wmCirclePickerView;

public class CirclePickerView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener,ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = CirclePickerView.class.getName();

    private ScreenCapture mScreenCapture;
    private final ScreenCapture.OnCaptureListener mCaptureListener = new ScreenCapture.OnCaptureListener() {
        @Override
        public void onScreenCaptureSuccess(Bitmap bitmap) {
            mScreenBitmap = bitmap;
            CirclePickerService service = CirclePickerService.Instance.get();
            if(service!=null){
                service.showCirclePicker();
                inUpdateFinalBitmap=false;

                ((Activity) getContext()).runOnUiThread(new Runnable() { // use to solve a bug
                    @Override
                    public void run() {
                        updateFinalBitmap();
                    }
                });
            }
        }

        @Override
        public void onScreenCaptureFailed(String errorMsg) {
            Log.d(TAG, "onScreenCaptureFailed errorMsg:" + errorMsg);
        }
    };

    private ScaleGestureDetector gestureScale;

    private BitmapShader mBitmapShader;

    /**
     * mFinalBitmap represent mScreenBitmap with black border added
     */
    private Bitmap mFinalBitmap;

    /**
     * mScreenBitmap represent the bitmap screenshot of the device
     */
    private Bitmap mScreenBitmap;

    /**
     * mBitmap present the bitmap inside the circle
     */
    private Bitmap mBitmap;

    private Path mBorderCircle;

    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private Paint mBorderPaintText;
    private Paint mMiddleLinePaint;

    private final Matrix mShaderMatrix = new Matrix();

    private RectF mDrawableRect;
    private RectF mBorderRect;

    private final Rect mBorderColorNameRect = new Rect();
    private final Rect mBorderColorHexRect = new Rect();


    private String mColorName="";
    private String mColorHexa="";

    private double scaleFactor = 0.5; // for scaleFactor = 1; if scaleFactor -> 0+ it zoom to pixel lvl

    private float mDrawableRadius;
    private float mBorderRadius;
    private float mPositionBorderColorName;
    private float mPositionBorderHex;

    private int mPhoneWidth;
    private int mPhoneHeight;
    private int mBitmapHeight;
    private int mBitmapWidth;
    private final int mBorderWidthDp = 12;
    private final int mBorderWidthPx = convertDpToPx(mBorderWidthDp);
    private int mBorderColor;
    private final int mMiddleLineStrokeWidth=3;
    private final int mMiddleLineSize=convertDpToPx(10);

    private int numFinger = 0;  // use to decked circle picker teleports bug
    private int recViewLastX;
    private int recViewLastY;
    private int recViewFirstX;
    private int recViewFirstY;

    private boolean mReady;
    private boolean mSetupPending=true;
    private boolean inUpdateFinalBitmap;
    private boolean startInCircleArea=false;// use to continue moving even if user isn't in touchable area
    private boolean zeroFingerSinceScale = true; // use to decked circle picker teleports bug


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

    /**
     * first part = we divide the circle in 8 part. we want the position at 7/8. 7/8 = 0.875 * circle perimeter.
     * After that the first part give the starting point but we want to center our text.
     * second part = we get width of text and we divide by 2 to get the offset to delete
     */
    @Override
    protected void onDraw(Canvas canvas) {

        if (mBitmap != null) {
            if(mBorderColor==Color.TRANSPARENT){
                setup();
            }

            try {
                canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);
                canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint);

                canvas.drawTextOnPath(mColorName, mBorderCircle, mPositionBorderColorName, mBorderWidthDp, mBorderPaintText);
                canvas.drawTextOnPath(mColorHexa, mBorderCircle, mPositionBorderHex, mBorderWidthDp, mBorderPaintText);

                canvas.drawLine(mDrawableRect.centerX()-mMiddleLineSize,mDrawableRect.centerY(),mDrawableRect.centerX()+mMiddleLineSize,mDrawableRect.centerY(),mMiddleLinePaint);
                canvas.drawLine(mDrawableRect.centerX(),mDrawableRect.centerY()-mMiddleLineSize,mDrawableRect.centerX(),mDrawableRect.centerY()+mMiddleLineSize,mMiddleLinePaint);
            }catch (NullPointerException e){
                e.printStackTrace();
                init();
            }
        }

    }

    private void init() {
        mReady=true;
    }

    public void readyToInit(){
        if (mSetupPending) {
            Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            mPhoneWidth = size.x;
            mPhoneHeight= size.y;

            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            ScreenCapture.Instance.set(new ScreenCapture(mPhoneHeight,mPhoneWidth,metrics.densityDpi));
            mScreenCapture = ScreenCapture.Instance.get();
            mScreenCapture.setCaptureListener(mCaptureListener);

            updateScreenBitmap();

            setup();

            gestureScale = new ScaleGestureDetector(getContext(), this);
            setOnTouchListener(this); // used to set on listener circle picker interaction
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
            updateScreenBitmap();
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

        mMiddleLinePaint=new Paint();
        mMiddleLinePaint.setStyle(Paint.Style.STROKE);
        mMiddleLinePaint.setStrokeWidth(mMiddleLineStrokeWidth);
        int middlePixel = mBitmap.getPixel(mBitmap.getWidth()/2,mBitmap.getHeight()/2);
        int[] RGB = new int[]{Color.red(middlePixel),Color.green(middlePixel),Color.blue(middlePixel)};
        mColorHexa = ColorUtility.getHexFromRGB(RGB);
        mColorName = ColorUtility.nearestColor(mColorHexa)[0];
        mBorderColor = Color.parseColor(mColorHexa);
        mMiddleLinePaint.setColor(ColorUtility.pickTextColorBasedOnBackgroundColor(middlePixel));
        updateNotificationHex();

        mBorderCircle = new Path();
        mBorderCircle.addCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, Path.Direction.CW);

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidthPx);

        mBorderPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
        mBorderPaintText.setColor(ColorUtility.pickTextColorBasedOnBackgroundColor(mBorderColor));
        mBorderPaintText.setTextSize(mBorderWidthPx);

        mBorderPaintText.getTextBounds(mColorName,0,mColorName.length(),mBorderColorNameRect);
        mBorderPaintText.getTextBounds(mColorHexa,0,mColorHexa.length(),mBorderColorHexRect);

        mPositionBorderColorName = (float)((2*Math.PI*mBorderRadius*0.875)-mBorderColorNameRect.width()/2);
        mPositionBorderHex = (float)((2*Math.PI*mBorderRadius*0.615)- mBorderColorHexRect.width()/2);

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

    /**
     * Start the update of phone bitmap
     */
    public void updateScreenBitmap(){
        if(!inUpdateFinalBitmap) {
            inUpdateFinalBitmap=true;
            CirclePickerService.Instance.get().hideCirclePicker();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() { // we delay by XXXXX ms to prevent circle picker view to capturing her self too much
                public void run() {
                    mScreenCapture.startScreenCapture();
                }
            }, 15);
        }
    }

    /**
     * We get ScreenCapture bitmap and we create black border in mFinalBitmap so user can move to border
     */
    private void updateFinalBitmap(){
        mFinalBitmap = Bitmap.createBitmap(mScreenBitmap.getWidth()+getWidth(), mScreenBitmap.getHeight()+getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(mFinalBitmap);
        canvas.drawColor(Color.BLACK);
        int left = getWidth()/2;
        int top = getHeight()/2;
        canvas.drawBitmap(mScreenBitmap, left, top, null);

        update_mBitmap(wmCirclePickerParams.x,wmCirclePickerParams.y);
    }

    /**
     * update mBitmap
     * @param wmX
     * @param wmY
     */
    private void update_mBitmap(int wmX,int wmY){
        if(mFinalBitmap!=null){
            int DesireXLocationOnScreen=mFinalBitmap.getWidth()/2+wmX-getWidth()/2;
            int DesireYLocationOnScreen=mFinalBitmap.getHeight()/2+wmY-getHeight()/2;

            int DesireX= (int) (getWidth()-getWidth()*scaleFactor)/2;
            int DesireY= (int) (getHeight()-getHeight()*scaleFactor)/2;
            int DesireWidth= (int) (getWidth()*scaleFactor);
            int DesireHeight= (int) (getHeight()*scaleFactor);
            Matrix matrix = new Matrix();
            matrix.postScale(1, 1);

            mBitmap = Bitmap.createBitmap(mFinalBitmap, DesireXLocationOnScreen+DesireX, DesireYLocationOnScreen+DesireY,DesireWidth,DesireHeight, matrix, true);
            setup();
        }else{
            updateScreenBitmap();
        }
    }

    /**
     * Update the hexadecimal value inside the notification
     */
    private void updateNotificationHex(){
        CirclePickerService service = CirclePickerService.Instance.get();
        try{
            service.updateHexaValue(mColorHexa);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void saveCurrentColor(){
        Activity activity = (Activity) getContext();
        SavedData.getInstance(activity).addColor(mColorHexa);
    }

    public void zoomIn(){
        if(scaleFactor>0.175){
            scaleFactor-=0.1;
        }else{
            scaleFactor-=0.025;
        }

        scaleFactor = Math.max(0.01f, Math.min(0.50,scaleFactor));
        update_mBitmap(wmCirclePickerParams.x,wmCirclePickerParams.y);
    }

    public void zoomOut(){
        if(scaleFactor>0.175){
            scaleFactor+=0.1;
        }else{
            scaleFactor+=0.025;
        }
        scaleFactor = Math.max(0.01f, Math.min(0.50,scaleFactor));
        update_mBitmap(wmCirclePickerParams.x,wmCirclePickerParams.y);
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
        update_mBitmap(wmCirclePickerParams.x,wmCirclePickerParams.y);
        zeroFingerSinceScale=false;
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureScale.onTouchEvent(event);

        numFinger=event.getPointerCount();  // use to decked circle picker teleports bug
        if(event.getAction()==MotionEvent.ACTION_DOWN||numFinger>0){ // use to continue moving even if user isn't in touchable area
            if(inTouchableArea(event)){
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

                    updateScreenBitmap();
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

                        int maxWidth = mFinalBitmap.getWidth()/2-(mFinalBitmap.getWidth()/2-mScreenBitmap.getWidth()/2);

                        int toDeductY=mMiddleLineStrokeWidth;
                        int toDeductX = mBorderWidthPx+mMiddleLineStrokeWidth*2;

                        if(wmCirclePickerParams.x<(-1*maxWidth+toDeductX)){
                            wmCirclePickerParams.x=(-1*maxWidth+toDeductX);
                        }else if(wmCirclePickerParams.x>maxWidth-toDeductX){
                            wmCirclePickerParams.x=maxWidth-toDeductX;
                        }

                        int maxHeight = mFinalBitmap.getHeight()/2-(mFinalBitmap.getHeight()/2-mScreenBitmap.getHeight()/2);
                        if(wmCirclePickerParams.y<(-1*maxHeight+toDeductY)){
                            wmCirclePickerParams.y=(-1*maxHeight+toDeductY);
                        }
                        if(wmCirclePickerParams.y>maxHeight){
                            wmCirclePickerParams.y=maxHeight;
                        }

                        WindowManager wm = (WindowManager) (getContext()).getSystemService(WINDOW_SERVICE);
                        wm.updateViewLayout(wmCirclePickerView,wmCirclePickerParams); // https://stackoverflow.com/a/17133350/12577512 we move x and y
                        update_mBitmap(wmCirclePickerParams.x,wmCirclePickerParams.y);
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

    /**
     * Give the permission to move the CirclePickerView or not
     * @param event
     * @return true = can move, false = can't move
     */
    private boolean allowToMove(MotionEvent event){
        return (zeroFingerSinceScale
                &&
                !inUpdateFinalBitmap
                &&
                startInCircleArea||inTouchableArea(event)); // (startInCircleArea||inTouchableArea(event.getX(),event.getY())) used to move circle;
    }

    /**
     * Detect if MotionEven is inside CirclePickerView
     * @param event
     * @return true = MotionEvent is inside CirclePickerView, false = MotionEvent is outside CirclePickerView
     */
    private boolean inTouchableArea(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        if (mBorderRect.isEmpty()) {
            return true;
        }
        return Math.pow(x - mBorderRect.centerX(), 2) + Math.pow(y - mBorderRect.centerY(), 2) <= Math.pow(mBorderRadius, 2);
    }


}


