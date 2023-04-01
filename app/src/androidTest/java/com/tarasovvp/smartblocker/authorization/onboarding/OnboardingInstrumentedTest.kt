package com.tarasovvp.smartblocker.authorization.onboarding

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.authorization.onboarding.OnBoardingFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class OnboardingInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<OnBoardingFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.onBoardingFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkOnBoardingViewPager() {
        onView(withId(R.id.on_boarding_view_pager)).check(matches(isDisplayed()))

    }

    @Test
    fun checkOnBoardingButton() {
        onView(withId(R.id.on_boarding_button)).check(matches(isDisplayed()))

    }
}
