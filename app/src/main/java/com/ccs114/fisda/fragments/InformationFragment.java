package com.ccs114.fisda.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Toast;

import com.ccs114.fisda.manager.FishDataManager;
import com.ccs114.fisda.R;
import com.ccs114.fisda.databinding.InformationLayoutBinding;
import com.ccs114.fisda.models.Fish;
import com.codebyashish.autoimageslider.Enums.ImageScaleType;
import com.codebyashish.autoimageslider.ExceptionsClass;
import com.codebyashish.autoimageslider.Models.ImageSlidesModel;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class InformationFragment extends AppCompatActivity{
    InformationLayoutBinding bindData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindData = DataBindingUtil.setContentView(this, R.layout.information_layout);
        String commonName = getIntent().getStringExtra("commonname");

        FishDataManager fishDataManager = new FishDataManager();
        fishDataManager.getFishData(commonName, new FishDataManager.FishDataListener() {
            @Override
            public void onFishDataLoaded(Fish fish) {
                Picasso.get().load(fish.getMainImage()).fit().into(bindData.mainImage);
                displayBasicInfo(bindData, fish);
                displayTaxonomy(bindData, fish);
                bindData.scrlInfo.setShortDescription(fish.getShortDescription());
                displayBioInfo(bindData, fish);

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
                //TODO: Change the ugly animation
                bindData.scrlInfo.resultSlider.setDefaultAnimation();
            }

            @Override
            public void onFishDataNotFound() {
                Toast.makeText(getApplicationContext(), "Fish data not found.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFishDataError(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Fish data not found."+ errorMessage, Toast.LENGTH_SHORT).show();
            }

            private void displayBasicInfo(InformationLayoutBinding bindData, Fish fish) {
                bindData.scrlInfo.setEnglishName(fish.getEnglishName());
                bindData.scrlInfo.setLocalName(fish.getLocalName());
                bindData.scrlInfo.setCommonName(fish.getCommonName());
                bindData.scrlInfo.setEdibility(fish.getEdibility());
                bindData.scrlInfo.setCategory(fish.getCategory());
            }

            private void displayTaxonomy(InformationLayoutBinding bindData, Fish fish) {
                bindData.scrlInfo.setVarClass(fish.getTClass());
                bindData.scrlInfo.setOrder(fish.getOrder());
                bindData.scrlInfo.setFamily(fish.getFamily());
                bindData.scrlInfo.setGenus(fish.getGenus());
                bindData.scrlInfo.setSciName(fish.getScientificName());
            }

            private void displayBioInfo(InformationLayoutBinding bindData, Fish fish) {
                bindData.scrlInfo.setMaxLength(fish.getMaxLength());
                bindData.scrlInfo.setMaxWeight(fish.getMaxWeight());
                bindData.scrlInfo.setSize(fish.getSize());
                bindData.scrlInfo.setEnvironment(fish.getEnvironment());
                bindData.scrlInfo.setTemperature(fish.getTemperature());
                bindData.scrlInfo.setDiet(fish.getDiet());
            }
        });

        bindData.btnBack.setOnClickListener(view1 -> finish());

    }
}