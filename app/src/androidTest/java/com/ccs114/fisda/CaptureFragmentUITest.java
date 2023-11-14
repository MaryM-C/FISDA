package com.ccs114.fisda;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;

public class CaptureFragmentUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @Test
    public void captureFragment_openCamera() {
        Espresso.onView(withId(R.id.btnCamera)).check(matches(isDisplayed()));
    }

    @Test
    public void captureFragment_openGallery() {
        Espresso.onView(withId(R.id.btnGallery)).check(matches(isDisplayed()));

    }
}