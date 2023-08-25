package com.ccs114.fisda;

import java.util.Locale;

/**
 * This file contains the 'OutputHandler' class, which processes confidence values for fish species
 * classification, providing methods to identify the highest confidence prediction and obtain the top
 * predicted fish species.
 */


public class OutputHandler {
    private final float[] confidence;
    private static final String[] fishSpeciesNames = {"Big Head Carp", "Blackchin Tilapia", "Carp", "Catfish", "Climbing Perch",
            "Freshwater Eel", "Goby", "Gold Fish", "Gourami", "Indian Carp", "Indio-Pacific Tarpon",
            "Jaguar Guapote", "Janitor fish", "Knife fish", "Manila Catfish", "Milkfish",
            "Mosquito Fish", "Mudfish", "Mullet", "Scat Fish", "Silver Barb", "Silver Carp",
            "Silver Perch", "Tenpounder", "Tilapia"};

    public OutputHandler(float[] confidence) {
        this.confidence = confidence;
    }

    // return the index of the fish species with the highest confidence
    public int getMaxIndex() {
        int maxIndex = 0;
        float maxValue = confidence[0];

        // Loop through confidence values to find the maximum
        for (int i = 1; i < confidence.length; i++) {
            if (confidence[i] > maxValue) { // Check if the current confidence is higher than the previous maximum
                maxIndex = i; // Update the index of the fish species with the highest confidence
                maxValue = confidence[i]; // Update the maximum confidence value
            }
        }
        return maxIndex;

    }

    //Returns maximum confidence value
    public float getMaximumValue() {
        float maxValue = confidence[0];

        // Loop through confidence values to find the maximum
        for (int i = 1; i < confidence.length; i++) {
            if (confidence[i] > maxValue) { // Check if the current confidence is higher than the previous maximum
                maxValue = confidence[i]; // Update the maximum confidence value
            }
        }
        return maxValue;
    }

    //Returns the indices of the top 3 predicted fish species
    //TODO : Optimize this code
    public int[] getTop3Predictions() {
        int[] topIndices = new int[3]; // Fish Species
        float[] topValues = new float[3]; // Confidence Value

        for (int i = 0; i < confidence.length; i++) {
            for (int j = 0; j < topValues.length; j++) {
                // Check if the current confidence is higher than the current top value
                if (confidence[i] > topValues[j]) {
                    for (int k = topValues.length - 1; k > j; k--) {
                        // Shift values to make room for the new top value
                        topValues[k] = topValues[k - 1];
                        topIndices[k] = topIndices[k - 1];
                    }
                    topValues[j] = confidence[i];
                    topIndices[j] = i;
                    break;
                }
            }
        }
        return topIndices;
    }

    //Returns the names of the top 3 predicted fish species
    public String[] getTop3FishSpecies() {
        int[] topIndices = getTop3Predictions();
        String[] topFishSpecies = new String[3];
        for (int i = 0; i < topFishSpecies.length; i++) {
            topFishSpecies[i] = fishSpeciesNames[topIndices[i]];
        }
        return topFishSpecies;
    }

    //Returns the confidence value of the top 3 predicted fish species
    private float[] getTop3Confidences() {
        int[] topIndices = getTop3Predictions();
        float[] topConfidences = new float[3];
        for (int i = 0; i < topConfidences.length; i++) {
            topConfidences[i] = confidence[topIndices[i]];
        }
        return topConfidences;
    }

    //Returns a formatted confidence value with only 2 decimals
    public String[] getConfidences() {
        int[] topIndices = getTop3Predictions();
        String[] formattedConfidences = new String[3];

        for (int i = 0; i < formattedConfidences.length; i++) {
            float confidenceValue = confidence[topIndices[i]] * 100; // Multiply by 100 for percentage
            formattedConfidences[i] = String.format(Locale.getDefault(), "%.2f", confidenceValue); // Format to two decimal places
        }

        return formattedConfidences;
    }
}
