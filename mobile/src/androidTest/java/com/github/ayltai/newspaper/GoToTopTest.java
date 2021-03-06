package com.github.ayltai.newspaper;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.android.libraries.cloudtesting.screenshots.ScreenShotter;

import com.github.ayltai.newspaper.util.MoreTestUtils;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GoToTopTest extends BaseTest {
    @Test
    public void goToTopTest() {
        // Checks that Featured News is displayed
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.featured_image),
            ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Scrolls down 20 view part items so that Featured News is not displayed
        Espresso.onView(MoreTestUtils.first(Matchers.allOf(
            ViewMatchers.withClassName(Matchers.is("com.github.ayltai.newspaper.widget.SmartRecyclerView")),
            ViewMatchers.withId(R.id.recyclerView))))
            .perform(MoreTestUtils.smoothScrollToPosition(20));

        MoreTestUtils.sleep(MoreTestUtils.DURATION_SHORT);

        ScreenShotter.takeScreenshot(this.getClass().getSimpleName() + ".goToTop.scrollDown", this.testRule.getActivity());

        // Scrolls up 4 view part items so that the Floating Action Button is displayed
        Espresso.onView(MoreTestUtils.first(Matchers.allOf(
            ViewMatchers.withClassName(Matchers.is("com.github.ayltai.newspaper.widget.SmartRecyclerView")),
            ViewMatchers.withId(R.id.recyclerView))))
            .perform(MoreTestUtils.smoothScrollToPosition(16));

        MoreTestUtils.sleep(MoreTestUtils.DURATION_SHORT);

        ScreenShotter.takeScreenshot(this.getClass().getSimpleName() + ".goToTop.scrollUp", this.testRule.getActivity());

        // Checks that Featured News is not displayed
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.featured_image),
            ViewMatchers.isDisplayed()))
            .check(ViewAssertions.doesNotExist());

        // Clicks the More button
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.action_more),
            ViewMatchers.isDisplayed()))
            .perform(ViewActions.click());

        MoreTestUtils.sleep(MoreTestUtils.DURATION_SHORT);

        // Clicks the Up button
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.action_up),
            ViewMatchers.isDisplayed()))
            .perform(ViewActions.click());

        MoreTestUtils.sleep(MoreTestUtils.DURATION_SHORT);

        ScreenShotter.takeScreenshot(this.getClass().getSimpleName() + ".goToTop", this.testRule.getActivity());

        // Checks that Featured News is displayed
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.featured_image),
            ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
