package com.ccs114.fisda;

public class OutputHandler {
    private float[] confidence;
    private static final String[] fishSpeciesNames = {"Big Head Carp", "Blackchin Tilapia", "Carp", "Catfish", "Climbing Perch",
            "Freshwater Eel", "Goby", "Gold Fish", "Gourami", "Indian Carp", "Indio-Pacific Tarpon",
            "Jaguar Guapote", "Janitor fish", "Knife fish", "Manila Catfish", "Milkfish",
            "Mosquito Fish", "Mudfish", "Mullet", "Scat Fish", "Silver Barb", "Silver Carp",
            "Silver Perch", "Tenpounder", "Tilapia"};

    public OutputHandler(float[] confidence) {
        this.confidence = confidence;
    }
    //return the highest maximum value
    public int getMaxIndex() {
        int maxIndex = 0;
        float maxValue = confidence[0];
        for (int i = 1; i < confidence.length; i++) {
            if (confidence[i] > maxValue) {
                maxIndex = i;
                maxValue = confidence[i];
            }
        }
        return maxIndex;
    }

    public float getMaximumValue() {
        float maxValue = confidence[0];

        for (int i = 1; i < confidence.length; i++) {
            if (confidence[i] > maxValue) {
                maxValue = confidence[i];
            }
        }
        return maxValue;
    }

    public int[] getTop3Predictions() {
        int[] topIndices = new int[3];
        float[] topValues = new float[3];

        for (int i = 0; i < confidence.length; i++) {
            for (int j = 0; j < topValues.length; j++) {
                if (confidence[i] > topValues[j]) {
                    for (int k = topValues.length - 1; k > j; k--) {
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

    public String[] getTop3FishSpecies() {
        int[] topIndices = getTop3Predictions();
        String[] topFishSpecies = new String[3];
        for (int i = 0; i < topFishSpecies.length; i++) {
            topFishSpecies[i] = fishSpeciesNames[topIndices[i]];
        }
        return topFishSpecies;
    }

    private float[] getTop3Confidences() {
        int[] topIndices = getTop3Predictions();
        float[] topConfidences = new float[3];
        for (int i = 0; i < topConfidences.length; i++) {
            topConfidences[i] = confidence[topIndices[i]];
        }
        return topConfidences;
    }

    public String[] getConfidences() {
        int[] topIndices = getTop3Predictions();
        String[] formattedConfidences = new String[3];

        for (int i = 0; i < formattedConfidences.length; i++) {
            float confidenceValue = confidence[topIndices[i]] * 100; // Multiply by 100 for percentage
            formattedConfidences[i] = String.format("%.2f", confidenceValue); // Format to two decimal places
        }

        return formattedConfidences;
    }





}
