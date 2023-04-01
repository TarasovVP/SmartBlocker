package com.tarasovvp.smartblocker.authorization.sign_up

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.authorization.sign_up.SignUpFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
        onView(withId(R.id.container)).check(matches(isDisplayed()))

    }

    @Test
    fun checkSignUpTitle() {
        onView(withId(R.id.sign_up_title)).check(matches(isDisplayed()))

    }

    @Test
    fun checkSignUpEmail() {
        onView(withId(R.id.sign_up_email_container)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_up_email)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSignUpPassword() {
        onView(withId(R.id.sign_up_password_container)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_up_password)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSignUpEntranceTitle() {
        onView(withId(R.id.sign_up_entrance_title)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_up_entrance)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSignUpContinue() {
        onView(withId(R.id.sign_up_continue)).check(matches(isDisplayed()))
    }
}
