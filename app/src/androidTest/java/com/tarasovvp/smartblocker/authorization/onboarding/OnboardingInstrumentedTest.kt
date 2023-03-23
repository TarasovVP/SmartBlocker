package com.tarasovvp.smartblocker.authorization.onboarding

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.authorization.onboarding.OnBoardingFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class OnboardingInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var navController: TestNavHostController? = null

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext?.let {
            navController = TestNavHostController(it)
        }
        launchFragmentInHiltContainer<OnBoardingFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.onBoardingFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkOnBoardingViewPager() {
        //TODO
        //onView(withId(R.id.on_boarding_view_pager)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkOnBoardingButton() {
        //TODO
        //onView(withId(R.id.on_boarding_button)).check(matches(isDisplayed()))

    }

    @After
    fun tearDown() {
        navController = null
    }
}
