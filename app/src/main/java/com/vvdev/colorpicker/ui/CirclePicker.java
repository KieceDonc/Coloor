package com.vvdev.colorpicker.ui;

import android.annotation.SuppressLint;
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
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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

import com.vvdev.colorpicker.R;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;

import static com.vvdev.colorpicker.activity.MainActivity.CallColorUtility;

@SuppressLint("AppCompatCustomView")
public class CirclePicker extends ImageView {

    private static final ImageView.ScaleType SCALE_TYPE = ImageView.ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final int DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private Paint mCenterRectPaint = new Paint();
    private Bitmap mScreenBitmap=null; // custom
    private Rect mMovableDimension = new Rect(); // custom
    private String TextOnBorder=""; // custom
    private float tdX = 0;// custom
    private float tdY = 0; // custom
    private int mBitmapRectDim;
    private int mBitmapRectLineDim;
    private int mCenterRectLineDim;
    private int mBitmapnumColumns, mBitmapnumRows;
    private Paint mBitmapRectPaint = new Paint();
    private boolean[][] mBitmapCellChecked;
    private boolean inUpdateScreenBitmap=false; // custom


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
    private boolean mBorderOverlay;
    private boolean mDisableCircularTransformation;


    private Path mCircle; // CUSTOM
    private Paint PaintText; // CUSTOM

    public CirclePicker(Context context) {
        super(context);
        init();
    }

