package com.ccs114.fisda;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HomeFragmentUITest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private String fishName = "Carp";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void navigateBottomBar_shoudDisplayAppropriateFragments() {
        //press on different navigation buttons
        Espresso.onView(withId(R.id.home)).perform(click());
        Espresso.onView(withId(R.id.capture)).perform(click());
        Espresso.onView(withId(R.id.collections)).perform(click());

    }

    @Test
    public void homeFragment_allowScrollingToViewItems() {
        Espresso.onView(withId(R.id.home)).perform(click());

        Espresso.onView(withId(R.id.collectionRecyclerView)).perform(RecyclerViewActions.scrollToLastPosition());
    }

    @Test
    public void homeFragment_clickAndViewAnItem() {
        Espresso.onView(withId(R.id.home)).perform(click());

        Espresso.onView(withId(R.id.collectionRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Espresso.onView(withId(R.id.scrlInfo)).perform(swipeDown());
        Espresso.onView(withId(R.id.btnBack)).perform(click());
    }
}