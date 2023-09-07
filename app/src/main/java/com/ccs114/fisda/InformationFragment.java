package com.ccs114.fisda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.ccs114.fisda.databinding.InformationLayoutBinding;
import com.squareup.picasso.Picasso;

public class InformationFragment extends AppCompatActivity {
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

                displayBasicInfo(bindData, fish);
                displayTaxonomy(bindData, fish);
                bindData.scrlInfo.setShortDescription(fish.getShortDescription());
                displayBioInfo(bindData, fish);

                Picasso.get().load(fish.getMainImage()).into(bindData.mainImage);

                //More Images
                Picasso.get().load(fish.getImg1()).into(bindData.scrlInfo.imgMImages1);
                Picasso.get().load(fish.getImg2()).into(bindData.scrlInfo.imgMImages2);
                Picasso.get().load(fish.getImg3()).into(bindData.scrlInfo.imgMImages3);
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

        bindData.btnBack.setOnClickListener(view14 -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });


        // Load the image using Picasso from the web
        //Picasso.get().load(image).resize(250, 250).into(imageView);

    }
}