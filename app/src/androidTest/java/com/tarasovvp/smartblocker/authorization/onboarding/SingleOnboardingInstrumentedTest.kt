package com.tarasovvp.smartblocker.authorization.onboarding

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ON_BOARDING_PAGE
import com.tarasovvp.smartblocker.domain.enums.OnBoarding
import com.tarasovvp.smartblocker.presentation.main.authorization.onboarding.SingleOnBoardingFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SingleOnboardingInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkOnBoardingViewPagerIntro() {
        launchFragmentInHiltContainer<SingleOnBoardingFragment>(fragmentArgs = bundleOf(ON_BOARDING_PAGE to OnBoarding.ONBOARDING_INTRO)) {}
        onView(withId(R.id.single_on_boarding_title)).check(matches(isDisplayed())).check(matches(withText(OnBoarding.ONBOARDING_INTRO.description)))
        onView(withId(R.id.single_on_boarding_tooltip_arrow))
        onView(withId(R.id.single_on_boarding_icon)).check(matches(isDisplayed())).check(matches(isDisplayed())).check(matches(
            withDrawable(OnBoarding.ONBOARDING_INTRO.mainImage)
        ))
        onView(withId(R.id.single_on_boarding_tab_layout)).check(matches(isDisplayed())).check(matches(isDisplayed())).check(matches(
            withDrawable(OnBoarding.ONBOARDING_INTRO.tabImage)
        ))
    }

    @Test
    fun checkOnBoardingViewPagerFilterConditions() {
        launchFragmentInHiltContainer<SingleOnBoardingFragment>(fragmentArgs = bundleOf(ON_BOARDING_PAGE to OnBoarding.ONBOARDING_FILTER_CONDITIONS)) {}
        onView(withId(R.id.single_on_boarding_title)).check(matches(isDisplayed())).check(matches(withText(OnBoarding.ONBOARDING_FILTER_CONDITIONS.description)))
        onView(withId(R.id.single_on_boarding_tooltip_arrow))
        onView(withId(R.id.single_on_boarding_icon)).check(matches(isDisplayed())).check(matches(isDisplayed())).check(matches(
            withDrawable(OnBoarding.ONBOARDING_FILTER_CONDITIONS.mainImage)
        ))
        onView(withId(R.id.single_on_boarding_tab_layout)).check(matches(isDisplayed())).check(matches(isDisplayed())).check(matches(
            withDrawable(OnBoarding.ONBOARDING_FILTER_CONDITIONS.tabImage)
        ))
    }

    @Test
    fun checkOnBoardingViewPagerInfo() {
        launchFragmentInHiltContainer<SingleOnBoardingFragment>(fragmentArgs = bundleOf(ON_BOARDING_PAGE to OnBoarding.ONBOARDING_INFO)) {}
        onView(withId(R.id.single_on_boarding_title)).check(matches(isDisplayed())).check(matches(withText(OnBoarding.ONBOARDING_INFO.description)))
        onView(withId(R.id.single_on_boarding_tooltip_arrow))
        onView(withId(R.id.single_on_boarding_icon)).check(matches(isDisplayed())).check(matches(isDisplayed())).check(matches(
            withDrawable(OnBoarding.ONBOARDING_INFO.mainImage)
        ))
        onView(withId(R.id.single_on_boarding_tab_layout)).check(matches(isDisplayed())).check(matches(isDisplayed())).check(matches(
            withDrawable(OnBoarding.ONBOARDING_INFO.tabImage)
        ))
    }

    @Test
    fun checkOnBoardingViewPagerPermissions() {
        launchFragmentInHiltContainer<SingleOnBoardingFragment>(fragmentArgs = bundleOf(ON_BOARDING_PAGE to OnBoarding.ONBOARDING_PERMISSIONS)) {}
        onView(withId(R.id.single_on_boarding_title)).check(matches(isDisplayed())).check(matches(withText(OnBoarding.ONBOARDING_PERMISSIONS.description)))
        onView(withId(R.id.single_on_boarding_tooltip_arrow))
        onView(withId(R.id.single_on_boarding_icon)).check(matches(isDisplayed())).check(matches(isDisplayed())).check(matches(
            withDrawable(OnBoarding.ONBOARDING_PERMISSIONS.mainImage)
        ))
        onView(withId(R.id.single_on_boarding_tab_layout)).check(matches(isDisplayed())).check(matches(isDisplayed())).check(matches(
            withDrawable(OnBoarding.ONBOARDING_PERMISSIONS.tabImage)
        ))
    }
}
