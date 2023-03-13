package com.tarasovvp.smartblocker.settings.settings_privacy

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.ui.main.settings.settings_privacy.SettingsPrivacyFragment
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
open class SettingsPrivacyInstrumentedTest {

    private var navController: TestNavHostController? = null
    private var scenario: FragmentScenario<SettingsPrivacyFragment>? = null

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
            navController?.setCurrentDestination(R.id.settingsPrivacyFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkSettingsPrivacyWebView() {
        onView(withId(R.id.settings_privacy_web_view)).check(matches(isDisplayed()))

    }

    @After
    fun tearDown() {
        navController = null
        scenario = null
    }
}
