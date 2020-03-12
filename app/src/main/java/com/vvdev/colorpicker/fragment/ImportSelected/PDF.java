package com.vvdev.colorpicker.fragment.ImportSelected;

import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class PDF extends Fragment {

    public static final String KEY_ARGUMENT_PDF_PATH ="PathToPDF";

    private static String TAG = PDF.class.getName();

    private PDFView pdfView;
    private EditText inputDesirePage;
    private TextView numberOfPage;
    private Uri pathToPDF;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.import_pdf, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        String toCheck = getArguments().getString(KEY_ARGUMENT_PDF_PATH); // get data send, plz refer to https://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments
        pathToPDF = Uri.parse(toCheck); // plz refer to https://stackoverflow.com/questions/17356312/converting-of-uri-to-string


        pdfView = view.findViewById(R.id.pdfView);
        inputDesirePage = view.findViewById(R.id.input_desire_page);
        numberOfPage = view.findViewById(R.id.numberOfPage);

        setupPdfView();
        setupInputDesirePage();
    }


    /*@Override
    public void onBackPressed() { // https://stackoverflow.com/questions/5448653/how-to-implement-onbackpressed-in-fragments
        super.onBackPressed();
        if(inputDesirePage.isCursorVisible()){
            inputDesirePage.clearFocus();
            inputDesirePage.setCursorVisible(false);
        }
    }*/

    private void setupPdfView(){
        DefaultLinkHandler mDefaultLinkHandler = new DefaultLinkHandler(pdfView);

        pdfView.fromUri(pathToPDF)
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
                        Log.e(TAG,"Error at onError. Throwable :\n"+t);

                    }
                })
                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {
                        Log.e(TAG,"Error at onPageError, page number "+page+". Throwable :\n"+t);
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
                    allowTextChange=pdfView.getPageCount() >= Integer.parseInt(s.toString()) && Integer.parseInt(s.toString()) > 0;
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
                    inputDesirePage.clearFocus();
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
