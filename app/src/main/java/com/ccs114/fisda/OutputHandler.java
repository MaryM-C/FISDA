package com.ccs114.fisda;

import java.util.Locale;
import java.util.TreeMap;

/**
 * The OutputHandler class processes confidence values for fish species classification and provides methods
 * to identify the highest confidence prediction and obtain information about the top predicted fish species.
 */
public class OutputHandler {

    private static float[] confidence ;
    private static final String[] fishSpeciesNames = {
            "Big Head Carp",
            "Blackchin Tilapia",
            "Carp",
            "Catfish",
            "Climbing Perch",
            "Freshwater Eel",
            "Goby",
            "Gold Fish",
            "Gourami",
            "Indian Carp",
            "Indo-Pacific Tarpon",
            "Jaguar Guapote",
            "Janitor Fish",
            "Knife Fish",
            "Manila Catfish",
            "Milkfish",
            "Mosquito Fish",
            "Mudfish",
            "Mullet",
            "Scat Fish",
            "Silver Barb",
            "Silver Carp",
            "Silver Perch",
            "Tenpounder",
            "Tilapia"};

    public float[] getConfidence() {
        return confidence;
    }

    public void setConfidence(float[] confidence) {
        this.confidence = confidence;
    }

    /**
     * Calculates and returns the highest confidence value from the given array.
     *
     * @param confidence An array of float values representing confidence levels
     * @return The highest confidence value from the provided array.
     */
    public float computeTopConfidence(float[] confidence) {
        float maxValue = confidence[0];

        for (int i = 1; i < confidence.length; i++) {
            if (confidence[i] > maxValue) {
                maxValue = confidence[i];
            }
        }

        return maxValue;
    }


    /**
     * Retrieves the indices of the top three predictions based on the confidence values.
     *
     * @param confidence An array of float values representing confidence levels
     * @return An array of integers containing the indices of the top three predictions.
     */
    public static int[] computeTopIndices(float[] confidence) {
        TreeMap<Float, Integer> sortedMap = new TreeMap<>();

        for (int i = 0; i < confidence.length; i++) {
            sortedMap.put(confidence[i], i);
        }

        int[] topIndices = new int[3];
        int count = 0;

        for (int index : sortedMap.descendingMap().values()) {
            if (count >= topIndices.length) {
                break;
            }
            topIndices[count++] = index;
        }

        return topIndices;
    }

    /**
     * Retrieves the names of the top three predicted fish species.
     *
     * @param topIndexPredictions an array containing indices of the top 3 highest confidence value
     * @return An array of strings containing the fish names
     */
    public static String[] getTopFishSpeciesName(int[] topIndexPredictions) {
        String[] topFishSpecies = new String[3];

        for (int i = 0; i < topFishSpecies.length; i++) {
            topFishSpecies[i] = fishSpeciesNames[topIndexPredictions[i]];
        }
        return topFishSpecies;
    }

    /**
     * Returns formatted confidence values for the top 3 predicted fish species with two decimal places.
     *
     * @param confidence An array of float values representing confidence levels
     * @return An array of strings containing formatted confidence values (in percentage) for the top 3 predicted fish species.
     */
    public static String[] getConfidencesAsFormattedString(float[] confidence, int[] topIndices) {
        String[] formattedConfidences = new String[3];

        for (int i = 0; i < formattedConfidences.length; i++) {
            float confidenceValue = confidence[topIndices[i]] * 100;
            formattedConfidences[i] = String.format(Locale.getDefault(), "%.2f", confidenceValue);
        }

        return formattedConfidences;
    }
}
