package com.ccs114.fisda;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

public class OutputFragment extends Fragment {
    FishDataManager fishDataManager = new FishDataManager();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentOutputBinding bindData =
                DataBindingUtil.inflate(inflater, R.layout.fragment_output,  container, false);

        View view = bindData.getRoot();

        // Retrieve data from the arguments bundle
        Bundle args = getArguments();
        if (args != null) {
            String imagePath = args.getString("imagePath");
            String[] topFishSpecies = args.getStringArray("topFishSpecies");
            String[] topConfidences = args.getStringArray("topConfidences");

            //button
            bindData.btnResultOne.setEnabled(false);
            //Image from the user
            displayImage(bindData.imgInputFish, bindData.imgFishSpecies, args);

            //TODO Add the image from the Firebase here


            displayFishInfo(bindData, topFishSpecies[0], topConfidences[0]);
//

            bindData.btnResultOne.setOnClickListener(view1 -> {
                displayFishInfo(bindData, topFishSpecies[0], topConfidences[0]);
                bindData.btnResultOne.setEnabled(false);

                bindData.btnResultTwo.setEnabled(true);
                bindData.btnResultThree.setEnabled(true);

            });
            bindData.btnResultTwo.setOnClickListener(view12 -> {
               displayFishInfo(bindData, topFishSpecies[1], topConfidences[1]);
                bindData.btnResultTwo.setEnabled(false);

                bindData.btnResultOne.setEnabled(true);
                bindData.btnResultThree.setEnabled(true);
            });
            bindData.btnResultThree.setOnClickListener(view13 -> {
                displayFishInfo(bindData, topFishSpecies[2], topConfidences[2]);
                bindData.btnResultThree.setEnabled(false);

                bindData.btnResultOne.setEnabled(true);
                bindData.btnResultTwo.setEnabled(true);
            });

            bindData.btnBack.setOnClickListener(view14 -> {
                CaptureFragment captureFragment = new CaptureFragment();
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, captureFragment);
                transaction.commit();
            });
        }


        return view;
    }

    private void displayFishInfo(FragmentOutputBinding bindData, String fishName, String confidence) {
        fishDataManager.getFishData(fishName, new FishDataManager.FishDataListener() {
            public void onFishDataLoaded(Fish fish) {

                bindData.setConfidence(confidence + "%");
                bindData.scrlInfo.setEnglishName(fishName);

                displayBasicInfo(bindData, fish);
                displayTaxonomy(bindData, fish);

                bindData.scrlInfo.setShortDescription(fish.getShortDescription());

                displayBioInfo(bindData, fish);

                Picasso.get().load(fish.getMainImage()).into(bindData.imgFishSpecies);

                //More Images
                Picasso.get().load(fish.getImg1()).into(bindData.scrlInfo.imgMImages1);
                Picasso.get().load(fish.getImg2()).into(bindData.scrlInfo.imgMImages2);
                Picasso.get().load(fish.getImg3()).into(bindData.scrlInfo.imgMImages3);

            }
            public void onFishDataNotFound() {
                Toast.makeText(getContext(), "Fish data not found.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFishDataError(String errorMessage) {
                Toast.makeText(getContext(), "Fish data not found." + errorMessage, Toast.LENGTH_SHORT).show();
            }

            private void displayBasicInfo(FragmentOutputBinding bindData, Fish fish) {
                bindData.scrlInfo.setLocalName(fish.getLocalName());
                bindData.scrlInfo.setCommonName(fish.getCommonName());
                bindData.scrlInfo.setEdibility(fish.getEdibility());
                bindData.scrlInfo.setCategory(fish.getCategory());
            }

            private void displayTaxonomy(FragmentOutputBinding bindData, Fish fish) {
                bindData.scrlInfo.setVarClass(fish.getTClass());
                bindData.scrlInfo.setOrder(fish.getOrder());
                bindData.scrlInfo.setFamily(fish.getFamily());
                bindData.scrlInfo.setGenus(fish.getGenus());
                bindData.scrlInfo.setSciName(fish.getScientificName());
            }

            private void displayBioInfo(FragmentOutputBinding bindData, Fish fish) {
                bindData.scrlInfo.setMaxLength(fish.getMaxLength());
                bindData.scrlInfo.setMaxWeight(fish.getMaxWeight());
                bindData.scrlInfo.setSize(fish.getSize());
                bindData.scrlInfo.setEnvironment(fish.getEnvironment());
                bindData.scrlInfo.setTemperature(fish.getTemperature());
                bindData.scrlInfo.setDiet(fish.getDiet());
            }
        });
    }

    private void displayImage(ImageView imageView, ImageView refImage,  Bundle args) {
        byte[] byteArray = args.getByteArray("imagebytes");
        if(byteArray != null) {
            Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            image = ThumbnailUtils.extractThumbnail(image, 150, 90);
            imageView.setImageBitmap(image);
            //TODO
            /*delete if the image from the Firebase is added*/
//            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(image, 370, 200);
//            refImage.setImageBitmap(thumbnail);


        }
    }


}