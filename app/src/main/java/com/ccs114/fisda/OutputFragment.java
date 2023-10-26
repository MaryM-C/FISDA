package com.ccs114.fisda;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.databinding.DataBindingUtil;
import com.ccs114.fisda.databinding.FragmentOutputBinding;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.squareup.picasso.Picasso;

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
            String imagePath = args.getString("imagepath");
            String[] topFishSpecies = args.getStringArray("topFishSpecies");
            String[] topConfidences = args.getStringArray("topConfidences");
            Boolean savedImage = args.getBoolean("Saved");
            Boolean notFishImage = args.getBoolean("isNotFish");

            if(savedImage) {
                bindData.btnSave.setVisibility(View.INVISIBLE);
            }


            if(notFishImage) {
                hideButtonResults();
                showDefaultImage();
                bindData.setShowRetake(true);
            } else {
                //button
                bindData.btnResultOne.setEnabled(false);
                //Image from the user
                displayImage(bindData.imgInputFish, bindData.imgFishSpecies);
                displayFishInfo(topFishSpecies[0], topConfidences[0]);
                bindData.setShowDescription(true);
            }




            bindData.btnResultOne.setOnClickListener(view1 -> {
                displayFishInfo(topFishSpecies[0], topConfidences[0]);
                bindData.btnResultOne.setEnabled(false);

                bindData.btnResultTwo.setEnabled(true);
                bindData.btnResultThree.setEnabled(true);

            });
            bindData.btnResultTwo.setOnClickListener(view12 -> {
               displayFishInfo(topFishSpecies[1], topConfidences[1]);
                bindData.btnResultTwo.setEnabled(false);

                bindData.btnResultOne.setEnabled(true);
                bindData.btnResultThree.setEnabled(true);
            });
            bindData.btnResultThree.setOnClickListener(view13 -> {
                displayFishInfo(topFishSpecies[2], topConfidences[2]);
                bindData.btnResultThree.setEnabled(false);

                bindData.btnResultOne.setEnabled(true);
                bindData.btnResultTwo.setEnabled(true);
            });

            bindData.btnBack.setOnClickListener(view14 -> {
//                CaptureFragment captureFragment = new CaptureFragment();
//                FragmentManager manager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.replace(R.id.container, captureFragment);
//                transaction.commit();
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                manager.popBackStack();

            });

            bindData.imgInputFish.setOnClickListener(view15 -> imagePopup.viewPopup());

            bindData.btnSave.setOnClickListener(view16 -> {
                CollectionsDbHelper dbHelper = new CollectionsDbHelper(getContext());
                dbHelper.addFishData(imagePath, imageFileName, topFishSpecies, topConfidences);

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
        byte[] byteArray = args.getByteArray("imagebytes");
        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

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

                bindData.setConfidence(confidence + "%");
                bindData.description.setEnglishName(fishName);

                Log.d("OutputContent", "Fish name: " + fishName + " confidence: " + confidence + "%" );

                displayBasicInfo(bindData, fish);
                displayTaxonomy(bindData, fish);

                bindData.description.setShortDescription(fish.getShortDescription());

                displayBioInfo(bindData, fish);

                Picasso.get().load(fish.getMainImage()).into(bindData.imgFishSpecies);


                //More Images
                byte[] byteArray = args.getByteArray("imagebytes");
                Drawable image = new BitmapDrawable(getResources(),BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                imagePopup.setWindowHeight(750); // Optional
                imagePopup.setWindowWidth(900); // Optional
                imagePopup.initiatePopup(image);
                Picasso.get().load(fish.getImg1()).into(bindData.description.imgMImages1);
                Picasso.get().load(fish.getImg2()).into(bindData.description.imgMImages2);
                Picasso.get().load(fish.getImg3()).into(bindData.description.imgMImages3);

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

    private void displayImage(ImageView imageView, ImageView refImage) {
        byte[] byteArray = args.getByteArray("imagebytes");
        if (byteArray != null) {
            Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            // Calculate the scaling factors for width and height
            float scaleX = (float) 900 / image.getWidth();
            float scaleY = (float) 750 / image.getHeight();

            float scale = Math.min(scaleX, scaleY);

            // Create a matrix for the scaling transformation
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            // Create a new Bitmap with the desired dimensions
            Bitmap resizedImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

            imageView.setImageBitmap(resizedImage);
//            imagePopup.setWindowHeight(resizedImage.getHeight()); // Optional
//            imagePopup.setWindowWidth(resizedImage.getWidth()); // Optional
            Drawable imgDrawable = new BitmapDrawable(resizedImage);
            bindData.imgInputFish.setImageDrawable(imgDrawable);
        }

    }
}