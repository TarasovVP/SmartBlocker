package com.tarasovvp.smartblocker.settings.settings_blocker

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.ui.main.settings.settings_blocker.SettingsBlockerFragment
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
open class SettingsBlockerInstrumentedTest {

    private var navController: TestNavHostController? = null
    private var scenario: FragmentScenario<SettingsBlockerFragment>? = null

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext?.let {
            navController = TestNavHostController(it)
        }
        scenario =
            launchFragmentInContainer(
                themeResId = R.style.Theme_SmartBlocker)
        scenario?.onFragment { fragment ->
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsBlockerFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerContainer() {
        onView(withId(R.id.settings_blocker_container)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerSwitch() {
        onView(withId(R.id.settings_blocker_switch)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerDivider() {
        onView(withId(R.id.settings_blocker_divider)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerDescribe() {
        onView(withId(R.id.settings_blocker_describe)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerHiddenContainer() {
        onView(withId(R.id.settings_blocker_hidden_container)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerHiddenSwitch() {
        onView(withId(R.id.settings_blocker_hidden_switch)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerHiddenDivider() {
        onView(withId(R.id.settings_blocker_hidden_divider)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerHiddenDescribe() {
        onView(withId(R.id.settings_blocker_hidden_describe)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerCountryContainer() {
        onView(withId(R.id.settings_blocker_country_container)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerCountrySwitch() {
        onView(withId(R.id.settings_blocker_country_switch)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerCountry() {
        onView(withId(R.id.settings_blocker_country)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerCountryDivider() {
        onView(withId(R.id.settings_blocker_country_divider)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsBlockerCountryDescribe() {
        onView(withId(R.id.settings_blocker_country_describe)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        navController = null
        scenario = null
    }
}
