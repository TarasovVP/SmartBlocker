package com.tarasovvp.smartblocker.authorization.onboarding

import androidx.core.os.bundleOf
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ON_BOARDING_PAGE
import com.tarasovvp.smartblocker.domain.enums.OnBoarding
import com.tarasovvp.smartblocker.presentation.main.authorization.onboarding.SingleOnBoardingFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SingleOnboardingInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SingleOnBoardingFragment>(fragmentArgs = bundleOf(ON_BOARDING_PAGE to OnBoarding.ONBOARDING_INTRO)) {}
    }

    @Test
    fun checkSingleOnBoardingTitle() {
        //TODO
        //onView(withId(R.id.single_on_boarding_title)).check(matches(isDisplayed()))

    }

    @Test
    fun checkSingleOnBoardingTooltipArrow() {
        //TODO
        //onView(withId(R.id.single_on_boarding_tooltip_arrow)).check(matches(isDisplayed()))

    }

    @Test
    fun checkSingleOnBoardingIcon() {
        //TODO
        //onView(withId(R.id.single_on_boarding_icon)).check(matches(isDisplayed()))
    }
}
