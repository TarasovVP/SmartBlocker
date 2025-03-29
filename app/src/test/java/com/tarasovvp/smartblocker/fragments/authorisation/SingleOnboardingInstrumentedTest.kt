package com.tarasovvp.smartblocker.fragments.authorisation

import android.os.Build
import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.OnBoarding
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withDrawable
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ON_BOARDING_PAGE
import com.tarasovvp.smartblocker.presentation.main.authorization.onboarding.SingleOnBoardingFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.O_MR1],
    application = HiltTestApplication::class,
)
class SingleOnboardingInstrumentedTest : BaseFragmentUnitTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var onBoardingPage: OnBoarding? = null

    @Before
    override fun setUp() {
        super.setUp()
        onBoardingPage =
            when {
                name.methodName.contains("Intro") -> OnBoarding.ONBOARDING_INTRO
                name.methodName.contains("FilterCondition") -> OnBoarding.ONBOARDING_FILTER_CONDITIONS
                name.methodName.contains("Info") -> OnBoarding.ONBOARDING_INFO
                else -> OnBoarding.ONBOARDING_PERMISSIONS
            }
        launchFragmentInHiltContainer<SingleOnBoardingFragment>(
            fragmentArgs =
                bundleOf(
                    ON_BOARDING_PAGE to onBoardingPage,
                ),
        ) {}
    }

    @Test
    fun checkOnBoardingIntroTitle() {
        onView(withId(R.id.single_on_boarding_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(onBoardingPage?.description(targetContext).toString())))
    }

    @Test
    fun checkOnBoardingIntroTooltipArrow() {
        onView(withId(R.id.single_on_boarding_tooltip_arrow))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkOnBoardingIntroIcon() {
        onView(withId(R.id.single_on_boarding_icon))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(onBoardingPage?.mainImage())))
    }

    @Test
    fun checkOnBoardingIntroTabs() {
        onView(withId(R.id.single_on_boarding_tab_layout))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(onBoardingPage?.tabImage())))
    }

    @Test
    fun checkOnBoardingFilterConditionTitle() {
        onView(withId(R.id.single_on_boarding_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(onBoardingPage?.description(targetContext).toString())))
    }

    @Test
    fun checkOnBoardingFilterConditionTooltipArrow() {
        onView(withId(R.id.single_on_boarding_tooltip_arrow))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkOnBoardingFilterConditionIcon() {
        onView(withId(R.id.single_on_boarding_icon))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(onBoardingPage?.mainImage())))
    }

    @Test
    fun checkOnBoardingFilterConditionTabs() {
        onView(withId(R.id.single_on_boarding_tab_layout))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(onBoardingPage?.tabImage())))
    }

    @Test
    fun checkOnBoardingInfoTitle() {
        onView(withId(R.id.single_on_boarding_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(onBoardingPage?.description(targetContext).toString())))
    }

    @Test
    fun checkOnBoardingInfoTooltipArrow() {
        onView(withId(R.id.single_on_boarding_tooltip_arrow))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkOnBoardingInfoIcon() {
        onView(withId(R.id.single_on_boarding_icon))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(onBoardingPage?.mainImage())))
    }

    @Test
    fun checkOnBoardingInfoTabs() {
        onView(withId(R.id.single_on_boarding_tab_layout))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(onBoardingPage?.tabImage())))
    }

    @Test
    fun checkOnBoardingPermissionTitle() {
        onView(withId(R.id.single_on_boarding_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(onBoardingPage?.description(targetContext).toString())))
    }

    @Test
    fun checkOnBoardingPermissionTooltipArrow() {
        onView(withId(R.id.single_on_boarding_tooltip_arrow))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkOnBoardingPermissionIcon() {
        onView(withId(R.id.single_on_boarding_icon))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(onBoardingPage?.mainImage())))
    }

    @Test
    fun checkOnBoardingPermissionTabs() {
        onView(withId(R.id.single_on_boarding_tab_layout))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(onBoardingPage?.tabImage())))
    }
}
