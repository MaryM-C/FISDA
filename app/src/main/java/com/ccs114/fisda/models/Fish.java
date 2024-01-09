package com.ccs114.fisda.models;

public class Fish {
    private String commonName;
    private String englishName;
    private String localName;

    private String horizontalImg;
    private String verticalImg;
    private String img1;
    private String img2;
    private String img3;
    private String optLength;
    private String optWeight;
    private String maxLength;
    private String maxWeight;
    private String tClass;
    private String genus;
    private String family;
    private String order;
    private String sciName;
    private String category;
    private String diet;
    private String edibility;
    private String environment;
    private String shortDescription;
    private String temperature;
    private String fishStatus;

    public Fish() {
        // Default constructor required for Firebase
    }

    public String getCommonName() {
        return commonName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getLocalName() {
        return localName;
    }


    public String getHorizontalImg() {
        return horizontalImg;
    }

    public String getVerticalImg() {
        return verticalImg;
    }
    public String getImg1() {
        return img1;
    }

    public String getImg2() {
        return img2;
    }

    public String getImg3() {
        return img3;
    }

    public String getOptLength() {
        return optLength;
    }

    public String getOptWeight() {
        return optWeight;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public String getMaxWeight() {
        return maxWeight;
    }

    public String gettClass() {
        return tClass;
    }

    public String getGenus() {
        return genus;
    }

    public String getFamily() {
        return family;
    }

    public String getOrder() {
        return order;
    }

    public String getSciName() {
        return sciName;
    }

    public String getCategory() {
        return category;
    }

    public String getDiet() {
        return diet;
    }

    public String getEdibility() {
        return edibility;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getFishStatus() {
        return fishStatus;
    }


}
