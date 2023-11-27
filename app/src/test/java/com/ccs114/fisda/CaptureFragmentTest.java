package com.ccs114.fisda;

import static com.google.common.truth.Truth.assertThat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import android.os.Bundle;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
public class CaptureFragmentTest {
    CaptureFragment captureFragment = new CaptureFragment();

    static Stream<Arguments> testDataForFishInputInfo() {
        return Stream.of(
                Arguments.of("image1.png", new String[]{"Trout", "Salmon", "Bass"},
                        new String[]{"0.05", "0.10", "0.20"}, "path1", "file1", new OutputHandler()),

                Arguments.of("image2.png", new String[]{"Tuna", "Swordfish", "Mackerel"},
                        new String[]{"0.51", "0.23", "0.80"}, "path2", "file2", new OutputHandler()),

                Arguments.of("image3.png", new String[]{"Carp", "Catfish", "Perch"},
                        new String[]{"5.0", "4.0", "6.0"}, "path3", "file3", new OutputHandler())
        );
    }

    @ParameterizedTest
    @MethodSource("testDataForFishInputInfo")
    public void fishInputInfo_shouldReturnCorrectArgs(String uri, String[] topFishSpecies, String[] topConfidences,
                                                      String imagepath, String imageFileName, OutputHandler handler) {

        Bundle expected = new Bundle();

        expected.putString("uri", uri);
        expected.putStringArray("topFishSpecies", topFishSpecies);
        expected.putStringArray("topConfidences", topConfidences);
        expected.putString("imagepath", imagepath);
        expected.putString("filename", imageFileName);

        handler.setConfidence(new float[3]);
        Bundle result = captureFragment.fishInputInfo(uri, topFishSpecies, topConfidences,
                imagepath, imageFileName, handler);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result.keySet()).isEqualTo(expected.keySet());
        assertThat(result.getByteArray("imagebytes")).isEqualTo(expected.getByteArray("imageBytes"));
    }
}