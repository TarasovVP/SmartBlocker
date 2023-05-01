package com.tarasovvp.smartblocker.authorization.login

import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.TestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withBackgroundTint
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.presentation.main.authorization.login.LoginFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<LoginFragment> {
                navController?.setGraph(R.navigation.navigation)
                navController?.setCurrentDestination(R.id.loginFragment)
                Navigation.setViewNavController(requireView(), navController)
        }
        Intents.init()
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkLoginMainTitle() {
        onView(withId(R.id.login_main_title)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_entrance)))
    }

    @Test
    fun checkLoginGoogleTitle() {
        onView(withId(R.id.login_google_title)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_with_google_account)))
    }

    @Test
    fun checkLoginGoogleAuth() {
        onView(withId(R.id.login_google_auth)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_enter))).check(
            matches(withDrawable(R.drawable.ic_logo_google))).perform(click())
        intended(
            allOf(
                hasAction("com.google.android.gms.auth.GOOGLE_SIGN_IN"),
                hasComponent("com.google.android.gms.auth.api.signin.internal.SignInHubActivity")
            )
        )
    }

    @Test
    fun checkLoginDivider() {
        onView(withId(R.id.login_left_divider)).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.cornflower_blue))))
        onView(withId(R.id.login_divider_title)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_or)))
        onView(withId(R.id.login_right_divider)).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, (R.color.cornflower_blue)))))
    }

    @Test
    fun checkLoginEmailInput() {
        onView(withId(R.id.login_email_input_container)).check(matches(isDisplayed()))
        onView(withId(R.id.login_email_input)).check(matches(isDisplayed())).check(matches(withHint(R.string.authorization_email)))
            .perform(typeText(TEST_EMAIL)).check(matches(withText(TEST_EMAIL)))
    }

    @Test
    fun checkLoginPasswordInput() {
        onView(withId(R.id.login_password_input_container)).check(matches(isDisplayed()))
        onView(withId(R.id.login_password_input)).check(matches(isDisplayed())).check(matches(withHint(R.string.authorization_password)))
            .perform(replaceText(TEST_PASSWORD)).check(matches(withText(TEST_PASSWORD)))
    }

    @Test
    fun checkLoginForgotPassword() {
        onView(withId(R.id.login_forgot_password)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_forgot_password)))
            .check(matches(withTextColor(R.color.button_bg))).perform(click())
        assertEquals(R.id.forgotPasswordDialog, navController?.currentDestination?.id)
    }

    @Test
    fun checkLoginSignUp() {
        onView(withId(R.id.login_sign_up)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_sign_up)))
            .check(matches(withTextColor(R.color.button_bg))).perform(click())
        assertEquals(R.id.signUpFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkLoginEnter() {
        onView(withId(R.id.login_enter)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_enter)))
            .check(matches(not(isEnabled()))).check(matches(withTextColor(R.color.inactive_bg))).check(matches(withBackgroundTint(ContextCompat.getColor(targetContext, R.color.transparent))))
        onView(withId(R.id.login_email_input)).perform(replaceText(TEST_EMAIL))
        onView(withId(R.id.login_enter)).check(matches(not(isEnabled())))
        onView(withId(R.id.login_email_input)).perform(replaceText(String.EMPTY))
        onView(withId(R.id.login_password_input)).perform(replaceText(TEST_PASSWORD))
        onView(withId(R.id.login_enter)).check(matches(not(isEnabled())))
        onView(withId(R.id.login_email_input)).perform(replaceText(TEST_EMAIL))
        onView(withId(R.id.login_enter))
            .check(matches(isEnabled())).check(matches(withTextColor(R.color.white)))
            .check(matches(withBackgroundTint(ContextCompat.getColor(targetContext, R.color.button_bg))))
            .perform(click())
        //TODO implement correct destination checking
        //assertEquals(R.id.listBlockerFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkLoginDividerUnauthorized() {
        onView(withId(R.id.login_left_divider_unauthorized)).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.cornflower_blue))))
        onView(withId(R.id.login_divider_title_unauthorized)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_or)))
        onView(withId(R.id.login_right_divider_unauthorized)).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.cornflower_blue))))
    }

    @Test
    fun checkLoginContinueWithoutAcc() {
        onView(withId(R.id.login_continue_without_acc)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_continue_without_account)))
            .check(matches(isEnabled())).perform(click())
        assertEquals(R.id.unauthorizedEnterDialog, navController?.currentDestination?.id)
    }

    @After
    override fun tearDown() {
        Intents.release()
    }
}
