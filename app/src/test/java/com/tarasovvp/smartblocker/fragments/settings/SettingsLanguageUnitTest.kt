package com.tarasovvp.smartblocker.fragments.settings

import android.os.Build
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
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withDrawable
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_EN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_RU
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_UK
import com.tarasovvp.smartblocker.presentation.main.settings.settings_language.SettingsLanguageFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.O_MR1],
    application = HiltTestApplication::class,
)
class SettingsLanguageUnitTest : BaseFragmentUnitTest() {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private var appLanguageLiveData: MutableLiveData<String>? = null

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SettingsLanguageFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsLanguageFragment)
            Navigation.setViewNavController(requireView(), navController)
            appLanguageLiveData =
                (this as? SettingsLanguageFragment)?.viewModel?.appLanguageLiveData
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkSettingsLanguagesRg() {
        onView(withId(R.id.settings_languages_rg)).check(matches(isDisplayed())).check(
            matches(
                hasChildCount(3),
            ),
        )
    }

    @Test
    fun checkSettingsLanguagesRbRu() {
        onView(withId(R.id.settings_languages_rb_ru)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_language_russian)))
            check(matches(withDrawable(R.drawable.ic_flag_ru)))
            check(
                matches(
                    if (appLanguageLiveData?.getOrAwaitValue() == APP_LANG_RU) {
                        isChecked()
                    } else {
                        not(
                            isChecked(),
                        )
                    },
                ),
            )
            perform(click())
            check(matches(isChecked()))
        }
    }

    @Test
    fun checkSettingsLanguagesRbUk() {
        onView(withId(R.id.settings_languages_rb_uk)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_language_ukrainian)))
            check(matches(withDrawable(R.drawable.ic_flag_ua)))
            check(
                matches(
                    if (appLanguageLiveData?.getOrAwaitValue() == APP_LANG_UK) {
                        isChecked()
                    } else {
                        not(
                            isChecked(),
                        )
                    },
                ),
            )
            perform(click())
            check(matches(isChecked()))
        }
    }

    @Test
    fun checkSettingsLanguagesRbEn() {
        onView(withId(R.id.settings_languages_rb_en)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_language_english)))
            check(matches(withDrawable(R.drawable.ic_flag_en)))
            check(
                matches(
                    if (appLanguageLiveData?.getOrAwaitValue() == APP_LANG_EN) {
                        isChecked()
                    } else {
                        not(
                            isChecked(),
                        )
                    },
                ),
            )
            perform(click())
            check(matches(isChecked()))
        }
    }
}
