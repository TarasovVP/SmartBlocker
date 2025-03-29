package com.tarasovvp.smartblocker.settings.settings_list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.presentation.main.settings.settings_list.SettingsListFragment
import com.tarasovvp.smartblocker.utils.extensions.flagDrawable
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsListInstrumentedTest : BaseInstrumentedTest() {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockFirebaseAuth: FirebaseAuth = mockk()

    private var appLanguageLiveData: MutableLiveData<String>? = null

    @Before
    override fun setUp() {
        super.setUp()
        every { mockFirebaseAuth.currentUser } returns mockk()
        mockkStatic("com.tarasovvp.smartblocker.utils.extensions.DeviceExtensionsKt")
        every { mockFirebaseAuth.isAuthorisedUser() } returns true
        launchFragmentInHiltContainer<SettingsListFragment> {
            (this as SettingsListFragment).firebaseAuth = mockFirebaseAuth
            this.initViews()
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsListFragment)
            Navigation.setViewNavController(requireView(), navController)
            appLanguageLiveData = (this as? SettingsListFragment)?.viewModel?.appLanguageLiveData
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
        val appLanguage = appLanguageLiveData?.getOrAwaitValue()
        onView(withId(R.id.settings_language)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_language)))
            check(matches(withDrawable(R.drawable.ic_settings_language)))
            appLanguage?.let {
                check(matches(withDrawable(it.flagDrawable(), 2)))
            }
            perform(click())
        }
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
        onView(withId(R.id.settings_feedback)).apply {
            if (mockFirebaseAuth.isAuthorisedUser()) {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.settings_feedback)))
                check(matches(withDrawable(R.drawable.ic_settings_feedback)))
                perform(click())
                assertEquals(R.id.settingsFeedbackDialog, navController?.currentDestination?.id)
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
