package com.tarasovvp.smartblocker.settings.settings_language

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.BuildConfig
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_EN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_RU
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_UK
import com.tarasovvp.smartblocker.data.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.main.settings.settings_language.SettingsLanguageFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import timber.log.Timber

@HiltAndroidTest
class SettingsLanguageInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SettingsLanguageFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsLanguageFragment)
            Navigation.setViewNavController(requireView(), navController)
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkSettingsLanguagesRg() {
        onView(withId(R.id.settings_languages_rg)).check(matches(isDisplayed())).check(matches(
            hasChildCount(3)
        ))

    }

    @Test
    fun checkSettingsLanguagesRbRu() {
        onView(withId(R.id.settings_languages_rb_ru)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_language_russian)))
            check(matches(withDrawable(R.drawable.ic_flag_ru)))
            check(matches(if (SharedPrefs.appLang == APP_LANG_RU) isChecked() else not(isChecked())))
            perform(click())
            check(matches(if (SharedPrefs.appLang == APP_LANG_RU) isChecked() else not(isChecked())))
        }
    }

    @Test
    fun checkSettingsLanguagesRbUk() {
        onView(withId(R.id.settings_languages_rb_uk)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_language_ukrainian)))
            check(matches(withDrawable(R.drawable.ic_flag_ua)))
            check(matches(if (SharedPrefs.appLang == APP_LANG_UK) isChecked() else not(isChecked())))
            perform(click())
            check(matches(if (SharedPrefs.appLang == APP_LANG_UK) isChecked() else not(isChecked())))
        }
    }

    @Test
    fun checkSettingsLanguagesRbEn() {
        onView(withId(R.id.settings_languages_rb_en)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_language_english)))
            check(matches(withDrawable(R.drawable.ic_flag_en)))
            check(matches(if (SharedPrefs.appLang == APP_LANG_EN) isChecked() else not(isChecked())))
            perform(click())
            check(matches(if (SharedPrefs.appLang == APP_LANG_EN) isChecked() else not(isChecked())))
        }
    }
}
