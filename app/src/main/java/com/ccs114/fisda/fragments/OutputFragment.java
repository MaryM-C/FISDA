package com.ccs114.fisda.fragments;

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
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.codebyashish.autoimageslider.Enums.ImageScaleType;
import com.codebyashish.autoimageslider.ExceptionsClass;
import com.codebyashish.autoimageslider.Models.ImageSlidesModel;
import com.squareup.picasso.Picasso;

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
        imagePopup = new ImagePopup(getActivity());

        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional

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

            bindData.imgInputFish.setOnClickListener(view15 -> imagePopup.viewPopup());

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
        fishDataManager.getFishData(fishName, new FishDataManager.FishDataListener() {
            public void onFishDataLoaded(Fish fish) {
                Picasso.get().load(fish.getMainImage()).fit().into(bindData.imgFishSpecies);
                //More Images
                ArrayList<ImageSlidesModel> resultsmodel = new ArrayList<>();
                try {
                    resultsmodel.add(new ImageSlidesModel(fish.getImg1(), ""));
                    resultsmodel.add(new ImageSlidesModel(fish.getImg2(), ""));
                    resultsmodel.add(new ImageSlidesModel(fish.getImg3(), ""));

                } catch (ExceptionsClass e) {
                    throw new RuntimeException(e);
                }
                bindData.description.resultSlider.setImageList(resultsmodel, ImageScaleType.CENTER_CROP);
                //TODO: Change the ugly animation
                bindData.description.resultSlider.setDefaultAnimation();

                bindData.setConfidence(confidence + "%");
                bindData.description.setEnglishName(fishName);

                Log.d("OutputContent", "Fish name: " + fishName + " confidence: " + confidence + "%" );

                displayBasicInfo(bindData, fish);
                displayTaxonomy(bindData, fish);

                bindData.description.setShortDescription(fish.getShortDescription());

                displayBioInfo(bindData, fish);

                String imagepath= args.getString("imagePath");
                Drawable image = new BitmapDrawable(imagepath);

                imagePopup.setWindowHeight(750); // Optional
                imagePopup.setWindowWidth(900);
                imagePopup.initiatePopup(image);

            }
            public void onFishDataNotFound() {
                Toast.makeText(getContext(), "Fish data not found.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFishDataError(String errorMessage) {
                Toast.makeText(getContext(), "Fish data not found." + errorMessage, Toast.LENGTH_SHORT).show();
            }

            private void displayBasicInfo(FragmentOutputBinding bindData, Fish fish) {
                bindData.description.setLocalName(fish.getLocalName());
                bindData.description.setCommonName(fish.getCommonName());
                bindData.description.setEdibility(fish.getEdibility());
                bindData.description.setCategory(fish.getCategory());
            }

            private void displayTaxonomy(FragmentOutputBinding bindData, Fish fish) {
                bindData.description.setVarClass(fish.getTClass());
                bindData.description.setOrder(fish.getOrder());
                bindData.description.setFamily(fish.getFamily());
                bindData.description.setGenus(fish.getGenus());
                bindData.description.setSciName(fish.getScientificName());
            }

            private void displayBioInfo(FragmentOutputBinding bindData, Fish fish) {
                bindData.description.setMaxLength(fish.getMaxLength());
                bindData.description.setMaxWeight(fish.getMaxWeight());
                bindData.description.setSize(fish.getSize());
                bindData.description.setEnvironment(fish.getEnvironment());
                bindData.description.setTemperature(fish.getTemperature());
                bindData.description.setDiet(fish.getDiet());
            }
        });
    }

    private void displayImage(ImageView imageView) {
        String imagepath = args.getString("imagePath");

        Bitmap image = BitmapFactory.decodeFile(imagepath);

        float scaleX = (float) 900 / image.getWidth();
        float scaleY = (float) 750 / image.getHeight();

        float scale = Math.min(scaleX, scaleY);

        // Create a matrix for the scaling transformation
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new Bitmap with the desired dimensions
        Bitmap resizedImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

        imageView.setImageBitmap(resizedImage);

        Drawable imgDrawable = new BitmapDrawable(resizedImage);
        bindData.imgInputFish.setImageDrawable(imgDrawable);

    }
}