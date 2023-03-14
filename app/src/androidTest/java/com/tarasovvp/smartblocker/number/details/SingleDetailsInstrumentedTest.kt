package com.tarasovvp.smartblocker.number.details

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.models.FilteredCallWithFilter
import com.tarasovvp.smartblocker.ui.main.number.details.SingleDetailsFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SingleDetailsInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var navController: TestNavHostController? = null

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext?.let {
            navController = TestNavHostController(it)
        }
        launchFragmentInHiltContainer<SingleDetailsFragment> (fragmentArgs = bundleOf(NUMBER_TYPE to FilteredCallWithFilter::class.simpleName.orEmpty())) {
            navController?.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkSingleFilterDetailsList() {
        onView(withId(R.id.single_filter_details_list)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkFilterDetailsNumberListEmpty() {
        onView(withId(R.id.filter_details_number_list_empty)).check(matches(isDisplayed()))

    }

    @After
    fun tearDown() {
        navController = null
    }
}
