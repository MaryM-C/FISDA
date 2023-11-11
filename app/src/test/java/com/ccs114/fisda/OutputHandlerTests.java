package com.ccs114.fisda;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class OutputHandlerTests {
    private final OutputHandler handler;

    public OutputHandlerTests() {
        handler = new OutputHandler();
    }

    static Stream<Arguments> testDataForComputeTopConfidence() {
        return Stream.of(
                Arguments.of(new float[] { 0.05f, 0.10f, 0.20f }, 0.20f),
                Arguments.of(new float[] { 0.51f, 0.23f, 0.80f }, 0.80f),
                Arguments.of(new float[] { 5.0f, 4.0f, 6.0f, 3.0f }, 6.0f)
        );
    }

    static Stream<Arguments> testDataForComputeTopIndices() {
        return Stream.of(
                Arguments.of(new float[] { 0.5f, 0.7f, 0.3f, 0.8f, 0.2f }, //confidences
                        new int[] { 3, 1, 0 }), //Index of the top three highest confidence
                Arguments.of(new float[] { 0.12f, 0.32f, 0.03f, 0.4f, 0.5f },
                        new int[] { 4, 3, 1}),
                Arguments.of(new float[] { 0.01f, 0.12f, 0.23f, 0.11f, 0.59f },
                        new int[] { 4, 2, 1})
        );
    }

    static Stream<Arguments> testDataForGetTopFishSpeciesName() {
        return Stream.of(
                Arguments.of(
                        new int[] { 3, 1, 0 }, // Top Indices
                        new String[] {"Catfish", "Blackchin Tilapia", "Big Head Carp",}), //Fish Names associated at top indices
                Arguments.of(
                        new int[] { 1, 0, 4 },
                        new String[] {"Blackchin Tilapia", "Big Head Carp", "Climbing Perch",}),
                Arguments.of(
                        new int[] { 2, 1, 4 },
                        new String[] {"Carp", "Blackchin Tilapia", "Climbing Perch",})

        );
    }

    static Stream<Arguments> testDataForGetConfidencesAsFormattedString() {
        return Stream.of(
                Arguments.of(
                        new float[] {0.1f, 0.2f, 0.3f}, // confidences
                        new int[] {2,1,0}, //indices
                        new String[] {"30.00", "20.00", "10.00"}), //Formatted Strings
                Arguments.of(
                        new float[] {0.99f, 0.21f, 0.32f},
                        new int[] {0,2,1},
                        new String[] {"99.00", "32.00", "21.00"}),
                Arguments.of(
                        new float[] {0.11f, 0.02f, 0.31f},
                        new int[] {2,0,1},
                        new String[] {"31.00", "11.00", "2.00"})
                );
    }

    @ParameterizedTest
    @MethodSource("testDataForComputeTopConfidence")
    public void computeTopConfidence_shouldReturnTheHighestConfidenceValue(float[] inputArray, float expectedValue) {
        handler.setConfidence(inputArray);
        float actualValue = handler.computeTopConfidence(handler.getConfidence());

        assertNotNull(inputArray, "Input array should not be null");
        assertArrayEquals(inputArray, handler.getConfidence(), "The arrays are not the same");
        assertEquals(expectedValue, actualValue, 0.0001, "Returns the wrong value of the highest confidence");
    }

    @ParameterizedTest
    @MethodSource("testDataForComputeTopIndices")
    public void computeTopIndices_shouldReturnTheIndicesOfTheHighestConfidences(float[] inputArray, int[] expectedValues) {
        int[] actualValues = handler.computeTopIndices(inputArray);

        assertEquals(expectedValues.length, actualValues.length, "Array lengths do not match");
        assertArrayEquals(expectedValues, actualValues, "Returns the wrong array of top indices");

    }

    @ParameterizedTest
    @MethodSource("testDataForGetTopFishSpeciesName")
    public void getTopFishSpeciesName_shouldReturnTheFishNamesBasedOnTheArray(int[] topIndices, String[] expectedFishNames) {
        String[] actualFishNames = handler.getTopFishSpeciesName(topIndices);

        assertNotNull(expectedFishNames, "No fish names were retrieved");
        assertArrayEquals(expectedFishNames, actualFishNames, "Fish names did not match");

    }

    @ParameterizedTest
    @MethodSource("testDataForGetConfidencesAsFormattedString")
    public void getConfidencesAsFormattedString_shouldReturnIndicatedFormatForConfidences(float[] confidences, int[] indices,
            String[] expectedStrings) {
        String[] actualStrings = handler.getConfidencesAsFormattedString(confidences, indices);

        assertArrayEquals(expectedStrings, actualStrings, "Formatted strings are not the same");

        for (String actualString : actualStrings) {
            assertTrue(actualString.matches("\\d+\\.\\d{2}"), "Invalid format in formatted string: " + actualString);
        }

    }
}
