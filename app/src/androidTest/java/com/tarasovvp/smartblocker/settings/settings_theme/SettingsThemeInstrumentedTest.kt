package com.tarasovvp.smartblocker.settings.settings_theme

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.ui.main.settings.settings_theme.SettingsThemeFragment
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
open class SettingsThemeInstrumentedTest {

    private var navController: TestNavHostController? = null
    private var scenario: FragmentScenario<SettingsThemeFragment>? = null

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
            navController?.setCurrentDestination(R.id.settingsThemeFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
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
        scenario = null
    }
}
