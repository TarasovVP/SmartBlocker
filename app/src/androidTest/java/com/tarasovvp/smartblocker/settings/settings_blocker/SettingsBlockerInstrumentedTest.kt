package com.tarasovvp.smartblocker.settings.settings_blocker

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsBlockerInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SettingsBlockerFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsBlockerFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkSettingsBlockerContainer() {
        onView(withId(R.id.settings_blocker_container)).check(matches(isDisplayed()))

    }

    @Test
    fun checkSettingsBlockerSwitch() {
        onView(withId(R.id.settings_blocker_switch)).check(matches(isDisplayed()))

    }

    @Test
    fun checkSettingsBlockerDivider() {
        onView(withId(R.id.settings_blocker_divider)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerDescribe() {
        onView(withId(R.id.settings_blocker_describe)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerHiddenContainer() {
        onView(withId(R.id.settings_blocker_hidden_container)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerHiddenSwitch() {
        onView(withId(R.id.settings_blocker_hidden_switch)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerHiddenDivider() {
        onView(withId(R.id.settings_blocker_hidden_divider)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerHiddenDescribe() {
        onView(withId(R.id.settings_blocker_hidden_describe)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerCountryContainer() {
        onView(withId(R.id.settings_blocker_country_container)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerCountrySwitch() {
        onView(withId(R.id.settings_blocker_country_switch)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerCountry() {
        onView(withId(R.id.settings_blocker_country)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerCountryDivider() {
        onView(withId(R.id.settings_blocker_country_divider)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerCountryDescribe() {
        onView(withId(R.id.settings_blocker_country_describe)).check(matches(isDisplayed()))
    }
}
