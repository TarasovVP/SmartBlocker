package com.tarasovvp.smartblocker.number.info

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.clickLinkWithText
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.presentation.main.number.info.InfoFragment
import com.tarasovvp.smartblocker.utils.extensions.htmlWithImages
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class InfoInstrumentedTest : BaseInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<InfoFragment>(fragmentArgs = bundleOf("info" to Info.INFO_FILTER)) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.infoFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkInfoWebView() {
        targetContext.apply {
            onView(withId(R.id.info_web_view))
                .check(matches(isDisplayed()))
                .check(matches(withText(htmlWithImages(getString(Info.INFO_FILTER.description())).toString())))
                .perform(clickLinkWithText("Permission"))
        }
        assertEquals(R.id.infoFragment, navController?.currentDestination?.id)
        assertEquals(Info.INFO_PERMISSION, navController?.backStack?.last()?.arguments?.get("info"))
    }
}
