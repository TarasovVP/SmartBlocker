package com.tarasovvp.smartblocker.settings.settings_theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.main.settings.settings_theme.SettingsThemeFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkAppTheme() {
        onView(withId(R.id.app_theme))
            .check(matches(isDisplayed()))
            .check(matches(hasChildCount(3)))
    }

    @Test
    fun checkAppThemeDay() {
        onView(withId(R.id.app_theme_day)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_theme_day)))
            check(matches(if (SharedPrefs.appTheme ==  AppCompatDelegate.MODE_NIGHT_NO) isChecked() else not(isChecked())))
            perform(click())
            check(matches(if (SharedPrefs.appTheme ==  AppCompatDelegate.MODE_NIGHT_NO) isChecked() else not(isChecked())))
        }
    }

    @Test
    fun checkAppThemeNight() {
        onView(withId(R.id.app_theme_night)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_theme_night)))
            check(matches(if (SharedPrefs.appTheme ==  AppCompatDelegate.MODE_NIGHT_YES) isChecked() else not(isChecked())))
            perform(click())
            check(matches(if (SharedPrefs.appTheme ==  AppCompatDelegate.MODE_NIGHT_YES) isChecked() else not(isChecked())))
        }
    }

    @Test
    fun checkAppAuto() {
        onView(withId(R.id.app_theme_auto)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_theme_auto)))
            check(matches(if (SharedPrefs.appTheme ==  AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) isChecked() else not(isChecked())))
            perform(click())
            check(matches(if (SharedPrefs.appTheme ==  AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) isChecked() else not(isChecked())))
        }
    }
}
