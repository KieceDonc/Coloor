package com.vvdev.colorpicker.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.interfaces.ColorSpec;
import com.vvdev.colorpicker.interfaces.ColorUtility;
import com.vvdev.colorpicker.interfaces.ColorsData;
import com.vvdev.colorpicker.Service.ScreenCapture;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;

@SuppressLint("AppCompatCustomView")
public class CirclePickerOld extends ImageView {

    private static final ImageView.ScaleType SCALE_TYPE = ImageView.ScaleType.CENTER_CROP;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private Path mCircle; // CUSTOM
    private Paint mBorderPaintText; // CUSTOM
    private Rect ColorNameDim = new Rect();
    private Rect ColorHexDim = new Rect();
    private Bitmap mScreenBitmap=null; // custom
    private Rect mMovableDimension = new Rect(); // custom
    private String mTextOnBorderHex=""; // custom
    private String mTextOnBorderColorName="";
    private float tdX = 0;// custom
    private float tdY = 0; // custom
    private boolean inUpdatePhoneBitmap=false; // custom
    private Paint gridSquarePaint;
    private int gridSquareDim;
    private int gridSquareBorderDim;
    public static int timeUpdateCirclePicker = 10;
    private ScreenCapture mScreenCapture = null;
    private final String TAG ="CirclePicker";



    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private final Paint mCircleBackgroundPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private int mCircleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;

    private ColorFilter mColorFilter;

    private boolean mReady;
    private boolean mSetupPending;


    public CirclePickerOld(Context context) {
        super(context);
        init();
    }

    public CirclePickerOld(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePickerOld(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_civ_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR);
        mCircleBackgroundColor = a.getColor(R.styleable.CircleImageView_civ_circle_background_color, DEFAULT_CIRCLE_BACKGROUND_COLOR);

        a.recycle();

        init();
    }

