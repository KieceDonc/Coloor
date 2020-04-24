package com.vvdev.coolor.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvdev.coolor.R;
import com.vvdev.coolor.fragment.TabHost.ColorsTab;
import com.vvdev.coolor.interfaces.ColorUtility;
import com.vvdev.coolor.interfaces.Gradient;
import com.vvdev.coolor.interfaces.Gradients;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.vvdev.coolor.interfaces.Gradients.NUM_NATIVE_GRAD;
import static com.vvdev.coolor.interfaces.Gradients.NUM_PREMIUM_GRAD;

public class GradientsRVAdapter extends RecyclerView.Adapter<GradientsRVAdapter.CustomGradientViewHolder> {

    private static final String TAG = GradientsRVAdapter.class.getName();

    private final Activity activity;
    private final RecyclerView recyclerView;
    private final Gradients gradients;

    private final setOnGradientDeleted listener;

    private boolean premiumGradCanBeSetup = true;

    public interface setOnGradientDeleted{
        void onGradientDeleted();
    }

    public GradientsRVAdapter(Activity activity,RecyclerView recyclerView,setOnGradientDeleted listener) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.gradients = Gradients.getInstance(activity);
        this.listener = listener;
        if (gradients.getAllPremium().size() == NUM_PREMIUM_GRAD) {
            premiumGradCanBeSetup = true;
        }
    }

    @Override
    public int getItemCount() {
        if(premiumGradCanBeSetup){
            return gradients.getAllPremium().size()+gradients.getAllCustom().size();
        }else{
            return gradients.getAllCustom().size();
        }
    }

    @NonNull
    @Override
    public CustomGradientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_gradients_itemrecycle, parent, false);
        return new CustomGradientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomGradientViewHolder holder, int position) {
        Gradient currentGradient = gradients.getAllCustom().get(position);
        holder.display(currentGradient);
    }

    public void removedItem(int position){
        recyclerView.getAdapter().notifyItemRemoved(position);
        recyclerView.getAdapter().notifyItemRangeChanged(position,gradients.getAllCustom().size());
        listener.onGradientDeleted();
    }

    public class CustomGradientViewHolder extends RecyclerView.ViewHolder {

        private Gradient currentGradient;

        private CircleImageView colorPreview;
        private TextView gradientName;
        private ImageView trash;

        private ConstraintLayout mainLayoutBlack;
        private ConstraintLayout mainLayoutWhite;

        private ArrayList<View> viewsBlack = new ArrayList<>();
        private ArrayList<View> viewsWhite = new ArrayList<>();

        private boolean itemDeleted = false; // prevent a bug

        public CustomGradientViewHolder(final View itemView) {
            super(itemView);

            colorPreview = itemView.findViewById(R.id.gradientColorPreview);
            gradientName = itemView.findViewById(R.id.gradientName);
            trash = itemView.findViewById(R.id.gradientTrash);

            mainLayoutBlack = itemView.findViewById(R.id.gradientIncludeBlack);
            mainLayoutWhite = itemView.findViewById(R.id.gradientIncludeWhite);

            viewsBlack.add(mainLayoutBlack.findViewById(R.id.cgResult0));
            viewsBlack.add(mainLayoutBlack.findViewById(R.id.cgResult1));
            viewsBlack.add(mainLayoutBlack.findViewById(R.id.cgResult2));
            viewsBlack.add(mainLayoutBlack.findViewById(R.id.cgResult3));
            viewsBlack.add(mainLayoutBlack.findViewById(R.id.cgResult4));
            viewsBlack.add(mainLayoutBlack.findViewById(R.id.cgResult5));

            viewsWhite.add(mainLayoutWhite.findViewById(R.id.cgResult0));
            viewsWhite.add(mainLayoutWhite.findViewById(R.id.cgResult1));
            viewsWhite.add(mainLayoutWhite.findViewById(R.id.cgResult2));
            viewsWhite.add(mainLayoutWhite.findViewById(R.id.cgResult3));
            viewsWhite.add(mainLayoutWhite.findViewById(R.id.cgResult4));
            viewsWhite.add(mainLayoutWhite.findViewById(R.id.cgResult5));


            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "trash imageView clicked");
                    if (!itemDeleted) {
                        itemDeleted = true;
                        Log.i(TAG, "gradient start to delete");
                        int position = getLayoutPosition();
                        gradients.remove(currentGradient);
                        removedItem(position);
                        ColorsTab.Instance.get().getColorsTabRVAdapter().updateSpinner();
                    }
                }
            });
        }

        public void display(Gradient gradient) {
            currentGradient = gradient;

            // setup preview of color
            Bitmap bitmapOfPreview = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888);
            bitmapOfPreview.eraseColor(Color.parseColor(currentGradient.getHexaValue()));
            colorPreview.setImageBitmap(bitmapOfPreview);

            // setup text
            gradientName.setText(currentGradient.getName());

            String[] generatedWhite = ColorUtility.gradientApproximatelyGenerator(currentGradient.getHexaValue(),"#FFFFFF",6);
            String[] generatedBlack = ColorUtility.gradientApproximatelyGenerator(currentGradient.getHexaValue(),"#000000",6);

            for (int x = 0; x < viewsBlack.size(); x++) { // setup preview generated colors
                viewsBlack.get(x).setBackgroundColor(Color.parseColor(generatedBlack[x]));
            }

            for (int x = 0; x < viewsWhite.size(); x++) { // setup preview generated colors
                viewsWhite.get(x).setBackgroundColor(Color.parseColor(generatedWhite[x]));
            }
        }
    }
}