package com.vvdev.coolor.fragment.TabHost;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.vvdev.coolor.R;
import com.vvdev.coolor.databinding.FragmentColorTabBinding;
import com.vvdev.coolor.interfaces.SaveColorsToFile;
import com.vvdev.coolor.interfaces.SavedData;
import com.vvdev.coolor.services.CirclePickerService;
import com.vvdev.coolor.ui.adapter.ColorsTabRVAdapter;
import com.vvdev.coolor.ui.alertdialog.AddFromHex;
import com.vvdev.coolor.ui.alertdialog.PickFromWheel;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.markormesher.android_fab.FloatingActionButton;
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;
import uk.co.markormesher.android_fab.SpeedDialMenuOpenListener;

public class ColorsTab extends Fragment {

    private final static String TAG = ColorsTab.class.getName();

    private final ActivityResultLauncher<Intent> drawOverAllAppsPerm = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        (result) -> {
            if (Settings.canDrawOverlays(getActivity())) {
                CirclePickerService.start(getContext());
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
    });

    private final ActivityResultLauncher<String[]> saveColorsToFilesPermission = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(),
        (Map<String, Boolean> result)->{
            boolean allPermissionGranted = true;
            Iterator<Boolean> iterator = result.values().iterator();

            while(iterator.hasNext() && allPermissionGranted){
                allPermissionGranted = iterator.next();
            }

            if (allPermissionGranted) {
                new SaveColorsToFile(getActivity());
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
    });

    private FloatingActionButton actionMenu;

    private ConstraintLayout tutorial;
    private RecyclerView recyclerView;
    private ColorsTabRVAdapter colorsTabRVAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance.set(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentColorTabBinding binding = FragmentColorTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //View view = inflater.inflate(R.layout.fragment_color_tab, container, false);
        actionMenu = binding.PaletteABMenu;
        tutorial = binding.ColorTabTuto.getRoot().findViewById(R.id.ColorTabTuto);
        recyclerView = binding.pRecyclerView;

        setupPaletteRecycleView();
        setupActionButton();

        SavedData temp = SavedData.getInstance(getActivity());
        if(temp.getColorsSize()>0){
            showColors();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Instance.set(null);
    }

    private void setupActionButton(){
        actionMenu.setContentCoverEnabled(false);

        actionMenu.setSpeedDialMenuAdapter(new SpeedDialMenuAdapter(){
            @Override
            public int getCount() {
                return 5;
            }

            @NotNull
            @Override
            public SpeedDialMenuItem getMenuItem(@NotNull Context context, int i) {
                switch(i){
                    case 4:{
                        return new SpeedDialMenuItem(context,R.drawable.save_icon,R.string.colors_tab_save_to_file);
                    }
                    case 3:{
                        return new SpeedDialMenuItem(context, R.drawable.ic_pipette_padding_0dp,R.string.colors_tab_pick_from_pixel);
                    }
                    case 2:{
                        return new SpeedDialMenuItem(context, R.drawable.icon_fab_fromwheel,R.string.colors_tab_pick_from_wheel);
                    }
                    case 1:{
                        return new SpeedDialMenuItem(context, R.drawable.icon_fab_hexa,R.string.colors_tab_add_hexa);
                    }
                    case 0:{
                        return new SpeedDialMenuItem(context, R.drawable.icon_fab_delete,R.string.colors_tab_delete_all_color);
                    }
                }
                return null;
            }

            @Override
            public boolean onMenuItemClick(int position) {
                switch(position){
                    case 4:{
                        SaveAllColorsToFile();
                        break;
                    }
                    case 3:{
                        if (!Settings.canDrawOverlays(getActivity())) {
                            CirclePickerStart();
                        } else {
                            CirclePickerService.start(getContext());
                        }
                        break;
                    }
                    case 2:{
                        Log.i(TAG,"Action button pick from wheel clicked");
                        PickFromWheel cpfwd = new PickFromWheel(getActivity());
                        cpfwd.show();
                        break;
                    }
                    case 1:{
                        Log.i(TAG,"Action button add color clicked");
                        AddFromHex cad = new AddFromHex(getActivity());
                        cad.show();
                        break;
                    }case 0: {
                        new AlertDialog.Builder(Instance.get().getContext())
                                // set message, title, and icon
                                .setTitle(R.string.alertdialog_confirmdelete_delete)
                                .setMessage(R.string.alertdialog_confirmdelete_mainText)
                                .setIcon(R.drawable.trash_icon)

                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        SavedData.getInstance(getActivity()).clearColors();
                                        dialog.dismiss();
                                    }

                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create().show();
                        break;
                    }
                }
                return super.onMenuItemClick(position);
            }

            @Override
            public void onPrepareItemLabel(@NotNull Context context, int position, @NotNull TextView label) {
                label.setTextColor(getResources().getColor(R.color.Theme1_text));

                int padding = 15;
                label.setPadding(padding,padding,padding,padding);

                ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel()
                        .toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED,15)
                        .build();
                MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(shapeAppearanceModel);
                shapeDrawable.setFillColor(ContextCompat.getColorStateList(getContext(),R.color.Theme1_Third));
                label.setBackground(shapeDrawable);
            }

            @Override
            public int getBackgroundColour(int position) {
                return getResources().getColor(R.color.Theme1_Secondary);
            }

            @Override
            public float fabRotationDegrees() {
                return 180+45;
            }
        });

