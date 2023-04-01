package com.tarasovvp.smartblocker.number.details.details_number_data

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DetailsNumberInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<DetailsNumberDataFragment> (fragmentArgs = bundleOf("numberData" to ContactWithFilter())) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.detailsNumberDataFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed()))

    }

    @Test
    fun checkDetailsNumberDataItemContact() {
        onView(withId(R.id.details_number_data_item_contact)).check(matches(isDisplayed()))

    }

    @Test
    fun checkDetailsNumberDataCreateBlocker() {
        onView(withId(R.id.details_number_data_create_blocker)).check(matches(isDisplayed()))
    }

    @Test
    fun checkDetailsNumberDataCreatePermission() {
        onView(withId(R.id.details_number_data_create_permission)).check(matches(isDisplayed()))
    }

    @Test
    fun checkDetailsNumberDataEmpty() {
        //TODO
        //onView(withId(R.id.details_number_data_empty)).check(matches(isDisplayed()))
    }

    @Test
    fun checkDetailsNumberDataTabs() {
        onView(withId(R.id.details_number_data_tabs)).check(matches(isDisplayed()))
    }

    @Test
    fun checkDetailsNumberDataViewPager() {
        onView(withId(R.id.details_number_data_view_pager)).check(matches(isDisplayed()))
    }

    @Test
    fun checkNumberDataDetailAddFilterFull() {
        //TODO
        //onView(withId(R.id.number_data_detail_add_filter_full)).check(matches(isDisplayed()))
    }

    @Test
    fun checkNumberDataDetailAddFilterStart() {
        //TODO
        //onView(withId(R.id.number_data_detail_add_filter_start)).check(matches(isDisplayed()))
    }

    @Test
    fun checkNumberDataDetailAddFilterContain() {
        //TODO
        //onView(withId(R.id.number_data_detail_add_filter_full)).check(matches(isDisplayed()))
    }
}
