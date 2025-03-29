package com.tarasovvp.smartblocker.settings.settings_account

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.Suppress
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountFragment
import com.tarasovvp.smartblocker.utils.extensions.currentUserEmail
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test

@Suppress
@HiltAndroidTest
open class BaseSettingsAccountInstrumentedTest : BaseInstrumentedTest() {
    private val mockFirebaseAuth: FirebaseAuth = mockk()

    @Before
    override fun setUp() {
        super.setUp()
        FirebaseApp.initializeApp(targetContext)
        every { mockFirebaseAuth.currentUser } returns mockk()
        mockkStatic("com.tarasovvp.smartblocker.utils.extensions.DeviceExtensionsKt")
        every { mockFirebaseAuth.isAuthorisedUser() } returns (this@BaseSettingsAccountInstrumentedTest is SettingsAccountInstrumentedTest)
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
        onView(withId(R.id.settings_account_container)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsAccountName() {
        onView(withId(R.id.settings_account_name)).apply {
            check(matches(isDisplayed()))
            check(
                matches(
                    withText(
                        if (mockFirebaseAuth.isAuthorisedUser()) {
                            mockFirebaseAuth.currentUser?.currentUserEmail()
                        } else {
                            targetContext.getString(
                                R.string.settings_account_unauthorised,
                            )
                        },
                    ),
                ),
            )
        }
    }

    @Test
    fun checkSettingsAccountLogOut() {
        onView(withId(R.id.settings_account_log_out)).apply {
            check(matches(isDisplayed()))
            check(
                matches(
                    withText(
                        if (mockFirebaseAuth.isAuthorisedUser()) R.string.settings_account_log_out_title else R.string.settings_account_unauthorised_log_out_title,
                    ),
                ),
            )
            perform(click())
            assertEquals(R.id.logOutDialog, navController?.currentDestination?.id)
        }
    }

    @Test
    fun checkSettingsAccountChangePassword() {
        onView(withId(R.id.settings_account_change_password)).apply {
            if (mockFirebaseAuth.isAuthorisedUser()) {
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
            if (mockFirebaseAuth.isAuthorisedUser()) {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.settings_account_delete_title)))
                perform(click())
                assertEquals(R.id.deleteAccountDialog, navController?.currentDestination?.id)
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkIncludeEmptyState() {
        onView(withId(R.id.include_empty_state)).apply {
            if (mockFirebaseAuth.isAuthorisedUser()) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                onView(withId(R.id.empty_state_description)).check(matches(isDisplayed()))
                    .check(matches(withText(EmptyState.EMPTY_STATE_ACCOUNT.description())))
                onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed()))
                    .check(matches(withDrawable(R.drawable.ic_empty_state)))
            }
        }
    }

    @Test
    fun checkSettingsAccountSignUp() {
        onView(withId(R.id.settings_account_sign_up)).apply {
            if (mockFirebaseAuth.isAuthorisedUser()) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.authorization_sign_up)))
                perform(click())
                assertEquals(R.id.settingsSignUpFragment, navController?.currentDestination?.id)
            }
        }
    }
}
