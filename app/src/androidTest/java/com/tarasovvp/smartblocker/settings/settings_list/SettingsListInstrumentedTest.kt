package com.tarasovvp.smartblocker.settings.settings_list

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.ui.main.settings.settings_list.SettingsListFragment
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
open class SettingsListInstrumentedTest {

    private var navController: TestNavHostController? = null
    private var scenario: FragmentScenario<SettingsListFragment>? = null

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
            navController?.setCurrentDestination(R.id.settingsListFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSettingsBlocker() {
        onView(withId(R.id.settings_blocker)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSettingsAccount() {
        onView(withId(R.id.settings_account)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsLanguage() {
        onView(withId(R.id.settings_language)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsTheme() {
        onView(withId(R.id.settings_theme)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsReview() {
        onView(withId(R.id.settings_review)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsPrivacy() {
        onView(withId(R.id.settings_privacy)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        navController = null
        scenario = null
    }
}
