package com.tarasovvp.smartblocker.settings.settings_theme

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.ui.main.settings.settings_theme.SettingsThemeFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsThemeInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var navController: TestNavHostController? = null

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext?.let {
            navController = TestNavHostController(it)
        }
        launchFragmentInHiltContainer<SettingsThemeFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsThemeFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkAppTheme() {
        onView(withId(R.id.app_theme)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkAppThemeDay() {
        onView(withId(R.id.app_theme_day)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkAppThemeNight() {
        onView(withId(R.id.app_theme_night)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkAppAuto() {
        onView(withId(R.id.app_auto)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        navController = null
    }
}
