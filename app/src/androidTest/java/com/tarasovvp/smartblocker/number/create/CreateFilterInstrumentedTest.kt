package com.tarasovvp.smartblocker.number.create

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.ui.main.authorization.login.LoginFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class CreateFilterInstrumentedTest {

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
    }

    /**
     *
     */
    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkLoginMainTitle() {
        onView(withId(R.id.login_main_title)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkLoginGoogleTitle() {
        onView(withId(R.id.login_google_title)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkLoginGoogleAuth() {
        onView(withId(R.id.login_google_auth)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkLoginDivider() {
        onView(withId(R.id.login_left_divider)).check(matches(isDisplayed()))
        onView(withId(R.id.login_divider_title)).check(matches(isDisplayed()))
        onView(withId(R.id.login_right_divider)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkLoginEmailInput() {
        onView(withId(R.id.login_email_input_container)).check(matches(isDisplayed()))
        onView(withId(R.id.login_email_input)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkLoginPasswordInput() {
        onView(withId(R.id.login_password_input_container)).check(matches(isDisplayed()))
        onView(withId(R.id.login_password_input)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkLoginForgotPassword() {
        onView(withId(R.id.login_forgot_password)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkLoginSignUp() {
        onView(withId(R.id.login_sign_up)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkLoginContinue() {
        onView(withId(R.id.login_continue)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkLoginDividerUnauthorized() {
        onView(withId(R.id.login_left_divider_unauthorized)).check(matches(isDisplayed()))
        onView(withId(R.id.login_divider_title_unauthorized)).check(matches(isDisplayed()))
        onView(withId(R.id.login_right_divider_unauthorized)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkLoginContinueWithoutAcc() {
        onView(withId(R.id.login_continue_without_acc)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        navController = null
    }
}
