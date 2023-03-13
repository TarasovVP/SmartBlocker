package com.tarasovvp.smartblocker.settings.settings_account

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.ui.main.settings.settings_account.SettingsAccountFragment
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
open class SettingsAccountInstrumentedTest {

    private var navController: TestNavHostController? = null
    private var scenario: FragmentScenario<SettingsAccountFragment>? = null

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
            navController?.setCurrentDestination(R.id.settingsAccountFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkSettingsAccountContainer() {
        onView(withId(R.id.settings_account_container)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSettingsAccountAvatar() {
        onView(withId(R.id.settings_account_avatar)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkSettingsAccountName() {
        onView(withId(R.id.settings_account_container)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_account_name)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsAccountLogOut() {
        onView(withId(R.id.settings_account_log_out)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsAccountChangePassword() {
        onView(withId(R.id.settings_account_change_password)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsAccountDelete() {
        onView(withId(R.id.settings_account_delete)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkIncludeEmptyState() {
        onView(withId(R.id.include_empty_state)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkSettingsAccountLogin() {
        onView(withId(R.id.settings_account_login)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        navController = null
        scenario = null
    }
}
