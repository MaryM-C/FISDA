package com.ccs114.fisda.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.ccs114.fisda.databinding.FragmentOutputBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ccs114.fisda.manager.FishDataManager;
import com.ccs114.fisda.R;
import com.ccs114.fisda.database.CollectionsDbHelper;
import com.ccs114.fisda.models.Fish;
//import com.ccs114.fisda.utils.FishInfoHelper;
import com.ccs114.fisda.utils.FishInfoHelper;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.codebyashish.autoimageslider.Enums.ImageScaleType;
import com.codebyashish.autoimageslider.ExceptionsClass;
import com.codebyashish.autoimageslider.Models.ImageSlidesModel;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class OutputFragment extends Fragment {
    FishDataManager fishDataManager = new FishDataManager();
    Bundle args;
    ImagePopup imagePopup;
    FragmentOutputBinding bindData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bindData = DataBindingUtil.inflate(inflater, R.layout.fragment_output,  container, false);

        View view = bindData.getRoot();
        args = getArguments();


        // Retrieve data from the arguments bundle
        if (args != null) {
            String imageFileName = args.getString("filename");
            String imagePath = args.getString("imagePath");
            String imageUri = args.getString("uri");
            String[] topFishSpecies = args.getStringArray("topFishSpecies");
            String[] topConfidences = args.getStringArray("topConfidences");
            boolean savedImage = args.getBoolean("Saved");
            boolean notFishImage = args.getBoolean("isNotFish");

            if(savedImage) {
                bindData.btnSave.setVisibility(View.INVISIBLE);
            }


            if(notFishImage) {
                //Todo when image is not fish enough
                hideButtonResults();
                showDefaultImage();
                bindData.setShowRetake(true);
            } else {
                //initial
                displayImage(bindData.imgInputFish);
                displayFishInfo(topFishSpecies[0], topConfidences[0]);
                bindData.setShowDescription(true);
            }

            bindData.btnResultOne.setOnClickListener(view12 ->
                    displayFishInfo(topFishSpecies[0], topConfidences[0]));

            bindData.btnResultTwo.setOnClickListener(view12 ->
                    displayFishInfo(topFishSpecies[1], topConfidences[1]));

            bindData.btnResultThree.setOnClickListener(view12 ->
                    displayFishInfo(topFishSpecies[2], topConfidences[2]));

            bindData.btnBack.setOnClickListener(view1 -> {
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                manager.popBackStack();

            });
            bindData.description.btnMoreInfo.setOnClickListener(view1 -> openDialog());
            bindData.description.btnMoreInfo2.setOnClickListener(view1 -> openOptimumDialog());
            bindData.description.btnMoreInfo3.setOnClickListener(view1 -> openMaximumDialog());

            //bindData.imgInputFish.setOnClickListener(view15 -> imagePopup.viewPopup());

            bindData.btnSave.setOnClickListener(view1 -> {
                CollectionsDbHelper dbHelper = new CollectionsDbHelper(getContext());
                dbHelper.addFishData(imagePath, imageUri, imageFileName, topFishSpecies, topConfidences);
                dbHelper.close();

                CaptureFragment captureFragment = new CaptureFragment();
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, captureFragment);
                transaction.commit();


            });
        }



        return view;
    }

    private void openOptimumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.optimal_size)
                .setMessage(R.string.optimal_size_text)
                .setPositiveButton(R.string.close, ((dialogInterface, i) -> {

                }));
        builder.create();
        builder.show();
    }

    private void openMaximumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.maximum_size)
                .setMessage(R.string.maximum_size_text)
                .setPositiveButton(R.string.close, ((dialogInterface, i) -> {

                }));
        builder.create();
        builder.show();
    }

    private void openDialog() {
        MoreInfoDialog dialogFragment = new MoreInfoDialog();
        dialogFragment.show(requireActivity().getFragmentManager(), "More Info");
    }

    private void showDefaultImage() {
        String imagePaths = args.getString("imagePath");
        Bitmap image = BitmapFactory.decodeFile(imagePaths);

        bindData.imgInputFish.setImageBitmap(image);
        bindData.imgFishSpecies.setImageBitmap(image);
    }

    private void hideButtonResults() {
        bindData.btnSave.setVisibility(View.INVISIBLE);
        bindData.btnResultOne.setVisibility(View.INVISIBLE);
        bindData.btnResultTwo.setVisibility(View.INVISIBLE);
        bindData.btnResultThree.setVisibility(View.INVISIBLE);

        bindData.description.getRoot().setVisibility(View.INVISIBLE);

        bindData.lblConfidence.setVisibility(View.INVISIBLE);
        bindData.textView.setVisibility(View.INVISIBLE);
    }

    private void displayFishInfo(String fishName, String confidence) {
        FishInfoHelper fishInfoHelper = new FishInfoHelper();
        fishInfoHelper.displayAllInfo(
                getContext(),
                bindData,
                fishName,
                confidence);
    }

    private void displayImage(ImageView imageView) {
        String imagepath = args.getString("imagePath");
        Bitmap image = BitmapFactory.decodeFile(imagepath);
        imageView.setImageBitmap(image);
    }
}