package com.ccs114.fisda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class InformationFragment extends AppCompatActivity {
    TextView commonName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_layout);

        // Retrieve the values from the intent
        String commonName = getIntent().getStringExtra("commonname");
        String localName = getIntent().getStringExtra("localname");
        String category = getIntent().getStringExtra("category");
        String image = getIntent().getStringExtra("image");



        // Set the retrieved values to the respective TextViews and ImageView
        TextView lblCommonNameTextView = findViewById(R.id.lblCommonName);
        TextView localNameTextView = findViewById(R.id.lblLocalName);
        TextView categoryTextView = findViewById(R.id.lblCategory);
        ImageView imageView = findViewById(R.id.image2);


        lblCommonNameTextView.setText(commonName);
        localNameTextView.setText(localName);
        categoryTextView.setText(category);

        // Load the image using Picasso from the web
        Picasso.get().load(image).resize(250, 250).into(imageView);

    }
}