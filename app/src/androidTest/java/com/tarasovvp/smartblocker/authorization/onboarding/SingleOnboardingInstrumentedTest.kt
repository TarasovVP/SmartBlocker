package com.tarasovvp.smartblocker.authorization.onboarding

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.ON_BOARDING_PAGE
import com.tarasovvp.smartblocker.enums.OnBoarding
import com.tarasovvp.smartblocker.ui.main.authorization.onboarding.SingleOnBoardingFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SingleOnboardingInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        launchFragmentInHiltContainer<SingleOnBoardingFragment>(fragmentArgs = bundleOf(ON_BOARDING_PAGE to OnBoarding.ONBOARDING_INTRO)) {
        }
    }

    /**
     *
     */
    @Test
    fun checkSingleOnBoardingTitle() {
        onView(withId(R.id.single_on_boarding_title)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSingleOnBoardingTooltipArrow() {
        onView(withId(R.id.single_on_boarding_tooltip_arrow)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSingleOnBoardingIcon() {
        onView(withId(R.id.single_on_boarding_icon)).check(matches(isDisplayed()))
    }
}
