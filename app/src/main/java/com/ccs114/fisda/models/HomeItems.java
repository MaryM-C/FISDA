package com.ccs114.fisda.models;

import android.widget.ImageView;

public class MyItems {

    private final String CommonName, LocalName, Category, Image;


    public MyItems(String commonName, String localName, String category, String image) {
        CommonName = commonName;
        LocalName = localName;
        Category = category;
        Image = image;

    }

    public String getCommonName() {
        return CommonName;
    }

    public String getLocalName() {
        return LocalName;
    }

    public String getCategory() {
        return Category;
    }

    public String getImage() {
        return Image;
    }

}


