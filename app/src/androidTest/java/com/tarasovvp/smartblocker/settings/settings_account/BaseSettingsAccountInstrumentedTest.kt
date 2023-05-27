package com.tarasovvp.smartblocker.settings.settings_account

import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.IS_LOG_OUT
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountFragment
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import androidx.test.filters.Suppress
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.TestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.TestUtils.withBitmap
import com.tarasovvp.smartblocker.utils.extensions.getInitialDrawable
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.nameInitial
import io.mockk.every
import io.mockk.mockk

@Suppress
@HiltAndroidTest
open class BaseSettingsAccountInstrumentedTest: BaseInstrumentedTest() {


    private val mockFirebaseAuth: FirebaseAuth = mockk()

    @Before
    override fun setUp() {
        super.setUp()
        every { mockFirebaseAuth.currentUser } returns if (this is SettingsAccountInstrumentedTest) mockk() else null
        if (this is SettingsAccountInstrumentedTest) every { mockFirebaseAuth.currentUser?.email } returns TEST_EMAIL
        launchFragmentInHiltContainer<SettingsAccountFragment> {
            (this as SettingsAccountFragment).firebaseAuth = mockFirebaseAuth
            this.initViews()
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
            .check(matches(if (mockFirebaseAuth.currentUser.isNotNull()) isDisplayed() else not(isDisplayed())))
    }

    @Test
    fun checkSettingsAccountAvatar() {
        onView(withId(R.id.settings_account_avatar)).apply {
            if (mockFirebaseAuth.currentUser.isNotNull()) {
                check(matches(isDisplayed()))
                check(matches(withBitmap(targetContext?.getInitialDrawable( mockFirebaseAuth.currentUser?.email.nameInitial())?.toBitmap())))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkSettingsAccountName() {
        onView(withId(R.id.settings_account_name)).apply {
            if (mockFirebaseAuth.currentUser.isNotNull()) {
                check(matches(isDisplayed()))
                check(matches(withText(TEST_EMAIL)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkSettingsAccountLogOut() {
        onView(withId(R.id.settings_account_log_out)).apply {
            if (mockFirebaseAuth.currentUser.isNotNull()) {
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
            if (mockFirebaseAuth.currentUser.isNotNull()) {
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
            if (mockFirebaseAuth.currentUser.isNotNull()) {
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
            if (mockFirebaseAuth.currentUser.isNotNull()) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                onView(withId(R.id.empty_state_description)).check(matches(isDisplayed())).check(matches(withText(EmptyState.EMPTY_STATE_ACCOUNT.description())))
                onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
                onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
            }
        }
    }

    @Test
    fun checkSettingsAccountLogin() {
        onView(withId(R.id.settings_account_login)).apply {
            if (mockFirebaseAuth.currentUser.isNotNull()) {
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
