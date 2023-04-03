package com.tarasovvp.smartblocker.settings.settings_theme

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.settings.settings_theme.SettingsThemeFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//TODO not finished
@androidx.test.filters.Suppress
@HiltAndroidTest
class SettingsThemeInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SettingsThemeFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsThemeFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkAppTheme() {
        onView(withId(R.id.app_theme)).check(matches(isDisplayed()))

    }

    @Test
    fun checkAppThemeDay() {
        onView(withId(R.id.app_theme_day)).check(matches(isDisplayed()))

    }

    @Test
    fun checkAppThemeNight() {
        onView(withId(R.id.app_theme_night)).check(matches(isDisplayed()))
    }

    @Test
    fun checkAppAuto() {
        onView(withId(R.id.app_auto)).check(matches(isDisplayed()))
    }
}
