package com.ccs114.fisda;

public class OutputHandler {
    private float[] confidence;

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


}
