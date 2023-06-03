package com.tarasovvp.smartblocker.fragments.authorisation

import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.ScrollActions.nestedScrollTo
import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.UnitTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.UnitTestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.UnitTestUtils.withBackgroundTint
import com.tarasovvp.smartblocker.UnitTestUtils.withTextColor
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.presentation.main.authorization.login.LoginFragment
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.O_MR1],
    application = HiltTestApplication::class)
class LoginUnitTest: BaseFragmentUnitTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        FirebaseApp.initializeApp(targetContext)
        launchFragmentInHiltContainer<LoginFragment> {
            val mockGoogleSignInClient: GoogleSignInClient = mockk()
            (this as LoginFragment).googleSignInClient = mockGoogleSignInClient
            val signInIntent = Intent().apply {
                action = "com.google.android.gms.auth.GOOGLE_SIGN_IN"
                setClassName("com.google.android.gms.auth.api.signin.internal", "com.google.android.gms.auth.api.signin.internal.SignInHubActivity")
            }
            every { mockGoogleSignInClient.signInIntent } returns signInIntent
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.loginFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        Intents.init()
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkLoginMainTitle() {
        onView(withId(R.id.login_main_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.authorization_entrance)))
    }

    @Test
    fun checkLoginGoogleTitle() {
        onView(withId(R.id.login_google_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.authorization_with_google_account)))
    }

    @Test
    fun checkLoginGoogleAuth() {
        onView(withId(R.id.login_google_auth)).apply {
            check(matches(isDisplayed())).check(matches(withText(R.string.authorization_enter)))
            check(matches(UnitTestUtils.withDrawable(R.drawable.ic_logo_google)))
            perform(click())
        }
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
        onView(withId(R.id.login_email_input))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.authorization_email)))
            .perform(typeText(TEST_EMAIL))
            .check(matches(withText(TEST_EMAIL)))
    }

    @Test
    fun checkLoginPasswordInput() {
        onView(withId(R.id.login_password_input))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.authorization_password)))
            .perform(replaceText(TEST_PASSWORD))
            .check(matches(withText(TEST_PASSWORD)))
    }

    @Test
    fun checkLoginForgotPassword() {
        onView(withId(R.id.login_forgot_password)).apply {
            perform(nestedScrollTo())
            check(matches(isDisplayed()))
            check(matches(withText(R.string.authorization_forgot_password)))
            check(matches(withTextColor(R.color.button_bg)))
            perform(click())
        }
        assertEquals(R.id.forgotPasswordDialog, navController?.currentDestination?.id)
    }

    @Test
    fun checkLoginSignUp() {
        onView(withId(R.id.login_sign_up)).apply {
            perform(nestedScrollTo())
            check(matches(isDisplayed()))
            check(matches(withText(R.string.authorization_sign_up)))
            check(matches(withTextColor(R.color.button_bg)))
            perform(click())
        }
        assertEquals(R.id.signUpFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkLoginEnter() {
        onView(withId(R.id.login_enter))
            .perform(nestedScrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.authorization_enter)))
            .check(matches(not(isEnabled())))
            .check(matches(withTextColor(R.color.inactive_bg)))
            .check(matches(withBackgroundTint(ContextCompat.getColor(targetContext, R.color.transparent))))
        onView(withId(R.id.login_email_input)).perform(replaceText(TEST_EMAIL))
        onView(withId(R.id.login_enter)).check(matches(not(isEnabled())))
        onView(withId(R.id.login_email_input)).perform(replaceText(String.EMPTY))
        onView(withId(R.id.login_password_input)).perform(replaceText(TEST_PASSWORD))
        onView(withId(R.id.login_enter)).check(matches(not(isEnabled())))
        onView(withId(R.id.login_email_input)).perform(replaceText(TEST_EMAIL))
        onView(withId(R.id.login_enter)).apply {
            check(matches(isEnabled())).check(matches(withTextColor(R.color.white)))
            check(matches(withBackgroundTint(ContextCompat.getColor(targetContext, R.color.button_bg))))
            perform(click())
        }
    }

    @Test
    fun checkLoginDividerUnauthorized() {
        onView(withId(R.id.login_left_divider_unauthorized)).perform(nestedScrollTo()).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.cornflower_blue))))
        onView(withId(R.id.login_divider_title_unauthorized)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_or)))
        onView(withId(R.id.login_right_divider_unauthorized)).check(matches(isDisplayed())).check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.cornflower_blue))))
    }

    @Test
    fun checkLoginContinueWithoutAcc() {
        onView(withId(R.id.login_continue_without_acc)).apply {
            perform(nestedScrollTo())
            check(matches(isDisplayed()))
            check(matches(withText(R.string.authorization_continue_without_account)))
            check(matches(isEnabled()))
            perform(click())
        }
        assertEquals(R.id.unauthorizedEnterDialog, navController?.currentDestination?.id)
    }

    @After
    override fun tearDown() {
        Intents.release()
    }
}
