package com.tarasovvp.smartblocker.settings.settings_account

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsAccountInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        launchFragmentInHiltContainer<SettingsAccountFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsAccountFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkSettingsAccountContainer() {
        onView(withId(R.id.settings_account_container)).check(matches(isDisplayed()))

    }

    @Test
    fun checkSettingsAccountAvatar() {
        onView(withId(R.id.settings_account_avatar)).check(matches(isDisplayed()))

    }

    @Test
    fun checkSettingsAccountName() {
        onView(withId(R.id.settings_account_container)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_account_name)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsAccountLogOut() {
        onView(withId(R.id.settings_account_log_out)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsAccountChangePassword() {
        onView(withId(R.id.settings_account_change_password)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsAccountDelete() {
        onView(withId(R.id.settings_account_delete)).check(matches(isDisplayed()))
    }

    @Test
    fun checkIncludeEmptyState() {
        onView(withId(R.id.include_empty_state)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsAccountLogin() {
        onView(withId(R.id.settings_account_login)).check(matches(isDisplayed()))
    }
}
