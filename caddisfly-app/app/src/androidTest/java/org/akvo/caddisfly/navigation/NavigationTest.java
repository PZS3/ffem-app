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

package org.akvo.caddisfly.navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.DatePicker;

import org.akvo.caddisfly.BuildConfig;
import org.akvo.caddisfly.R;
import org.akvo.caddisfly.common.TestConstants;
import org.akvo.caddisfly.ui.MainActivity;
import org.akvo.caddisfly.util.TestUtil;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.filters.LargeTest;
import androidx.test.filters.RequiresDevice;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.akvo.caddisfly.util.TestHelper.clickExternalSourceButton;
import static org.akvo.caddisfly.util.TestHelper.enterDiagnosticMode;
import static org.akvo.caddisfly.util.TestHelper.goToMainScreen;
import static org.akvo.caddisfly.util.TestHelper.gotoSurveyForm;
import static org.akvo.caddisfly.util.TestHelper.leaveDiagnosticMode;
import static org.akvo.caddisfly.util.TestHelper.loadData;
import static org.akvo.caddisfly.util.TestHelper.mCurrentLanguage;
import static org.akvo.caddisfly.util.TestHelper.mDevice;
import static org.akvo.caddisfly.util.TestHelper.saveCalibration;
import static org.akvo.caddisfly.util.TestHelper.takeScreenshot;
import static org.akvo.caddisfly.util.TestUtil.childAtPosition;
import static org.akvo.caddisfly.util.TestUtil.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.object.HasToString.hasToString;

@SuppressWarnings("PMD.NcssMethodCount")
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void initialize() {
        if (mDevice == null) {
            mDevice = UiDevice.getInstance(getInstrumentation());

            for (int i = 0; i < 5; i++) {
                mDevice.pressBack();
            }
        }
    }

    @Before
    public void setUp() {

        loadData(mActivityRule.getActivity(), mCurrentLanguage);

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mActivityRule.getActivity());
        prefs.edit().clear().apply();

//        resetLanguage();
    }

    @Test
    @RequiresDevice
    public void testNavigateAll() {

        saveCalibration("TestInvalid", TestConstants.CUVETTE_TEST_ID_1);

        String path = Environment.getExternalStorageDirectory().getPath()
                + "/" + BuildConfig.APPLICATION_ID + "/screenshots";

        File folder = new File(path);
        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }

        mDevice.waitForWindowUpdate("", 2000);

        goToMainScreen();

        //Main Screen
        takeScreenshot();

        onView(withId(R.id.actionSettings)).perform(click());

        //Settings Screen
        takeScreenshot();

        onView(withText(R.string.about)).check(matches(isDisplayed())).perform(click());

        mDevice.waitForWindowUpdate("", 1000);

        //About Screen
        takeScreenshot();

        Espresso.pressBack();

//        onView(withText(R.string.language)).perform(click());

//        mDevice.waitForWindowUpdate("", 1000);

        //Language Dialog
//        takeScreenshot();

//        onView(withId(android.R.id.button2)).perform(click());

        onView(withText(R.string.about)).check(matches(isDisplayed())).perform(click());

        enterDiagnosticMode();

        goToMainScreen();

        onView(withText(R.string.calibrate)).perform(click());

        sleep(4000);

        onView(allOf(withId(R.id.list_types),
                childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        0))).perform(actionOnItemAtPosition(
                TestConstants.TEST_INDEX, click()));

        if (TestUtil.isEmulator()) {

            onView(withText(R.string.errorCameraFlashRequired))
                    .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow()
                            .getDecorView())))).check(matches(isDisplayed()));
            return;
        }

        onView(withId(R.id.menuLoad)).perform(click());

        sleep(2000);

        onData(hasToString(startsWith("TestInvalid"))).perform(click());

        sleep(2000);

        onView(withText(String.format("%s. %s", mActivityRule.getActivity()
                        .getString(R.string.calibrationIsInvalid),
                mActivityRule.getActivity().getString(R.string.tryRecalibrating)))).check(matches(isDisplayed()));

        leaveDiagnosticMode();

        sleep(4000);

        onView(withText(R.string.calibrate)).perform(click());

        //Test Types Screen
        takeScreenshot();

        onView(allOf(withId(R.id.list_types),
                childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        0))).perform(actionOnItemAtPosition(
                TestConstants.TEST_INDEX, click()));

        //Calibrate Swatches Screen
        takeScreenshot();

//        DecimalFormatSymbols dfs = new DecimalFormatSymbols();

        onView(withId(R.id.fabEditCalibration)).perform(click());

