package com.tarasovvp.smartblocker.settings.settingstheme

import androidx.appcompat.app.AppCompatDelegate
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.settings.settingstheme.SettingsThemeFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsThemeInstrumentedTest : BaseInstrumentedTest() {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private var appThemeLiveData: MutableLiveData<Int>? = null

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SettingsThemeFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsThemeFragment)
            Navigation.setViewNavController(requireView(), navController)
            appThemeLiveData = (this as? SettingsThemeFragment)?.viewModel?.appThemeLiveData
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkAppTheme() {
        onView(withId(R.id.app_theme_group))
            .check(matches(isDisplayed()))
            .check(matches(hasChildCount(3)))
    }

    @Test
    fun checkAppThemeDay() {
        onView(withId(R.id.app_theme_day)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_theme_day)))
            check(
                matches(
                    if (appThemeLiveData?.getOrAwaitValue() == AppCompatDelegate.MODE_NIGHT_NO) {
                        isChecked()
                    } else {
                        not(
                            isChecked(),
                        )
                    },
                ),
            )
            perform(click())
            check(
                matches(
                    if (appThemeLiveData?.getOrAwaitValue() == AppCompatDelegate.MODE_NIGHT_NO) {
                        isChecked()
                    } else {
                        not(
                            isChecked(),
                        )
                    },
                ),
            )
        }
    }

    @Test
    fun checkAppThemeNight() {
        onView(withId(R.id.app_theme_night)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_theme_night)))
            check(
                matches(
                    if (appThemeLiveData?.getOrAwaitValue() == AppCompatDelegate.MODE_NIGHT_YES) {
                        isChecked()
                    } else {
                        not(
                            isChecked(),
                        )
                    },
                ),
            )
            perform(click())
            check(
                matches(
                    if (appThemeLiveData?.getOrAwaitValue() == AppCompatDelegate.MODE_NIGHT_YES) {
                        isChecked()
                    } else {
                        not(
                            isChecked(),
                        )
                    },
                ),
            )
        }
    }

    @Test
    fun checkAppAuto() {
        onView(withId(R.id.app_theme_auto)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_theme_auto)))
            check(
                matches(
                    if (appThemeLiveData?.getOrAwaitValue() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                        isChecked()
                    } else {
                        not(
                            isChecked(),
                        )
                    },
                ),
            )
            perform(click())
            check(
                matches(
                    if (appThemeLiveData?.getOrAwaitValue() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                        isChecked()
                    } else {
                        not(
                            isChecked(),
                        )
                    },
                ),
            )
        }
    }
}
