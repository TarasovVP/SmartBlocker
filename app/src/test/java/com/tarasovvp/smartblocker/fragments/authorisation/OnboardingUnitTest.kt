package com.tarasovvp.smartblocker.fragments.authorisation

import android.os.Build
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.OnBoarding
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.authorization.onboarding.OnBoardingAdapter
import com.tarasovvp.smartblocker.presentation.main.authorization.onboarding.OnBoardingFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertEquals
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
class OnboardingUnitTest : BaseFragmentUnitTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var adapter: OnBoardingAdapter? = null

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<OnBoardingFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.onBoardingFragment)
            Navigation.setViewNavController(requireView(), navController)
            adapter = (this as OnBoardingFragment).adapter
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkOnBoardingViewPagerIntro() {
        onView(withId(R.id.on_boarding_view_pager)).check(matches(isDisplayed()))
        onView(withId(R.id.on_boarding_button)).apply {
            check(matches(isEnabled()))
            check(matches(withText(R.string.button_next)))
            assertEquals(OnBoarding.ONBOARDING_INTRO, adapter?.getItemBundle(0))
        }
    }

    @Test
    fun checkOnBoardingViewPagerFilterConditions() {
        onView(withId(R.id.on_boarding_view_pager)).check(matches(isDisplayed()))
        onView(withId(R.id.on_boarding_button)).apply {
            check(matches(isEnabled()))
            check(matches(withText(R.string.button_next)))
            perform(click())
            check(matches(isEnabled()))
            check(matches(withText(R.string.button_next)))
            assertEquals(OnBoarding.ONBOARDING_FILTER_CONDITIONS, adapter?.getItemBundle(1))
        }
    }

    @Test
    fun checkOnBoardingViewPagerInfo() {
        onView(withId(R.id.on_boarding_button)).apply {
            check(matches(isEnabled()))
            check(matches(withText(R.string.button_next)))
            perform(click())
            perform(click())
            check(matches(isEnabled()))
            check(matches(withText(R.string.button_next)))
            assertEquals(OnBoarding.ONBOARDING_INFO, adapter?.getItemBundle(2))
        }
    }

    @Test
    fun checkOnBoardingViewPagerPermissions() {
        onView(withId(R.id.on_boarding_view_pager)).check(matches(isDisplayed()))
        onView(withId(R.id.on_boarding_button)).apply {
            check(matches(isEnabled()))
            check(matches(withText(R.string.button_next)))
            perform(click())
            perform(click())
            perform(click())
            check(matches(isEnabled()))
            check(matches(withText(R.string.button_accept)))
            assertEquals(OnBoarding.ONBOARDING_PERMISSIONS, adapter?.getItemBundle(3))
        }
    }
}
