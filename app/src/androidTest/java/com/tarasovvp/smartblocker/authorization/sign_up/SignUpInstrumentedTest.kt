package com.tarasovvp.smartblocker.authorization.sign_up

import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundTint
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.presentation.main.authorization.sign_up.SignUpFragment
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SignUpInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SignUpFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.signUpFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())

    }

    @Test
    fun checkSignUpTitle() {
        onView(withId(R.id.sign_up_title)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_sign_up)))

    }

    @Test
    fun checkSignUpEmail() {
        onView(withId(R.id.sign_up_email_container)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_up_email)).check(matches(isDisplayed())).check(matches(withHint(R.string.authorization_email)))
            .perform(typeText(TEST_EMAIL)).check(matches(withText(TEST_EMAIL)))
    }

    @Test
    fun checkSignUpPassword() {
        onView(withId(R.id.sign_up_password_container)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_up_password)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSignUpEntranceTitle() {
        onView(withId(R.id.sign_up_entrance_title)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_entrance_title)))
        onView(withId(R.id.sign_up_entrance)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.authorization_entrance)))
            .check(matches(withTextColor(R.color.button_bg))).perform(click())
        assertEquals(R.id.onBoardingFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkSignUpContinue() {
        onView(withId(R.id.sign_up_continue)).check(matches(isDisplayed())).check(matches(withText(R.string.authorization_signing_up)))
            .check(matches(not(isEnabled()))).check(matches(withTextColor(R.color.inactive_bg))).check(matches(
               withBackgroundTint(ContextCompat.getColor(targetContext, R.color.transparent))))
        onView(withId(R.id.sign_up_email)).perform(ViewActions.replaceText(TEST_EMAIL))
        onView(withId(R.id.sign_up_continue)).check(matches(not(isEnabled())))
        onView(withId(R.id.sign_up_email)).perform(ViewActions.replaceText(String.EMPTY))
        onView(withId(R.id.sign_up_password)).perform(ViewActions.replaceText(TestUtils.TEST_PASSWORD))
        onView(withId(R.id.sign_up_continue)).check(matches(not(isEnabled())))
        onView(withId(R.id.sign_up_email)).perform(ViewActions.replaceText(TEST_EMAIL))
        onView(withId(R.id.sign_up_continue))
            .check(matches(isEnabled())).check(matches(withTextColor(R.color.white)))
            .check(matches(withBackgroundTint(ContextCompat.getColor(targetContext, R.color.button_bg)))).perform(click())
        //TODO implement correct destination checking
        //assertEquals(R.id.listBlockerFragment, navController?.currentDestination?.id)
    }
}