        actionMenu.setOnSpeedDialMenuOpenListener(new SpeedDialMenuOpenListener() {
            @Override
            public void onOpen(@NotNull FloatingActionButton floatingActionButton){
            }
        });
    }

    private void CirclePickerStart(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
            .setIcon(android.R.drawable.ic_dialog_info)
            .setTitle(getResources().getString(R.string.circle_picker_activity_title))
            .setMessage(getResources().getString(R.string.circle_picker_activity_message))
            .setPositiveButton(getResources().getString(R.string.circle_picker_activity_yes), (DialogInterface dialogInterface, int i)-> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName())); // we only call the alert dialog if we are SDK > 23
                drawOverAllAppsPerm.launch(intent);
                dialogInterface.dismiss();
            })
            .setNegativeButton(getResources().getString(R.string.circle_picker_activity_cancel), (DialogInterface dialogInterface, int i)-> {
                dialogInterface.dismiss();
                Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            })
            .setCancelable(false);
        builder.create().show();
    }

    private void SaveAllColorsToFile(){
        boolean resultWriteExternal = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean resultReadExternal = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(!resultReadExternal && !resultWriteExternal){
            saveColorsToFilesPermission.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
        }else if(!resultReadExternal){
            saveColorsToFilesPermission.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        }else if(!resultWriteExternal){
            saveColorsToFilesPermission.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }else{
            new SaveColorsToFile(getActivity());
        }
    }

    private void setupPaletteRecycleView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy >= 0){
                    // Scrolling up
                    actionMenu.show();
                }else{
                    // Scrolling down
                    actionMenu.hide(false);
                }
            }
        });
        colorsTabRVAdapter = new ColorsTabRVAdapter(getActivity());
        recyclerView.setAdapter(colorsTabRVAdapter);
    }

    public void showTutorial(){
        tutorial.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void showColors() {
        tutorial.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public FloatingActionButton getActionMenu(){
        return this.actionMenu;
    }

    public RecyclerView getRecycleView(){
        return this.recyclerView;
    }

    public ColorsTabRVAdapter getColorsTabRVAdapter(){
        return this.colorsTabRVAdapter;
    }

    public static class Instance{
        private static ColorsTab colorsTab_;

        public static void set(ColorsTab colorsTab){
            colorsTab_=colorsTab;
        }

        public static ColorsTab get(){
            return colorsTab_;
        }
    }
}