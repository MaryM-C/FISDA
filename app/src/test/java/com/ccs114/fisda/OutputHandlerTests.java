package com.ccs114.fisda;

import static com.google.common.truth.Truth.assertThat;

import com.ccs114.fisda.utils.OutputHandler;

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
                Arguments.of(new float[] { 0.01f, 0.12f, 0.23f, 0.11f, 0.59f},
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
                        new float[] {0.001f, 0.2f, 0.3f}, // confidences
                        new int[] {2,1,0}, //indices
                        new String[] {"30.00", "20.00", "00.01"}), //Formatted Strings
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

        assertThat(inputArray).isNotNull();
        assertThat(inputArray).isEqualTo(handler.getConfidence());
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @ParameterizedTest
    @MethodSource("testDataForComputeTopIndices")
    public void computeTopIndices_shouldReturnTheIndicesOfTheHighestConfidences(float[] inputArray, int[] expectedValues) {
        int[] actualValues = OutputHandler.computeTopIndices(inputArray);

        assertThat(actualValues).isNotEmpty();
        assertThat(actualValues.length).isEqualTo(expectedValues.length);
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @ParameterizedTest
    @MethodSource("testDataForGetTopFishSpeciesName")
    public void getTopFishSpeciesName_shouldReturnTheFishNamesBasedOnTheArray(int[] topIndices, String[] expectedFishNames) {
        String[] actualFishNames = OutputHandler.getTopFishSpeciesName(topIndices);

        assertThat(actualFishNames).isNotEmpty();
        assertThat(actualFishNames.length).isEqualTo(expectedFishNames.length);
        assertThat(actualFishNames).isEqualTo(expectedFishNames);
    }

    @ParameterizedTest
    @MethodSource("testDataForGetConfidencesAsFormattedString")
    public void getConfidencesAsFormattedString_shouldReturnIndicatedFormatForConfidences(float[] confidences, int[] indices,
            String[] expectedStrings) {
        String[] actualStrings = OutputHandler.getConfidencesAsFormattedString(confidences, indices);

        assertThat(actualStrings).isNotEmpty();
        assertThat(actualStrings.length).isEqualTo(expectedStrings.length);

        for (String actualString : actualStrings) {
            assertThat(actualString).matches("\\d+\\.\\d{2}");
        }
    }
}
