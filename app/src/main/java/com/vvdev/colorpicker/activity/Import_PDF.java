package com.vvdev.colorpicker.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.link.DefaultLinkHandler;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnLongPressListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.vvdev.colorpicker.R;
import com.vvdev.colorpicker.ui.CirclePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import static android.view.View.inflate;
import static com.vvdev.colorpicker.fragment.Import.ImportFragment.IntentExtraPath;
import static com.vvdev.colorpicker.ui.CirclePicker.timeUpdateCirclePicker;

public class Import_PDF extends AppCompatActivity {


    // https://github.com/yeokm1/docs-to-pdf-converter#library-usage
    // https://github.com/alaeddinejebali/Android-ConvertToPDF
    private ConstraintLayout rootConstraintLayout;
    private PDFView pdfView;
    private View CirclePickerView;
    private EditText inputDesirePage;
    private TextView numberOfPage;
    private CirclePicker mCirclePicker;
    private boolean circlePickerAlreadyAdded = false;
    private boolean circleViewVisibility=true;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_pdf);
        getSupportActionBar().hide();

        pdfView = findViewById(R.id.pdfView);
        inputDesirePage = findViewById(R.id.input_desire_page);
        numberOfPage = findViewById(R.id.numberOfPage);
        rootConstraintLayout = findViewById(R.id.import_WebViewConstraintLayout);


        setupPdfView();
        setupCirclePicker();
        setupInputDesirePage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(inputDesirePage.isCursorVisible()){
            inputDesirePage.clearFocus();
            inputDesirePage.setCursorVisible(false);
        }
    }

    private void setupPdfView(){
        Intent receiveData = getIntent(); // get intent
        Uri path = Uri.parse(receiveData.getStringExtra(IntentExtraPath)); // get img path from intent

        DefaultLinkHandler mDefaultLinkHandler = new DefaultLinkHandler(pdfView);

        pdfView.fromUri(path)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                // allows to draw something on the current page, usually visible in the middle of the screen
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                    }
                })
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                .onDrawAll(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                    }
                })
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                    }
                }) // called after document is loaded and starts to be rendered
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        numberOfPage.setText(String.valueOf(pageCount));
                        inputDesirePage.setText(String.valueOf((page+1)));

                    }
                })
                .onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e("Import_PDF","Error at onError. Trowable :\n"+t);

                    }
                })
                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {
                        Log.e("Import_PDF","Error at onPageError, page number "+page+". Trowable :\n"+t);
                    }
                })
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages) {

                    }

                }) // called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        return false;
                    }
                })
                .onLongPress(new OnLongPressListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {

                    }
                })
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(20)
                .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
                .linkHandler(mDefaultLinkHandler)
                .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
                .pageSnap(false) // snap pages to screen boundaries
                .pageFling(false) // make a fling change only a single page like ViewPager
                .nightMode(false) // toggle night mode
                .load();
    }

    private void setupCirclePicker(){
        final Context c = this;
        findViewById(R.id.startCirclePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!circlePickerAlreadyAdded){
                    circlePickerAlreadyAdded=true;
                    CirclePickerView = inflate(c,R.layout.circlepicker,rootConstraintLayout);
                    rootConstraintLayout.bringChildToFront(CirclePickerView);// make view to first plan
                    mCirclePicker = findViewById(R.id.CirclePicker);
                }else if(circleViewVisibility){
                    circleViewVisibility=false;
                    mCirclePicker.setVisibility(View.GONE);
                }else{
                    mCirclePicker.setVisibility(View.INVISIBLE);
                    mCirclePicker.updatePhoneBitmap();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCirclePicker.setVisibility(View.VISIBLE);
                        }
                    }, timeUpdateCirclePicker+10);
                    circleViewVisibility=true;
                }
            }
        });
    }

    private void setupInputDesirePage(){
        inputDesirePage.clearFocus();
        inputDesirePage.setCursorVisible(false);
        inputDesirePage.addTextChangedListener(new TextWatcher() {

            boolean inModification=false;
            boolean allowTextChange=false;
            CharSequence oldCharSequence;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldCharSequence=""+(pdfView.getCurrentPage()+1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0){
                    if(pdfView.getPageCount()>=Integer.parseInt(s.toString())&&Integer.parseInt(s.toString())>0){
                        allowTextChange=true;
                    }else{
                        allowTextChange=false;
                    }
                }else{
                    oldCharSequence="";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!allowTextChange&&!inModification){
                    inModification=true;
                    inputDesirePage.setText(oldCharSequence);
                    inModification=false;
                }
            }
        });

        inputDesirePage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if(actionId == EditorInfo.IME_ACTION_SEND&&inputDesirePage.getText().toString().length()>0&&inputDesirePage.getText().toString().length()<=pdfView.getPageCount()){
                    pdfView.jumpTo((Integer.parseInt(inputDesirePage.getText().toString())-1),true);
                    handled=true;
                }

                return handled;
            }
        });


        inputDesirePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDesirePage.setCursorVisible(true);
            }
        });
    }
}
