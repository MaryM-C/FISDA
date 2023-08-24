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

import androidx.fragment.app.Fragment;

public class OutputFragment extends Fragment {
    private ImageView imageView;
    private TextView resultTextView;
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
            resultTextView = view.findViewById(R.id.txtEngName);
            confidenceView = view.findViewById(R.id.lblConfidence);
            topOne = view.findViewById(R.id.btnResultOne);
            topTwo = view.findViewById(R.id.btnResultTwo);
            topThree = view.findViewById(R.id.btnResultThree);


            displayImage(imageView, args);

            resultTextView.setText(topFishSpecies[0]);
            confidenceView.setText(topConfidences[0] + "%");

            topOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultTextView.setText(topFishSpecies[0]);
                    confidenceView.setText(topConfidences[0] + "%");
                }
            });
            topTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultTextView.setText(topFishSpecies[1]);
                    confidenceView.setText(topConfidences[1] + "%");
                }
            });
            topThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultTextView.setText(topFishSpecies[2]);
                    confidenceView.setText(topConfidences[2] + "%");
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