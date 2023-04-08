package com.tarasovvp.smartblocker.number.details

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//TODO not finished
@androidx.test.filters.Suppress
@HiltAndroidTest
class SingleDetailsInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<SingleDetailsFragment> (fragmentArgs = bundleOf(NUMBER_TYPE to FilteredCallWithFilter::class.simpleName.orEmpty())) {
            navController?.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkSingleFilterDetailsList() {
        onView(withId(R.id.single_filter_details_list)).check(matches(isDisplayed()))

    }

    @Test
    fun checkFilterDetailsNumberListEmpty() {
        //TODO
        //onView(withId(R.id.filter_details_number_list_empty)).check(matches(isDisplayed()))

    }
}
