package com.ccs114.fisda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class InformationFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_layout);

        String commonName = getIntent().getStringExtra("commonname");

        // Set the retrieved values to the respective TextViews and ImageView
        ImageView imageView = findViewById(R.id.mainImage);
        TextView commonNameTextView = findViewById(R.id.lblCommonName);
        TextView localNameTextView = findViewById(R.id.lblLocalName);
        TextView categoryTextView = findViewById(R.id.lblCategory);
        TextView englishNameTextView = findViewById(R.id.lblEnglish);
        TextView edibilityTextView = findViewById(R.id.lblEdibility);
        TextView classTextView = findViewById(R.id.lblClass);
        TextView orderTextView = findViewById(R.id.lblOrder);
        TextView familyTextView = findViewById(R.id.lblFamily);
        TextView genusTextView = findViewById(R.id.lblGenus);
        TextView scientificnameTextView = findViewById(R.id.lblScientificName);
        TextView sdrescriptionTextView = findViewById(R.id.shortDescription);
        TextView mlengthTextView = findViewById(R.id.lblMaxLength);
        TextView mweightTextView = findViewById(R.id.lblMaxWeight);
        TextView sizeView = findViewById(R.id.lblSize);
        TextView environmentView = findViewById(R.id.lblEnvironment);
        TextView tempTextView = findViewById(R.id.lblTemperature);
        TextView dietTextView = findViewById(R.id.lblDiet);


        FishDataManager fishDataManager = new FishDataManager();
        fishDataManager.getFishData(commonName, new FishDataManager.FishDataListener() {
            @Override
            public void onFishDataLoaded(Fish fish) {
                commonNameTextView.setText(fish.getCommonName());
                localNameTextView.setText(fish.getLocalName());
                categoryTextView.setText(fish.getCategory());
                englishNameTextView.setText(fish.getEnglishName());
                edibilityTextView.setText(fish.getEdibility());
                classTextView.setText(fish.getTClass());
                orderTextView.setText(fish.getOrder());
                familyTextView.setText(fish.getFamily());
                genusTextView.setText(fish.getGenus());
                scientificnameTextView.setText(fish.getScientificName());
                sdrescriptionTextView.setText(fish.getShortDescription());
                mlengthTextView.setText(fish.getMaxLength());
                mweightTextView.setText(fish.getMaxWeight());
                sizeView.setText(fish.getSize());
                environmentView.setText(fish.getEnvironment());
                tempTextView.setText(fish.getTemperature());
                dietTextView.setText(fish.getDiet());


            }

            @Override
            public void onFishDataNotFound() {
                Toast.makeText(getApplicationContext(), "Fish data not found.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFishDataError(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Fish data not found."+ errorMessage, Toast.LENGTH_SHORT).show();
            }
        });



        //lblCommonNameTextView.setText(commonName);
        //localNameTextView.setText(localName);
        //categoryTextView.setText(category);

        // Load the image using Picasso from the web
        //Picasso.get().load(image).resize(250, 250).into(imageView);

    }
}