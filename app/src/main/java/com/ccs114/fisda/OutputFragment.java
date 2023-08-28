package com.ccs114.fisda;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class OutputFragment extends Fragment {
    FishDataManager fishDataManager = new FishDataManager();
    private ImageView imageView;
    private TextView englishName, comName, localName,edibility;
    private TextView confidenceView;

    private Button topOne;

    private Button topTwo;

    private Button topThree;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_output, container, false);

        // Retrieve data from the arguments bundle
        Bundle args = getArguments();
        if (args != null) {
            String imagePath = args.getString("imagePath");
            String[] topFishSpecies = args.getStringArray("topFishSpecies");
            String[] topConfidences = args.getStringArray("topConfidences");

            // Find the ImageView and TextView by their IDs
            imageView = (ImageView) view.findViewById(R.id.imgInputFish);
            englishName = view.findViewById(R.id.txtEngName);
            comName = view.findViewById(R.id.txtComName);
            localName = view.findViewById(R.id.txtLocName);
            edibility = view.findViewById(R.id.txtEdibility);
            confidenceView = view.findViewById(R.id.lblConfidence);
            topOne = view.findViewById(R.id.btnResultOne);
            topTwo = view.findViewById(R.id.btnResultTwo);
            topThree = view.findViewById(R.id.btnResultThree);


            displayImage(imageView, args);


            fishDataManager.getFishData(topFishSpecies[0], new FishDataManager.FishDataListener() {
                public void onFishDataLoaded(Fish fish) {
                    comName.setText(topFishSpecies[0]);
                    confidenceView.setText(topConfidences[0] + "%");

                    englishName.setText(fish.getEnglishName());
                    localName.setText(fish.getLocalName());
                    edibility.setText(fish.getEdibility());
                }
                public void onFishDataNotFound() {
                    Toast.makeText(getContext(), "Fish data not found.", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFishDataError(String errorMessage) {
                    Toast.makeText(getContext(), "Fish data not found." + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

            topOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fishDataManager.getFishData(topFishSpecies[0], new FishDataManager.FishDataListener() {
                        public void onFishDataLoaded(Fish fish) {
                            comName.setText(topFishSpecies[0]);
                            confidenceView.setText(topConfidences[0] + "%");

                            englishName.setText(fish.getEnglishName());
                            localName.setText(fish.getLocalName());
                            edibility.setText(fish.getEdibility());
                        }
                        public void onFishDataNotFound() {
                            Toast.makeText(getContext(), "Fish data not found.", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFishDataError(String errorMessage) {
                            Toast.makeText(getContext(), "Fish data not found." + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                    
                }
            });
            topTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fishDataManager.getFishData(topFishSpecies[1], new FishDataManager.FishDataListener() {
                        public void onFishDataLoaded(Fish fish) {
                            comName.setText(topFishSpecies[1]);
                            confidenceView.setText(topConfidences[1] + "%");

                            englishName.setText(fish.getEnglishName());
                            localName.setText(fish.getLocalName());
                            edibility.setText(fish.getEdibility());
                        }
                        public void onFishDataNotFound() {
                            Toast.makeText(getContext(), "Fish data not found.", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFishDataError(String errorMessage) {
                            Toast.makeText(getContext(), "Fish data not found." + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });
            topThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fishDataManager.getFishData(topFishSpecies[2], new FishDataManager.FishDataListener() {
                        public void onFishDataLoaded(Fish fish) {
                            comName.setText(topFishSpecies[2]);
                            confidenceView.setText(topConfidences[2] + "%");

                            englishName.setText(fish.getEnglishName());
                            localName.setText(fish.getLocalName());
                            edibility.setText(fish.getEdibility());
                        }
                        public void onFishDataNotFound() {
                            Toast.makeText(getContext(), "Fish data not found.", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFishDataError(String errorMessage) {
                            Toast.makeText(getContext(), "Fish data not found." + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


        return view;
    }

    private void displayImage(ImageView imageView, Bundle args) {
        byte[] byteArray = args.getByteArray("imagebytes");
        if(byteArray != null) {
            Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(image);
        }
    }






}