package com.tarasovvp.smartblocker.settings.settings_language

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.settings.settings_language.SettingsLanguageFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsLanguageInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var navController: TestNavHostController? = null

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext?.let {
            navController = TestNavHostController(it)
        }
        launchFragmentInHiltContainer<SettingsLanguageFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsLanguageFragment)
            Navigation.setViewNavController(requireView(), navController)
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
    }
}
