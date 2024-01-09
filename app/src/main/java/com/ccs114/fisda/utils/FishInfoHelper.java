package com.ccs114.fisda.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.ccs114.fisda.databinding.DescriptionLayoutBinding;
import com.ccs114.fisda.databinding.FragmentOutputBinding;
import com.ccs114.fisda.databinding.InformationLayoutBinding;
import com.ccs114.fisda.manager.FishDataManager;
import com.ccs114.fisda.models.Fish;
import com.codebyashish.autoimageslider.Enums.ImageAnimationTypes;
import com.codebyashish.autoimageslider.Enums.ImageScaleType;
import com.codebyashish.autoimageslider.ExceptionsClass;
import com.codebyashish.autoimageslider.Models.ImageSlidesModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;



public class FishInfoHelper {
    FishDataManager fishDataManager;

    public FishInfoHelper() {
        this.fishDataManager = new FishDataManager();
    }

    public void displayAllInfo(Context context, InformationLayoutBinding bindData, String fishName) {
        fishDataManager.getFishData(fishName, new FishDataManager.FishDataListener() {
            @Override
            public void onFishDataLoaded(Fish fish) {
                displayInfo(bindData, fish);
            }

            @Override
            public void onFishDataNotFound() {
                Toast.makeText(context, "Fish data not found.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFishDataError(String errorMessage) {
                Toast.makeText(context, "Fish data not found." + errorMessage, Toast.LENGTH_SHORT).show();
            }


        });
    }
    public void displayAllInfo(Context context, FragmentOutputBinding bindData, String fishName, String confidence) {
        fishDataManager.getFishData(fishName, new FishDataManager.FishDataListener() {
            @Override
            public void onFishDataLoaded(Fish fish) {
                displayInfo(bindData, fish, confidence);
            }

            @Override
            public void onFishDataNotFound() {
                Toast.makeText(context, "Fish data not found.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFishDataError(String errorMessage) {
                Toast.makeText(context, "Fish data not found." + errorMessage, Toast.LENGTH_SHORT).show();
            }


        });
    }
    private void displayNames(DescriptionLayoutBinding bindData, Fish fish) {
        bindData.setEnglishName(fish.getEnglishName());
        bindData.setLocalName(fish.getLocalName());
        bindData.setCommonName(fish.getCommonName());
    }

    private void displaySize(DescriptionLayoutBinding bindData, Fish fish) {
        bindData.setMaxLength(fish.getMaxLength());
        bindData.setMaxWeight(fish.getMaxWeight());
        bindData.setOptLength(fish.getOptLength());
        bindData.setOptWeight(fish.getOptWeight());
    }

    private void displayClassification(DescriptionLayoutBinding bindData, Fish fish) {
        bindData.setVarClass(fish.gettClass());
        bindData.setGenus(fish.getGenus());
        bindData.setFamily(fish.getFamily());
        bindData.setOrder(fish.getOrder());
        bindData.setSciName(fish.getSciName());
    }

    private void displayProfile(DescriptionLayoutBinding bindData, Fish fish) {
        bindData.setCategory(fish.getCategory());
        bindData.setDiet(fish.getDiet());
        bindData.setEdibility(fish.getEdibility());
        bindData.setEnvironment(fish.getEnvironment());
        bindData.setShortDescription(fish.getShortDescription());
        bindData.setTemperature(fish.getTemperature());
        bindData.setStatus(fish.getFishStatus());
    }

    private void displayImages(FragmentOutputBinding bindData, Fish fish) {
        Picasso.get().load(fish.getHorizontalImg()).fit().into(bindData.imgFishSpecies);

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
        bindData.description.resultSlider.setSlideAnimation(ImageAnimationTypes.DEPTH_SLIDE);
    }

    private void displayImages(InformationLayoutBinding bindData, Fish fish) {
        Bitmap image = BitmapFactory.decodeFile(fish.getHorizontalImg());
        bindData.mainImage.setImageBitmap(image);
        Log.d("Horizontal", fish.getHorizontalImg() + " test");
        Picasso.get().load(fish.getHorizontalImg()).fit().into(bindData.mainImage, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("MainImage", "succ");
            }

            @Override
            public void onError(Exception e) {
                Log.d("MainImage", "fail");
            }
        });
        Picasso.get().setLoggingEnabled(true);


        //More Images
        ArrayList<ImageSlidesModel> resultsmodel = new ArrayList<>();
        try {
            resultsmodel.add(new ImageSlidesModel(fish.getImg1(), ""));
            resultsmodel.add(new ImageSlidesModel(fish.getImg2(), ""));
            resultsmodel.add(new ImageSlidesModel(fish.getImg3(), ""));

        } catch (ExceptionsClass e) {
            throw new RuntimeException(e);
        }
        bindData.scrlInfo.resultSlider.setImageList(resultsmodel, ImageScaleType.CENTER_CROP);
        bindData.scrlInfo.resultSlider.setSlideAnimation(ImageAnimationTypes.DEPTH_SLIDE);

    }

    private void displayConfidence(FragmentOutputBinding bindData, String confidence) {
        bindData.setConfidence(confidence + "%");
    }

    private void displayInfo(InformationLayoutBinding bindData, Fish fish) {
        displayNames(bindData.scrlInfo, fish);
        displayClassification(bindData.scrlInfo, fish);
        displayProfile(bindData.scrlInfo, fish);
        displaySize(bindData.scrlInfo, fish);
        displayImages(bindData, fish);
    }
    private void displayInfo(FragmentOutputBinding bindData, Fish fish, String confidence) {
        displayNames(bindData.description, fish);
        displayClassification(bindData.description, fish);
        displayProfile(bindData.description, fish);
        displaySize(bindData.description, fish);
        displayImages(bindData, fish);
        displayConfidence(bindData, confidence);
    }

}