package com.tarasovvp.smartblocker.settings.settings_language

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.ui.main.settings.settings_language.SettingsLanguageFragment
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
open class SettingsLanguageInstrumentedTest {

    private var navController: TestNavHostController? = null
    private var scenario: FragmentScenario<SettingsLanguageFragment>? = null

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
            navController?.setCurrentDestination(R.id.settingsLanguageFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkSettingsLanguagesRg() {
        onView(withId(R.id.settings_languages_rg)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSettingsLanguagesRbRu() {
        onView(withId(R.id.settings_languages_rb_ru)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSettingsLanguagesRbUk() {
        onView(withId(R.id.settings_languages_rb_uk)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsLanguagesRbEn() {
        onView(withId(R.id.settings_languages_rb_en)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        navController = null
        scenario = null
    }
}
