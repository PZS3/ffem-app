package org.akvo.caddisfly.test;

import android.support.test.filters.RequiresDevice;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;

import org.akvo.caddisfly.R;
import org.akvo.caddisfly.ui.MainActivity;
import org.akvo.caddisfly.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertNotNull;
import static org.akvo.caddisfly.util.TestHelper.activateTestMode;
import static org.akvo.caddisfly.util.TestHelper.clearPreferences;
import static org.akvo.caddisfly.util.TestHelper.clickExternalSourceButton;
import static org.akvo.caddisfly.util.TestHelper.gotoSurveyForm;
import static org.akvo.caddisfly.util.TestHelper.loadData;
import static org.akvo.caddisfly.util.TestHelper.mCurrentLanguage;
import static org.akvo.caddisfly.util.TestHelper.mDevice;
import static org.akvo.caddisfly.util.TestUtil.sleep;

public class StriptestTest {

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

        clearPreferences(mActivityRule);

//        resetLanguage();
    }

    @Test
    @RequiresDevice
    public void startStriptest() {

        activateTestMode(mActivityRule.getActivity());

        testArsenic();

        testArsenic2();
    }

    @After
    public void tearDown() {
        clearPreferences(mActivityRule);
    }

    private void testArsenic() {

        gotoSurveyForm();

        TestUtil.nextSurveyPage(3, "Arsenic");

        clickExternalSourceButton(0);

        mDevice.waitForIdle();

        sleep(1000);

        onView(withText("Prepare for test")).perform(click());

        sleep(8000);

        onView(withText(R.string.start)).perform(click());

        sleep(5000);

        onView(withText(R.string.result)).check(matches(isDisplayed()));
        onView(withText("Arsenic")).check(matches(isDisplayed()));
        onView(withText(R.string.no_result)).check(matches(isDisplayed()));

        onView(withId(R.id.image_result)).check(matches(isDisplayed()));

        onView(withText(R.string.save)).check(matches(isDisplayed()));

        onView(withText(R.string.save)).perform(click());

//        assertNotNull(mDevice.findObject(By.text("Result: ")));
//        assertNotNull(mDevice.findObject(By.text("20")));
    }

    private void testArsenic2() {

        gotoSurveyForm();

        TestUtil.nextSurveyPage(3, "Arsenic");

        clickExternalSourceButton(2);

        mDevice.waitForIdle();

        sleep(1000);

        onView(withText("Prepare for test")).perform(click());

        sleep(8000);

        onView(withText("Start")).perform(click());

        sleep(5000);

        onView(withText(R.string.result)).check(matches(isDisplayed()));
        onView(withText("Arsenic")).check(matches(isDisplayed()));
        onView(withText("No Result")).check(matches(isDisplayed()));

        onView(withId(R.id.image_result)).check(matches(isDisplayed()));

        onView(withText(R.string.save)).check(matches(isDisplayed()));

        onView(withText(R.string.save)).perform(click());

        assertNotNull(mDevice.findObject(By.text("Unit: ")));
        assertNotNull(mDevice.findObject(By.text("ug/l")));
    }


}
