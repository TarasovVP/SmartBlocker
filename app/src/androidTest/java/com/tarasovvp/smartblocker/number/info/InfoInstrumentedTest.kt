package com.tarasovvp.smartblocker.number.info

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.domain.models.InfoData
import com.tarasovvp.smartblocker.presentation.main.number.info.InfoFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//TODO not finished
@androidx.test.filters.Suppress
@HiltAndroidTest
class InfoInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<InfoFragment> (fragmentArgs = bundleOf("info" to InfoData())) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.infoFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkInfoWebView() {
        onView(withId(R.id.info_web_view)).check(matches(isDisplayed()))
    }
}
