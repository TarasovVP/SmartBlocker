package com.tarasovvp.smartblocker

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
    }

    /**
     *
     */
    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed()))

    }

    @After
    fun tearDown() {
        navController = null
    }
}
