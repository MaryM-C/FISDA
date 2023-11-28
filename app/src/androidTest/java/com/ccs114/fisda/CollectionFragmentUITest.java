package com.ccs114.fisda;


import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.ccs114.fisda.activities.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

public class CollectionFragmentUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Test
    public void collectionFragment_clickAndViewAnItem() {
        Matcher<Intent> expectedIntent = allOf(
                hasAction(Intent.ACTION_PICK),
                hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        );

        Instrumentation.ActivityResult activityResult = createGalleryPickActivityResult();
        intending(expectedIntent).respondWith(activityResult);

        Espresso.onView(withId(R.id.btnGallery)).perform(click());


        intended(expectedIntent);

    }

    private Instrumentation.ActivityResult createGalleryPickActivityResult() {
        Resources resources = InstrumentationRegistry.getInstrumentation().getContext().getResources();

        Uri imageUri = Uri.parse("android.resource://" +
                resources.getResourcePackageName(R.drawable.ic_launcher_background) + "/" +
                resources.getResourceTypeName(R.drawable.ic_launcher_background) + "/" +
                resources.getResourceEntryName(R.drawable.ic_launcher_background));

        Intent resultIntent = new Intent();
        resultIntent.setData(imageUri);
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent);

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