//        onView(withId(R.id.editBatchCode))
//                .perform(typeText("TEST 123#*@!"), closeSoftKeyboard());
//
        onView(withId(R.id.editExpiryDate)).perform(click());

        onView(withClassName((Matchers.equalTo(DatePicker.class.getName()))))
                .perform(PickerActions.setDate(2025, 8, 25));

        onView(withId(android.R.id.button1)).perform(click());

        onView(withText(R.string.save)).perform(click());

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.calibrationList),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView3.perform(actionOnItemAtPosition(4, click()));

        // onView(withText("2" + dfs.getDecimalSeparator() + "0 mg/l")).perform(click());

        //onView(withId(R.id.buttonStart)).perform(click());

        saveCalibration("TestValid", TestConstants.CUVETTE_TEST_ID_1);

        goToMainScreen();

        onView(withId(R.id.actionSettings)).perform(click());

        onView(withText(R.string.about)).check(matches(isDisplayed())).perform(click());

        enterDiagnosticMode();

        Espresso.pressBack();

        Espresso.pressBack();

        onView(withText(R.string.calibrate)).perform(click());

        sleep(4000);

        onView(allOf(withId(R.id.list_types),
                childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        0))).perform(actionOnItemAtPosition(
                TestConstants.TEST_INDEX, click()));

        onView(withId(R.id.menuLoad)).perform(click());

        sleep(2000);

        onData(hasToString(startsWith("TestValid"))).perform(click());

        sleep(2000);

        leaveDiagnosticMode();

        onView(withText(R.string.calibrate)).perform(click());

//        onView(withText(currentHashMap.get("electricalConductivity"))).perform(click());
//
//        try {
//            onView(withText(R.string.incorrectCalibrationCanAffect)).check(matches(isDisplayed()));
//            //Calibrate EC Warning
//            takeScreenshot();
//
//            onView(withText(R.string.cancel)).perform(click());
//
//            onView(withText(currentHashMap.get("electricalConductivity"))).perform(click());
//
//            onView(withText(R.string.warning)).check(matches(isDisplayed()));
//
//            onView(withText(R.string.calibrate)).perform(click());
//
//            //Calibrate EC
//            takeScreenshot();
//
//            onView(withId(R.id.buttonStartCalibrate)).perform(click());
//
//            //EC not found dialog
//            takeScreenshot();
//
//            onView(withId(android.R.id.button1)).perform(click());
//
//        } catch (Exception ex) {
//            String message = String.format("%s\r\n\r\n%s", mActivityRule.getActivity().getString(R.string.phoneDoesNotSupport),
//                    mActivityRule.getActivity().getString(R.string.pleaseContactSupport));
//
//            onView(withText(message)).check(matches(isDisplayed()));
//
//            //Feature not supported
//            takeScreenshot();
//
//            onView(withText(R.string.ok)).perform(click());
//        }

        goToMainScreen();

        gotoSurveyForm();

        clickExternalSourceButton(TestConstants.CUVETTE_TEST_ID_1);

        onView(withId(R.id.button_prepare)).check(matches(isDisplayed()));

        onView(withId(R.id.button_prepare)).perform(click());

        onView(withId(R.id.buttonNoDilution)).check(matches(isDisplayed()));

        //Dilution dialog
        takeScreenshot();

        TestUtil.goBack(5);

        mActivityRule.launchActivity(new Intent());

        gotoSurveyForm();

        clickExternalSourceButton(TestConstants.CUVETTE_TEST_ID_1);

        onView(withText(R.string.testName)).check(matches(isDisplayed()));

//        //Calibration incomplete
        takeScreenshot();

        // Chlorine not calibrated
        //onView(withText(R.string.cannotStartTest)).check(matches(isDisplayed()));

        //onView(withId(android.R.id.button2)).perform(click());

        mDevice.pressBack();

        mDevice.waitForWindowUpdate("", 2000);

//        clickExternalSourceButton(TestConstants.CUVETTE_TEST_ID_2);

//        onView(withText(R.string.chromium)).check(matches(isDisplayed()));

//        onView(withText(R.string.cannotStartTest)).check(matches(isDisplayed()));

//        takeScreenshot();

//        mDevice.pressBack();

//        TestUtil.nextSurveyPage(3);
//
//        //Unknown test
//        clickExternalSourceButton(0, TestConstant.USE_EXTERNAL_SOURCE);
//
//        onView(withText(R.string.cannotStartTest)).check(matches(isDisplayed()));
//
//        mDevice.pressBack();

//        TestUtil.swipeRight(7);
//
//        clickExternalSourceButton(0); //Iron
//
//        onView(withText(R.string.prepare_test)).check(matches(isDisplayed()));

        //onView(withText(R.string.cannotStartTest)).check(matches(isDisplayed()));

        //onView(withText(R.string.ok)).perform(click());

        mDevice.pressBack();

        //mDevice.pressBack();
        //onView(withId(android.R.id.button1)).perform(click());

    }
}
