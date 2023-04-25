package com.tarasovvp.smartblocker.settings.settings_blocker

import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerFragment
import com.tarasovvp.smartblocker.utils.extensions.flagEmoji
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsBlockerInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    @Before
    override fun setUp() {
        super.setUp()
        SharedPrefs.countryCode = CountryCode()
        launchFragmentInHiltContainer<SettingsBlockerFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsBlockerFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkSettingsBlockerContainer() {
        onView(withId(R.id.settings_blocker_container)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerSwitch() {
        onView(withId(R.id.settings_blocker_switch)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_blocker_title)))
            check(matches(if (SharedPrefs.smartBlockerTurnOff.isTrue()) not(isChecked()) else isChecked()))
            perform(click())
            check(matches(if (SharedPrefs.smartBlockerTurnOff.isTrue()) not(isChecked()) else isChecked()))
        }
    }

    @Test
    fun checkSettingsBlockerDivider() {
        onView(withId(R.id.settings_blocker_divider))
            .check(matches(isDisplayed()))
            .check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue))))
    }

    @Test
    fun checkSettingsBlockerDescribe() {
        onView(withId(R.id.settings_blocker_describe)).apply {
            check(matches(isDisplayed()))
            check(matches(withText( if (SharedPrefs.smartBlockerTurnOff.isTrue()) R.string.settings_blocker_off else R.string.settings_blocker_on)))
            onView(withId(R.id.settings_blocker_switch)).perform(click())
            check(matches(withText( if (SharedPrefs.smartBlockerTurnOff.isTrue()) R.string.settings_blocker_off else R.string.settings_blocker_on)))
        }
    }

    @Test
    fun checkSettingsBlockerHiddenContainer() {
        onView(withId(R.id.settings_blocker_hidden_container)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerHiddenSwitch() {
        onView(withId(R.id.settings_blocker_hidden_switch)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_block_hidden_title)))
            check(matches(if (SharedPrefs.blockHidden.isTrue()) isChecked() else not(isChecked())))
            perform(click())
            //TODO error throwing
            //check(matches(if (SharedPrefs.blockHidden.isTrue()) isChecked() else not(isChecked())))
        }
    }

    @Test
    fun checkSettingsBlockerHiddenDivider() {
        onView(withId(R.id.settings_blocker_hidden_divider))
            .check(matches(isDisplayed()))
            .check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue))))
    }

    @Test
    fun checkSettingsBlockerHiddenDescribe() {
        onView(withId(R.id.settings_blocker_hidden_describe)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(if (SharedPrefs.blockHidden.isTrue()) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)))
            perform(click())
            check(matches(withText(if (SharedPrefs.blockHidden.isTrue()) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)))
        }
    }

    @Test
    fun checkSettingsBlockerCountryContainer() {
        onView(withId(R.id.settings_blocker_country_container)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerCountrySwitch() {
        onView(withId(R.id.settings_blocker_country_switch))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.settings_block_country_title)))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun checkSettingsBlockerCountry() {
        onView(withId(R.id.settings_blocker_country))
            .check(matches(isDisplayed()))
            .check(matches(withText(String.format("%s %s", SharedPrefs.countryCode?.country?.uppercase()?.flagEmoji(), SharedPrefs.countryCode?.country?.uppercase()))))
            .perform(click())
        assertEquals(R.id.countryCodeSearchDialog, navController?.currentDestination?.id)
    }

    @Test
    fun checkSettingsBlockerCountryDivider() {
        onView(withId(R.id.settings_blocker_country_divider))
            .check(matches(isDisplayed()))
            .check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue))))
    }

    @Test
    fun checkSettingsBlockerCountryDescribe() {
        onView(withId(R.id.settings_blocker_country_describe))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.settings_block_country_description)))
    }
}
