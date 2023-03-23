package com.tarasovvp.smartblocker.authorization.login

import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.TestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withBackgroundTint
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.orZero
import com.tarasovvp.smartblocker.ui.main.authorization.login.LoginFragment
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
class LoginInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var navController: TestNavHostController? = null

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext?.let {
            navController = TestNavHostController(it)
        }
        launchFragmentInHiltContainer<LoginFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.loginFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        Intents.init()
    }

    /**
     * Check main layout is visible
     */
    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed()))
    }

    /**
     * Check main title is visible with correct text
     */
    @Test
    fun checkLoginMainTitle() {
        onView(withId(R.id.login_main_title)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_entrance)))
    }

    /**
     * Check google title is visible with correct text
     */
    @Test
    fun checkLoginGoogleTitle() {
        onView(withId(R.id.login_google_title)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_with_google_account)))
    }

    /**
     * Check google button is visible with correct text
     */
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

    /**
     * Check login divider is visible with correct line color and with correct text
     */
    @Test
    fun checkLoginDivider() {
        onView(withId(R.id.login_left_divider)).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().targetContext, R.color.cornflower_blue))))
        onView(withId(R.id.login_divider_title)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_or)))
        onView(withId(R.id.login_right_divider)).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().targetContext, (R.color.cornflower_blue)))))
    }

    /**
     * Check email input is visible with correct hint, after text typing check with correct text
     */
    @Test
    fun checkLoginEmailInput() {
        onView(withId(R.id.login_email_input_container)).check(matches(isDisplayed()))
        onView(withId(R.id.login_email_input)).check(matches(isDisplayed())).check(matches(withHint(R.string.authorization_email)))
            .perform(typeText(TEST_EMAIL)).check(matches(withText(TEST_EMAIL)))
    }

    /**
     * Check password input is visible with correct hint, after text typing check with correct text
     */
    @Test
    fun checkLoginPasswordInput() {
        onView(withId(R.id.login_password_input_container)).check(matches(isDisplayed()))
        onView(withId(R.id.login_password_input)).check(matches(isDisplayed())).check(matches(withHint(R.string.authorization_password)))
            .perform(replaceText(TEST_PASSWORD)).check(matches(withText(TEST_PASSWORD)))
    }

    /**
     * Check forgot password button is visible with correct text and text color, after clicking check forgot password dialog displaying
     */
    @Test
    fun checkLoginForgotPassword() {
        onView(withId(R.id.login_forgot_password)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_forgot_password)))
            .check(matches(withTextColor(R.color.button_bg))).perform(click())
        assertEquals(R.id.forgotPasswordDialog, navController?.currentDestination?.id.orZero())
    }

    /**
     * Check sign up button is visible with correct text and text color, after clicking check sign up screen displaying
     */
    @Test
    fun checkLoginSignUp() {
        onView(withId(R.id.login_sign_up)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_sign_up)))
            .check(matches(withTextColor(R.color.button_bg))).perform(click())
        assertEquals(R.id.signUpFragment, navController?.currentDestination?.id.orZero())
    }

    /**
     * Check login enter button is visible correct text, text color, background tint and disable,
     * enter only email check button is disable, enter only password check button is disable,
     * enter email and password check button is enable, perform click
     */
    @Test
    fun checkLoginEnter() {
        onView(withId(R.id.login_enter)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_enter)))
            .check(matches(not(isEnabled()))).check(matches(withTextColor(R.color.inactive_bg))).check(matches(withBackgroundTint(ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().targetContext, R.color.transparent))))
        onView(withId(R.id.login_email_input)).perform(replaceText(TEST_EMAIL))
        onView(withId(R.id.login_enter)).check(matches(not(isEnabled())))
        onView(withId(R.id.login_email_input)).perform(replaceText(String.EMPTY))
        onView(withId(R.id.login_password_input)).perform(replaceText(TEST_PASSWORD))
        onView(withId(R.id.login_enter)).check(matches(not(isEnabled())))
        onView(withId(R.id.login_email_input)).perform(replaceText(TEST_EMAIL))
        onView(withId(R.id.login_enter))
            .check(matches(isEnabled())).check(matches(withTextColor(R.color.white))).check(matches(withBackgroundTint(ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().targetContext, R.color.button_bg))))
        assertEquals(R.id.listBlockerFragment, navController?.currentDestination?.id.orZero())
    }

    /**
     * Check unauthorized divider is visible with correct line color and with correct text
     */
    @Test
    fun checkLoginDividerUnauthorized() {
        onView(withId(R.id.login_left_divider_unauthorized)).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().targetContext, R.color.cornflower_blue))))
        onView(withId(R.id.login_divider_title_unauthorized)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_or)))
        onView(withId(R.id.login_right_divider_unauthorized)).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().targetContext, R.color.cornflower_blue))))
    }

    /**
     * Check continue button without acc is visible with correct text, after clicking check unauthorized enter dialog displaying
     */
    @Test
    fun checkLoginContinueWithoutAcc() {
        onView(withId(R.id.login_continue_without_acc)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_continue_without_account)))
            .check(matches(isEnabled())).perform(click())
        assertEquals(R.id.unauthorizedEnterDialog, navController?.currentDestination?.id.orZero())
    }

    @After
    fun tearDown() {
        navController = null
        Intents.release()
    }
}