    public CirclePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_civ_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR);
        mBorderOverlay = a.getBoolean(R.styleable.CircleImageView_civ_border_overlay, DEFAULT_BORDER_OVERLAY);
        mCircleBackgroundColor = a.getColor(R.styleable.CircleImageView_civ_circle_background_color, DEFAULT_CIRCLE_BACKGROUND_COLOR);

        a.recycle();

        init();
    }

    private UserInteractionHandler mUserInteractionHandler;

    private void init() {
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new com.vvdev.colorpicker.ui.CirclePicker.OutlineProvider());
        }

        if (mSetupPending) {
            setup();
            initPicker();
            mUserInteractionHandler = new UserInteractionHandler(getContext(),this);
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
        if (mDisableCircularTransformation) {
            super.onDraw(canvas);
            return;
        }

        if (mBitmap == null) {
            return;
        }

        if (mCircleBackgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mCircleBackgroundPaint);
        }
        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);
        if (mBorderWidth > 0) {
            canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint);
            canvas.drawTextOnPath(TextOnBorder, mCircle, 0f, getDpFromPx(mBorderWidth), PaintText); // CUSTOM
            canvas.drawLine(mDrawableRect.centerX()+mBitmapRectDim,mDrawableRect.centerY()+mBitmapRectDim,mDrawableRect.centerX()-mBitmapRectDim,mDrawableRect.centerY()+mBitmapRectDim,mCenterRectPaint);
            canvas.drawLine(mDrawableRect.centerX()+mBitmapRectDim,mDrawableRect.centerY()+mBitmapRectDim,mDrawableRect.centerX()+mBitmapRectDim,mDrawableRect.centerY()-mBitmapRectDim,mCenterRectPaint);
            canvas.drawLine(mDrawableRect.centerX()-mBitmapRectDim,mDrawableRect.centerY()-mBitmapRectDim,mDrawableRect.centerX()+mBitmapRectDim,mDrawableRect.centerY()-mBitmapRectDim,mCenterRectPaint);
            canvas.drawLine(mDrawableRect.centerX()-mBitmapRectDim,mDrawableRect.centerY()-mBitmapRectDim,mDrawableRect.centerX()-mBitmapRectDim,mDrawableRect.centerY()+mBitmapRectDim,mCenterRectPaint);
            /*if(scaleFactor<0.2){

                for (int i = 0; i < mBitmapnumColumns; i++) {
                    for (int j = 0; j < mBitmapnumRows; j++) {
                        if (mBitmapCellChecked[i][j]) {
                            canvas.drawRect(i * mBitmapRectDim, j * mBitmapRectDim,
                                    (i + 1) * mBitmapRectDim, (j + 1) * mBitmapRectDim,
                                    mBitmapRectPaint);
                        }

                    }
                }

                for (int x = 1; x < mBitmapnumColumns; x++) {
                    for (int i = 1; i < mBitmapnumColumns; i++) {
                        canvas.drawLine(i * mBitmapRectDim, mBitmapRectDim*x, i * mBitmapRectDim, mBitmapRectDim, mBitmapRectPaint);
                    }
                }

                for (int i = 1; i < mBitmapnumRows; i++) {
                    canvas.drawLine(0, i * mBitmapRectDim, mBitmapRectDim, i * mBitmapRectDim, mBitmapRectPaint);
                }

            }*/

        }
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

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getCircleBackgroundColor() {
        return mCircleBackgroundColor;
    }

    public void setCircleBackgroundColor(@ColorInt int circleBackgroundColor) {
        if (circleBackgroundColor == mCircleBackgroundColor) {
            return;
        }

        mCircleBackgroundColor = circleBackgroundColor;
        mCircleBackgroundPaint.setColor(circleBackgroundColor);
        invalidate();
    }

    public void setCircleBackgroundColorResource(@ColorRes int circleBackgroundRes) {
        setCircleBackgroundColor(getContext().getResources().getColor(circleBackgroundRes));
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverlay) {
            return;
        }

        mBorderOverlay = borderOverlay;
        setup();
    }

    public boolean isDisableCircularTransformation() {
        return mDisableCircularTransformation;
    }

    public void setDisableCircularTransformation(boolean disableCircularTransformation) {
        if (mDisableCircularTransformation == disableCircularTransformation) {
            return;
        }

        mDisableCircularTransformation = disableCircularTransformation;
        initializeBitmap();
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

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeBitmap() {
        if (mDisableCircularTransformation) {
            mBitmap = null;
        } else {
            mBitmap = getBitmapFromDrawable(getDrawable());
        }
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
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f);
        }
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);

        PaintText = new Paint(Paint.ANTI_ALIAS_FLAG); // CUSTOM
        mCircle = new Path(); // CUSTOM

        PaintText.setStyle(Paint.Style.FILL_AND_STROKE); // CUSTOM
        PaintText.setColor(CallColorUtility.pickTextColorBasedOnBackgroundColor(mBorderColor)); // CUSTOM
        PaintText.setTextSize(mBorderWidth); //  CUSTOM in px

        mCircle.addCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, Path.Direction.CW); // CUSTOM

        int pixel = mBitmap.getPixel((mBitmap.getWidth()/2),(mBitmap.getHeight()/2)); // middle pixel of mBitmap 
        mCenterRectPaint.setColor(CallColorUtility.pickTextColorBasedOnBackgroundColor(pixel)); // set appropriate color of center rect
        mCenterRectLineDim= (int)(0.5*(1/scaleFactor)); // calculate center line rect  width / height
        mCenterRectPaint.setStrokeWidth(mCenterRectLineDim); // set center rect width / height

        mBitmapRectDim = (int)(1*(1/scaleFactor)); // calculate rect width / height on mBitmap
        mBitmapRectLineDim =  (int)(0.4*(1/scaleFactor)); // calculate line rect width / height
        mBitmapRectPaint.setColor(Color.BLACK); // set color of grid
        mBitmapRectPaint.setStrokeWidth(mBitmapRectLineDim); // set dim grid
        mBitmapnumColumns =(int) mDrawableRadius/mBitmapRectDim; // calculate number of columms. Refer to https://stackoverflow.com/questions/24842550/2d-array-grid-on-drawing-canvas/24844534
        mBitmapnumRows = (int) mDrawableRadius/mBitmapRectDim; // calculate number of rows. Refer to https://stackoverflow.com/questions/24842550/2d-array-grid-on-drawing-canvas/24844534


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

    public void setMovableDimension(Rect MovableDimension){
        mMovableDimension = MovableDimension;
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

    private float getDpFromPx(float p){
        return p / getResources().getDisplayMetrics().density;
    }

    /**
     * update phone bitmap
     */
    private void UpdateScreenBitmap(){
        if(!inUpdateScreenBitmap) {
            inUpdateScreenBitmap=true;
            setVisibility(INVISIBLE); // set this view invisible so we won't get it in bitmap. It give a basis to work on.
            mScreenBitmap = getBitmapOfView(getRootView()); // we are sending phone view to get all the phone bitmap
            final Handler handler = new Handler(); // we call the code 100 ms later time to get the phone bitmap
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setVisibility(VISIBLE);
                    inUpdateScreenBitmap=false;
                    ShowBitmapWithScaleFactor();
                }
            }, 100); // TODO make parameters to let user choose the frequency
        }
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

        if(view.getDrawingCache() == null) return null; // Verificamos antes de que no sea null

        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return snapshot;
    }

    double scaleFactor = 0.5; // for scaleFactor = 1; if scaleFactor -> 0+ it zoom to pixel lvl

    /**
     * Show zoomed screen with desired scale factor
     */
    private void ShowBitmapWithScaleFactor(){
        if(mScreenBitmap!=null){
            int DesireX= (int) (getWidth()-getWidth()*scaleFactor)/2;
            int DesireY= (int) (getHeight()-getHeight()*scaleFactor)/2;
            int DesireWidth= (int) (getWidth()*scaleFactor);
            int DesireHeight= (int) (getHeight()*scaleFactor);
            Matrix matrix = new Matrix();
            matrix.postScale(1, 1);
            int[] locationOnScreen = new int[2];
            getLocationOnScreen(locationOnScreen);
            Bitmap bitmapInCirclePicker = Bitmap.createBitmap(mScreenBitmap, locationOnScreen[0]+DesireX, locationOnScreen[1]+DesireY,DesireWidth,DesireHeight, matrix, true);
            UpdateBorder(bitmapInCirclePicker);
            mBitmap = bitmapInCirclePicker;
            setup();
        }else{
            UpdateScreenBitmap();
        }

    }

    private void UpdateBorder(Bitmap CurrentBitmap){
        int pixel = CurrentBitmap.getPixel((CurrentBitmap.getWidth()/2),(CurrentBitmap.getHeight()/2)); // TODO take all pixels inside rectangle
        int[] RGB = new int[]{Color.red(pixel), Color.blue(pixel), Color.green(pixel)};
        TextOnBorder = CallColorUtility.getHexFromRGB(RGB);
        mBorderColor= pixel;
    }

    private void initPicker(){
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(getWidth()>0&&getHeight()>0){
                    UpdateScreenBitmap();
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private class UserInteractionHandler implements OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener, OnDragListener {

        private View mCirclePickerView;
        private GestureDetector gesture;
        private ScaleGestureDetector gestureScale;

        private boolean inScale =false;

        public UserInteractionHandler(Context c, View v){
            gesture = new GestureDetector(c, this);
            gestureScale = new ScaleGestureDetector(c, this);
            mCirclePickerView = v;
            mCirclePickerView.setOnTouchListener(this);
            mCirclePickerView.setOnDragListener(this);
        }


        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.05f, Math.min(0.50,scaleFactor));
            ShowBitmapWithScaleFactor();
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
            }, 250);
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
            float newX,newY;
            onTouchEvent(event);


            if(inTouchableArea(event.getX(),event.getY())&&mMovableDimension!=null){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        UpdateScreenBitmap();
                        tdX = getX() - event.getRawX();
                        tdY = getY() - event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_UP:{

                    }
                    case MotionEvent.ACTION_MOVE: {

                        if (event.getPointerCount() == 1&&!inScale){ // if one finger detect we move layout

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

                            ShowBitmapWithScaleFactor();
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

        private boolean inTouchableArea(float x, float y) {
            if (mBorderRect.isEmpty()) {
                return true;
            }
            return Math.pow(x - mBorderRect.centerX(), 2) + Math.pow(y - mBorderRect.centerY(), 2) <= Math.pow(mBorderRadius, 2);
        }
    }

}
