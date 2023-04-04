package com.tarasovvp.smartblocker.settings.settings_account

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.TestUtils.IS_LOG_OUT
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountFragment
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseSettingsAccountInstrumentedTest: BaseInstrumentedTest() {

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SettingsAccountFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsAccountFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkSettingsAccountContainer() {
        onView(withId(R.id.settings_account_container))
            .check(matches(if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) isDisplayed() else not(isDisplayed())))
    }

    @Test
    fun checkSettingsAccountAvatar() {
        onView(withId(R.id.settings_account_avatar)).apply {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                check(matches(isDisplayed()))
                //TODO implement bitmap checking
                //check(matches(withDrawable()))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkSettingsAccountName() {
        onView(withId(R.id.settings_account_name)).apply {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                check(matches(isDisplayed()))
                check(matches(withText(SmartBlockerApp.instance?.auth?.currentUser?.email)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkSettingsAccountLogOut() {
        onView(withId(R.id.settings_account_log_out)).apply {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.settings_account_log_out_title)))
                perform(click())
                assertEquals(R.id.accountActionDialog, navController?.currentDestination?.id)
                assertEquals(true, navController?.backStack?.last()?.arguments?.getBoolean(IS_LOG_OUT))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkSettingsAccountChangePassword() {
        onView(withId(R.id.settings_account_change_password)).apply {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.settings_account_change_password_title)))
                perform(click())
                assertEquals(R.id.changePasswordDialog, navController?.currentDestination?.id)
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkSettingsAccountDelete() {
        onView(withId(R.id.settings_account_delete)).apply {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.settings_account_delete_title)))
                perform(click())
                assertEquals(R.id.accountActionDialog, navController?.currentDestination?.id)
                assertEquals(false, navController?.backStack?.last()?.arguments?.getBoolean(IS_LOG_OUT))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkIncludeEmptyState() {
        onView(withId(R.id.include_empty_state)).apply {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                onView(withId(R.id.empty_state_description)).check(matches(isDisplayed())).check(matches(withText(EmptyState.EMPTY_STATE_ACCOUNT.description)))
                onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
                onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
            }
        }
    }

    @Test
    fun checkSettingsAccountLogin() {
        onView(withId(R.id.settings_account_login)).apply {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.authorization_enter)))
                perform(click())
                assertEquals(R.id.loginFragment, navController?.currentDestination?.id)
            }
        }
    }
}
