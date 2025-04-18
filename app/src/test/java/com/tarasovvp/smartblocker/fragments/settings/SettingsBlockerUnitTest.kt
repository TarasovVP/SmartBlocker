package com.tarasovvp.smartblocker.fragments.settings

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.firebase.FirebaseApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.presentation.main.settings.settingsblocker.SettingsBlockerFragment
import com.tarasovvp.smartblocker.presentation.uimodels.CountryCodeUIModel
import com.tarasovvp.smartblocker.utils.extensions.flagEmoji
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertEquals
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
class SettingsBlockerUnitTest : BaseFragmentUnitTest() {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private var blockerTurnOnLiveData: MutableLiveData<Boolean>? = null
    private var blockHiddenLiveData: MutableLiveData<Boolean>? = null
    private var currentCountryCodeLiveData: MutableLiveData<CountryCodeUIModel>? = null

    @Before
    override fun setUp() {
        super.setUp()
        FirebaseApp.initializeApp(targetContext)
        launchFragmentInHiltContainer<SettingsBlockerFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.settingsBlockerFragment)
            Navigation.setViewNavController(requireView(), navController)
            (this as? SettingsBlockerFragment)?.apply {
                blockerTurnOnLiveData = viewModel.blockerTurnOnLiveData
                blockHiddenLiveData = viewModel.blockHiddenLiveData
                currentCountryCodeLiveData = viewModel.currentCountryCodeLiveData
            }
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
    fun checkSettingsBlockerTurnOnSwitch() {
        val blockerTurnOn = blockerTurnOnLiveData?.getOrAwaitValue().isTrue()
        onView(withId(R.id.settings_blocker_switch)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_blocker_title)))
            check(matches(if (blockerTurnOn) isChecked() else not(isChecked())))
            perform(click())
            blockerTurnOnLiveData?.postValue(blockerTurnOn.not())
            check(
                matches(
                    if (blockerTurnOnLiveData?.getOrAwaitValue().isTrue()) {
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
    fun checkSettingsBlockerTurnOnDivider() {
        onView(withId(R.id.settings_blocker_divider))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withBackgroundColor(
                        ContextCompat.getColor(
                            targetContext,
                            R.color.light_steel_blue,
                        ),
                    ),
                ),
            )
    }

    @Test
    fun checkSettingsBlockerTurnOnDescribe() {
        val blockerTurnOn = blockerTurnOnLiveData?.getOrAwaitValue().isTrue()
        onView(withId(R.id.settings_blocker_describe)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(if (blockerTurnOn) R.string.settings_blocker_on else R.string.settings_blocker_off)))
            onView(withId(R.id.settings_blocker_switch)).perform(click())
            blockerTurnOnLiveData?.postValue(blockerTurnOn.not())
            check(
                matches(
                    withText(
                        if (blockerTurnOnLiveData?.getOrAwaitValue()
                                .isTrue()
                        ) {
                            R.string.settings_blocker_on
                        } else {
                            R.string.settings_blocker_off
                        },
                    ),
                ),
            )
        }
    }

    @Test
    fun checkSettingsBlockerHiddenContainer() {
        onView(withId(R.id.settings_blocker_hidden_container)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsBlockerHiddenSwitch() {
        val blockHidden = blockHiddenLiveData?.getOrAwaitValue().isTrue()
        onView(withId(R.id.settings_blocker_hidden_switch)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(R.string.settings_block_hidden_title)))
            check(matches(if (blockHidden) isChecked() else not(isChecked())))
            perform(click())
            blockHiddenLiveData?.postValue(blockHidden.not())
            check(
                matches(
                    if (blockHiddenLiveData?.getOrAwaitValue().isTrue()) {
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
    fun checkSettingsBlockerHiddenDivider() {
        onView(withId(R.id.settings_blocker_hidden_divider))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withBackgroundColor(
                        ContextCompat.getColor(
                            targetContext,
                            R.color.light_steel_blue,
                        ),
                    ),
                ),
            )
    }

    @Test
    fun checkSettingsBlockerHiddenDescribe() {
        val blockHidden = blockHiddenLiveData?.getOrAwaitValue().isTrue()
        onView(withId(R.id.settings_blocker_hidden_describe)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(if (blockHidden) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)))
            onView(withId(R.id.settings_blocker_hidden_switch)).perform(click())
            blockHiddenLiveData?.postValue(blockHidden.not())
            check(
                matches(
                    withText(
                        if (blockHiddenLiveData?.getOrAwaitValue()
                                .isTrue()
                        ) {
                            R.string.settings_block_hidden_on
                        } else {
                            R.string.settings_block_hidden_off
                        },
                    ),
                ),
            )
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
        val countryCode = currentCountryCodeLiveData?.getOrAwaitValue()
        onView(withId(R.id.settings_blocker_country))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withText(
                        String.format(
                            "%s %s",
                            countryCode?.country?.uppercase()?.flagEmoji(),
                            countryCode?.country?.uppercase(),
                        ),
                    ),
                ),
            )
            .perform(click())
        assertEquals(R.id.countryCodeSearchDialog, navController?.currentDestination?.id)
    }

    @Test
    fun checkSettingsBlockerCountryDivider() {
        onView(withId(R.id.settings_blocker_country_divider))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withBackgroundColor(
                        ContextCompat.getColor(
                            targetContext,
                            R.color.light_steel_blue,
                        ),
                    ),
                ),
            )
    }

    @Test
    fun checkSettingsBlockerCountryDescribe() {
        onView(withId(R.id.settings_blocker_country_describe))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.settings_block_country_description)))
    }
}
