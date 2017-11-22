/*
 * Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Caddisfly.
 *
 * Akvo Caddisfly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Caddisfly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Caddisfly. If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.caddisfly.ui;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.RequiresDevice;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.akvo.caddisfly.R;
import org.akvo.caddisfly.helper.TestConfigHelper;
import org.akvo.caddisfly.model.TestInfo;
import org.akvo.caddisfly.model.TestType;
import org.akvo.caddisfly.sensor.SensorConstants;
import org.akvo.caddisfly.util.TestConstant;
import org.akvo.caddisfly.util.TestUtil;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.akvo.caddisfly.util.TestHelper.clickExternalSourceButton;
import static org.akvo.caddisfly.util.TestHelper.getString;
import static org.akvo.caddisfly.util.TestHelper.goToMainScreen;
import static org.akvo.caddisfly.util.TestHelper.gotoSurveyForm;
import static org.akvo.caddisfly.util.TestHelper.loadData;
import static org.akvo.caddisfly.util.TestHelper.mCurrentLanguage;
import static org.akvo.caddisfly.util.TestHelper.mDevice;
import static org.akvo.caddisfly.util.TestHelper.resetLanguage;
import static org.akvo.caddisfly.util.TestHelper.takeScreenshot;
import static org.akvo.caddisfly.util.TestUtil.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InstructionsTest {

    private final StringBuilder jsArrayString = new StringBuilder();
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void initialize() {
        if (mDevice == null) {
            mDevice = UiDevice.getInstance(getInstrumentation());

            for (int i = 0; i < 5; i++) {
                mDevice.pressBack();
            }
        }
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

    @Before
    public void setUp() {

        loadData(mActivityTestRule.getActivity(), mCurrentLanguage);

//        SharedPreferences prefs =
//                PreferenceManager.getDefaultSharedPreferences(mActivityTestRule.getActivity());
//        prefs.edit().clear().apply();

        resetLanguage();
    }

    @Test
    public void instructionsTest() {

        onView(withText("Strip Test")).perform(click());

        ViewInteraction linearLayout1 = onView(
                allOf(childAtPosition(
                        withId(R.id.list_types),
                        3),
                        isDisplayed()));
        linearLayout1.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button_instructions), withText("Instructions"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton2.perform(click());

        onView(withText(R.string.collect_5ml_mehlich_sample))
                .check(matches(isDisplayed()));

        onView(withText("Soil - Phosphorous"))
                .check(matches(isDisplayed()));

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.image_pageRight),
                        childAtPosition(
                                allOf(withId(R.id.layout_footer),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatImageView.perform(click());

        onView(withText(R.string.add_5_drops_reagent_1))
                .check(matches(isDisplayed()));

//        onView(withText(R.string.swirl_and_mix))
//                .check(matches(isDisplayed()));

        onView(withId(R.id.pager_indicator)).check(matches(isDisplayed()));

        onView(withId(R.id.pager))
                .perform(swipeLeft());

        onView(withText(R.string.put_6_drops_of_reagent_in_another_container))
                .check(matches(isDisplayed()));

        onView(withText(R.string.place_tube_in_provided_rack))
                .check(matches(isDisplayed()));

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.image_pageRight),
                        childAtPosition(
                                allOf(withId(R.id.layout_footer),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatImageView2.perform(click());

//        onView(withText(R.string.dip_container_15s))
//                .check(matches(isDisplayed()));

//        onView(withText(R.string.shake_excess_water))
//                .check(matches(isDisplayed()));

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.image_pageRight),
                        childAtPosition(
                                allOf(withId(R.id.layout_footer),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        ViewInteraction appCompatImageView4 = onView(
                allOf(withId(R.id.image_pageRight),
                        childAtPosition(
                                allOf(withId(R.id.layout_footer),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatImageView4.perform(click());

        ViewInteraction textView20 = onView(
                allOf(withText("Place the test strip with the patch facing left on the black area of the Colour Reference Card and press Start."),
                        childAtPosition(
                                allOf(withId(R.id.layout_instructions),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView20.check(matches(withText("Place the test strip with the patch facing left on the black area of the Colour Reference Card and press Start.")));

        ViewInteraction imageView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.layout_instructions),
                                childAtPosition(
                                        IsInstanceOf.instanceOf(android.widget.LinearLayout.class),
                                        0)),
                        1),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.imageBrandLabel),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                1),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.button_prepare),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                2),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.button_instructions),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                3),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        pressBack();

        onView(withText("Strip Test")).check(matches(isDisplayed()));

    }

    @Test
    public void ironStripTestInstructions() {

        goToMainScreen();

        gotoSurveyForm();

        clickExternalSourceButton(TestConstant.NEXT);

        clickExternalSourceButton(TestConstant.NEXT);

        clickExternalSourceButton(TestConstant.NEXT);

        clickExternalSourceButton(0);

        sleep(1000);

        mDevice.waitForIdle();

        TestUtil.sleep(1000);

        onView(withText("Water - Total Iron"))
                .check(matches(isDisplayed()));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button_instructions), withText("Instructions"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton2.perform(click());

        onView(withText(R.string.fill_half_with_sample))
                .check(matches(isDisplayed()));

        onView(withText("Water - Total Iron"))
                .check(matches(isDisplayed()));

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.image_pageRight),
                        childAtPosition(
                                allOf(withId(R.id.layout_footer),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatImageView.perform(click());

        onView(withText(R.string.open_one_foil_and_add_powder))
                .check(matches(isDisplayed()));

        onView(withId(R.id.pager_indicator)).check(matches(isDisplayed()));

        onView(withId(R.id.pager))
                .perform(swipeLeft());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.imageBrandLabel),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                1),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.button_prepare),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                2),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.button_instructions),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                3),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));
        button2.perform(click());

        pressBack();

        ViewInteraction imageView3 = onView(
                allOf(withId(R.id.imageBrandLabel),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                1),
                        isDisplayed()));
        imageView3.check(matches(isDisplayed()));

        ViewInteraction button1 = onView(
                allOf(withId(R.id.button_prepare),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinatorLayout),
                                        0),
                                2),
                        isDisplayed()));
        button1.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

    }

    @Test
    @RequiresDevice
    public void testInstructionsBackcase() {

        goToMainScreen();

        gotoSurveyForm();

        clickExternalSourceButton(0);

        sleep(1000);

        mDevice.waitForIdle();

        TestUtil.sleep(1000);

        String id = SensorConstants.FLUORIDE_ID.substring(
                SensorConstants.FLUORIDE_ID.lastIndexOf("-") + 1, SensorConstants.FLUORIDE_ID.length());

        takeScreenshot(id, -1);

        mDevice.waitForIdle();

        onView(withText(getString(mActivityTestRule.getActivity(), R.string.instructions))).perform(click());

        for (int i = 0; i < 17; i++) {

            try {
                takeScreenshot(id, i);

                onView(withId(R.id.image_pageRight)).perform(click());

            } catch (Exception e) {
                TestUtil.sleep(600);
                Espresso.pressBack();
                break;
            }
        }
    }

    @Test
    @RequiresDevice
    public void testInstructionsBackcase2() {

        goToMainScreen();

        gotoSurveyForm();

        clickExternalSourceButton(TestConstant.NEXT);

        clickExternalSourceButton(0);

        sleep(1000);

        mDevice.waitForIdle();

        TestUtil.sleep(1000);

        String id = SensorConstants.FREE_CHLORINE_ID.substring(
                SensorConstants.FREE_CHLORINE_ID.lastIndexOf("-") + 1, SensorConstants.FREE_CHLORINE_ID.length());

        takeScreenshot(id, -1);

        mDevice.waitForIdle();

        onView(withText(getString(mActivityTestRule.getActivity(), R.string.instructions))).perform(click());

        for (int i = 0; i < 17; i++) {

            try {
                takeScreenshot(id, i);

                onView(withId(R.id.image_pageRight)).perform(click());

            } catch (Exception e) {
                TestUtil.sleep(600);
                Espresso.pressBack();
                break;
            }
        }
    }

    @Test
    @RequiresDevice
    public void testInstructionsAll() {

        goToMainScreen();

        onView(withText(getString(mActivityTestRule.getActivity(), R.string.stripTest))).perform(click());

        List<TestInfo> testList = TestConfigHelper.loadTestsList();

        int index = 0;
        int firstTestIndex = 3;
        for (int i = firstTestIndex + index; i < 30; i++) {

            if (testList.get(i).getType() == TestType.COLORIMETRIC_STRIP) {
//                if (testList.get(i).getTitle().startsWith("Soil")) {
                String id = testList.get(i).getId();
                id = id.substring(id.lastIndexOf("-") + 1, id.length());

                int pages = navigateToTest(index, id);

                jsArrayString.append("[").append("\"").append(id).append("\",").append(pages).append("],");

                index++;
//                }
            }
        }

        Log.e("Caddisfly", jsArrayString.toString());

    }

    private int navigateToTest(int index, String id) {

        DataInteraction linearLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.list_types),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)))
                .atPosition(index);
        linearLayout.perform(click());

        mDevice.waitForIdle();

        TestUtil.sleep(1000);

        takeScreenshot(id, -1);

        mDevice.waitForIdle();

        onView(withText(getString(mActivityTestRule.getActivity(), R.string.instructions))).perform(click());

        int pages = 0;
        for (int i = 0; i < 17; i++) {
            pages++;

            try {
                takeScreenshot(id, i);

                onView(withId(R.id.image_pageRight)).perform(click());

            } catch (Exception e) {
                TestUtil.sleep(600);
                Espresso.pressBack();
                Espresso.pressBack();
                TestUtil.sleep(600);
                break;
            }
        }
        return pages;
    }
}