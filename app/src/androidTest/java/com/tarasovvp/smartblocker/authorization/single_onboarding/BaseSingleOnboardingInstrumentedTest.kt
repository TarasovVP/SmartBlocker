package com.tarasovvp.smartblocker.authorization.single_onboarding

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
import com.tarasovvp.smartblocker.utils.extensions.orZero
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseSingleOnboardingInstrumentedTest: BaseInstrumentedTest() {

    private var onBoardingPage: OnBoarding? = null

    @Before
    override fun setUp() {
        super.setUp()
        onBoardingPage = when(this) {
            is SingleOnboardingIntroInstrumentedTest -> OnBoarding.ONBOARDING_INTRO
            is SingleOnboardingFilterConditionsInstrumentedTest -> OnBoarding.ONBOARDING_FILTER_CONDITIONS
            is SingleOnboardingInfoInstrumentedTest -> OnBoarding.ONBOARDING_INFO
            else -> OnBoarding.ONBOARDING_INTRO
        }
        launchFragmentInHiltContainer<SingleOnBoardingFragment>(fragmentArgs = bundleOf(ON_BOARDING_PAGE to onBoardingPage)) {}
    }

    @Test
    fun checkOnBoardingTitle() {
        onView(withId(R.id.single_on_boarding_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(onBoardingPage?.description.orZero())))
    }

    @Test
    fun checkOnBoardingTooltipArrow() {
        onView(withId(R.id.single_on_boarding_tooltip_arrow))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
    }

    @Test
    fun checkOnBoardingIcon() {
        onView(withId(R.id.single_on_boarding_icon))
            .check(matches(isDisplayed()))
                //TODO drawable
            //.check(matches(withDrawable(onBoardingPage?.mainImage)))
    }

    @Test
    fun checkOnBoardingTabs() {
        onView(withId(R.id.single_on_boarding_tab_layout))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(onBoardingPage?.tabImage)))
    }
}