    private void init() {
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new com.vvdev.colorpicker.ui.CirclePickerOld.OutlineProvider());
        }

        if (mSetupPending) {
            setup();
            initPicker();
            new UserInteractionHandler(getContext(), this);
            mSetupPending = false;
        }

    }

    @Override
    public ImageView.ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ImageView.ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mBitmap == null) {
            return;
        }

        if (mCircleBackgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mCircleBackgroundPaint);
        }
        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);
        canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint);
        /**
         * first part = we divide the circle in 8 part. we want the position at 7/8. 7/8 = 0.875 * circle perimeter.
         * After that the first part give the starting point but we want to center our text.
         * second part = we get width of text and we divide by 2 to get the offset to delete
         */

        mBorderPaintText.getTextBounds(mTextOnBorderColorName,0,mTextOnBorderColorName.length(),ColorNameDim);
        mBorderPaintText.getTextBounds(mTextOnBorderHex,0,mTextOnBorderHex.length(),ColorHexDim);
        float offset1 = ColorNameDim.width()/2;
        float offset2 = ColorHexDim.width()/2;
        float positionOnBorderName = (float)(2*Math.PI*mBorderRadius*0.875)-offset1;
        float positionOnBorderHex = (float)(2*Math.PI*mBorderRadius*0.615)-offset2;
        canvas.drawTextOnPath(mTextOnBorderHex, mCircle, positionOnBorderHex, getDpFromPx(mBorderWidth), mBorderPaintText); // CUSTOM
        canvas.drawTextOnPath(mTextOnBorderColorName, mCircle, positionOnBorderName, getDpFromPx(mBorderWidth), mBorderPaintText); // CUSTOM
        canvas.drawRect(mDrawableRect.centerX()+gridSquareDim,mDrawableRect.centerY()+gridSquareDim,mDrawableRect.centerX()-gridSquareDim,mDrawableRect.centerY()-gridSquareDim,gridSquarePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        applyColorFilter();
        invalidate();
    }

    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    private void applyColorFilter() {
        mBitmapPaint.setColorFilter(mColorFilter);
    }

    private void initializeBitmap() {
        updatePhoneBitmap(); // TODO make something more cleaner
        setup();
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }

        if (mBitmap == null) {
            invalidate();
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);


        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mCircleBackgroundPaint.setStyle(Paint.Style.FILL);
        mCircleBackgroundPaint.setAntiAlias(true);
        mCircleBackgroundPaint.setColor(mCircleBackgroundColor);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(calculateBounds());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2.0f, (mBorderRect.width() - mBorderWidth) / 2.0f);

        mDrawableRect.set(mBorderRect);
        if (mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f);
        }
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);

        mBorderPaintText = new Paint(Paint.ANTI_ALIAS_FLAG); // CUSTOM
        mCircle = new Path(); // CUSTOM

        mBorderPaintText.setStyle(Paint.Style.FILL_AND_STROKE); // CUSTOM
        mBorderPaintText.setColor(ColorUtility.pickTextColorBasedOnBackgroundColor(mBorderColor)); // CUSTOM
        mBorderPaintText.setTextSize(mBorderWidth); //  CUSTOM in px

        mCircle.addCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, Path.Direction.CW); // CUSTOM

        gridSquarePaint=new Paint();
        if(scaleFactor>0.075) {
            gridSquareDim = (int) ((1 / scaleFactor));
            gridSquareBorderDim = (int) (0.5 * (1 / scaleFactor));
        }
        gridSquarePaint.setStyle(Paint.Style.STROKE);
        gridSquarePaint.setStrokeWidth(gridSquareBorderDim);
        int pixel = mBitmap.getPixel(mBitmap.getWidth()/2,mBitmap.getHeight()/2);
        gridSquarePaint.setColor(ColorUtility.pickTextColorBasedOnBackgroundColor(pixel));

        getRootView().getGlobalVisibleRect(mMovableDimension);


        applyColorFilter();
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class OutlineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            Rect bounds = new Rect();
            mBorderRect.roundOut(bounds);
            outline.setRoundRect(bounds, bounds.width() / 2.0f);
        }

    }

    public float getDpFromPx(float p){
        return p / getResources().getDisplayMetrics().density;
    }

    private void initPicker(){
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(getWidth()>0&&getHeight()>0){
                    updatePhoneBitmap();
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    double scaleFactor = 0.5; // for scaleFactor = 1; if scaleFactor -> 0+ it zoom to pixel lvl

    /**
     * Show zoomed screen with desired scale factor
     */
    private void showPickerBitmap(){
        if(mScreenBitmap!=null){

            int DesireX= (int) (getWidth()-getWidth()*scaleFactor)/2;
            int DesireY= (int) (getHeight()-getHeight()*scaleFactor)/2;
            int DesireWidth= (int) (getWidth()*scaleFactor);
            int DesireHeight= (int) (getHeight()*scaleFactor);

            Matrix matrix = new Matrix();
            matrix.postScale(1, 1);

            int[] locationOnScreen = new int[2];
            getLocationOnScreen(locationOnScreen);
            mBitmap = Bitmap.createBitmap(mScreenBitmap, locationOnScreen[0]+DesireX, locationOnScreen[1]+DesireY,DesireWidth,DesireHeight, matrix, true);
            setup();
            if(!inScale){
                updatePickerBorder();
            }
        }else{
            updatePhoneBitmap();
        }
    }

    /**
     * update phone bitmap
     */
    public void updatePhoneBitmap(){
        if(!inUpdatePhoneBitmap) {
            inUpdatePhoneBitmap=true;

           setVisibility(INVISIBLE); // set this view invisible so we won't get it in bitmap. It give a basis to work on.
           mScreenBitmap = getBitmapOfView(getRootView()); // we are sending phone view to get all the phone bitmap
            Handler handler = new Handler(); // we call the code 100 ms later time to get the phone bitmap
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setVisibility(VISIBLE);
                    inUpdatePhoneBitmap=false;
                    showPickerBitmap();
                }
            }, timeUpdateCirclePicker); // TODO make parameters to let user choose the frequency

        }
    }

    public void updatePickerBorder() {

        Bitmap mBitmapScreenWithPicker = getBitmapOfView(this);

        int DesireX=  (getWidth()/2+gridSquareDim/2-gridSquareBorderDim);
        int DesireY= (getHeight()/2+gridSquareDim/2-gridSquareBorderDim);
        int DesireWidth= (gridSquareDim-gridSquareBorderDim);
        int DesireHeight= (gridSquareDim-gridSquareBorderDim);

        Bitmap AllPixel =Bitmap.createBitmap(mBitmapScreenWithPicker, DesireX, DesireY,DesireWidth,DesireHeight);

        int[] RGB =ColorUtility.getRGBAverageFromBitmap(AllPixel);
        mTextOnBorderHex = ColorUtility.getHexFromRGB(RGB);
        mTextOnBorderColorName=ColorUtility.nearestColor(mTextOnBorderHex)[0];
        mBorderColor = Color.parseColor(mTextOnBorderHex);
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

    public void setupScreenCapture(){
        mScreenCapture.setCaptureListener(new ScreenCapture.OnCaptureListener() {
            @Override
            public void onScreenCaptureSuccess(Bitmap bitmap, String savePath) {
                Log.d(TAG, "onScreenCaptureSuccess savePath:" + savePath);

                mScreenBitmap = bitmap;
                setVisibility(VISIBLE);
                inUpdatePhoneBitmap=false;
                showPickerBitmap();

            }

            @Override
            public void onScreenCaptureFailed(String errorMsg) {
                Log.d(TAG, "onScreenCaptureFailed errorMsg:" + errorMsg);
            }

            @Override
            public void onScreenRecordStart() {
                Log.d(TAG, "onScreenRecordStart");
            }

            @Override
            public void onScreenRecordStop() {
                Log.d(TAG, "onScreenRecordStop");
            }

            @Override
            public void onScreenRecordSuccess(String savePath) {
                Log.d(TAG, "onScreenRecordSuccess savePath:" + savePath);
            }

            @Override
            public void onScreenRecordFailed(String errorMsg) {
                Log.d(TAG, "onScreenRecordFailed errorMsg:" + errorMsg);
            }
        });
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
            getRootView().setOnTouchListener(this);
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
            showPickerBitmap();
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
                }
            }, 100);
            updatePickerBorder();
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
            ColorsData colorsData = new ColorsData((Activity) getContext());
            colorsData.addColor(new ColorSpec(mTextOnBorderHex));
            Toast.makeText(getContext(), mTextOnBorderHex+" have been added to the palette !", Toast.LENGTH_LONG).show(); // TODO replace by a dialog message

            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gesture.onTouchEvent(event);
            gestureScale.onTouchEvent(event);

            numFinger=event.getPointerCount();  // use to decked circle picker teleports bug

            float newX,newY;
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
            if(event.getAction()==MotionEvent.ACTION_DOWN){ // use to continue moving even if user isn't in touchable area
                if(inTouchableArea(event.getX(),event.getY())){
                    startInCircleArea=true;
                }
            }

            if(allowToMove(event)){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        updatePhoneBitmap();
                        tdX = getX() - event.getRawX();
                        tdY = getY() - event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_UP:{

                    }
                    case MotionEvent.ACTION_MOVE: {

                       if (event.getPointerCount() == 1){ // if one finger detect we move layout

                            newX = event.getRawX() + tdX;
                            newY = event.getRawY() + tdY;

                            if (!(newX <= 0 || newX >= mMovableDimension.width() - getWidth())) {
                                animate().x(event.getRawX() + tdX).setDuration(0).start();
                            } else if (newX >= mMovableDimension.width() - getWidth()) {
                                animate().x(mMovableDimension.width() - getWidth()).setDuration(0).start();
                            } else {
                                animate().x(0).setDuration(0).start();
                            }

                            if (!(newY <= 0 || newY >= mMovableDimension.height() - getHeight())) {
                                animate().y(event.getRawY() + tdY).setDuration(0).start();
                            } else if (newY >= mMovableDimension.height() - getHeight()

                            ) {
                                animate().y(mMovableDimension.height() - getHeight()).setDuration(0).start();
                            } else {
                                animate().y(0).setDuration(0).start();
                            }

                            showPickerBitmap();
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
        public boolean onDrag(View v, DragEvent event) { // https://www.vogella.com/tutorials/AndroidDragAndDrop/article.html
                return true;

        }

        private boolean allowToMove(MotionEvent event){
            return (startInCircleArea||inTouchableArea(event.getX(),event.getY())) // (startInCircleArea||inTouchableArea(event.getX(),event.getY())) used to move circle
                    &&
                    mMovableDimension!=null
                    &&
                    zeroFingerSinceScale
                    &&
                    !inScale;
        }

        private boolean inTouchableArea(float x, float y) {
            if (mBorderRect.isEmpty()) {
                return true;
            }
            return Math.pow(x - mBorderRect.centerX(), 2) + Math.pow(y - mBorderRect.centerY(), 2) <= Math.pow(mBorderRadius, 2);
        }
    }

}
