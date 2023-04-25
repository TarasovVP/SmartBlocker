package com.tarasovvp.smartblocker.settings.settings_list

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.presentation.main.settings.settings_list.SettingsListFragment
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsListInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SettingsListFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsListFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())

    }

    @Test
    fun checkSettingsBlocker() {
        onView(withId(R.id.settings_blocker)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.settings_blocker)))
            .check(matches(withDrawable(R.drawable.ic_settings_blocker))).perform(click())
        assertEquals(R.id.settingsBlockerFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkSettingsAccount() {
        onView(withId(R.id.settings_account)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.settings_account)))
            .check(matches(withDrawable(R.drawable.ic_settings_account))).perform(click())
        assertEquals(R.id.settingsAccountFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkSettingsLanguage() {
        onView(withId(R.id.settings_language)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.settings_language)))
            .check(matches(withDrawable(R.drawable.ic_settings_language))).perform(click())
        assertEquals(R.id.settingsLanguageFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkSettingsTheme() {
        onView(withId(R.id.settings_theme)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.settings_theme)))
            .check(matches(withDrawable(R.drawable.ic_settings_theme))).perform(click())
        assertEquals(R.id.settingsThemeFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkSettingsReview() {
        onView(withId(R.id.settings_review)).apply {
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.settings_review)))
                check(matches(withDrawable(R.drawable.ic_settings_review)))
                perform(click())
                assertEquals(R.id.settingsReviewDialog, navController?.currentDestination?.id)
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkSettingsPrivacy() {
        onView(withId(R.id.settings_privacy)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.settings_privacy)))
            .check(matches(withDrawable(R.drawable.ic_settings_privacy))).perform(click())
        assertEquals(R.id.settingsPrivacyFragment, navController?.currentDestination?.id)
    }
}
