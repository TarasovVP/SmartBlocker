package com.tarasovvp.smartblocker.number.details.details_filter

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.presentation.main.number.details.details_filter.DetailsFilterFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DetailsFilterInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<DetailsFilterFragment> (fragmentArgs = bundleOf("filterWithCountryCode" to FilterWithCountryCode(filter = Filter()))) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.detailsFilterFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkDetailsFilterItemFilter() {
        onView(withId(R.id.details_filter_item_filter)).check(matches(isDisplayed()))

    }

    @Test
    fun checkDetailsFilterDeleteFilter() {
        onView(withId(R.id.details_filter_delete_filter)).check(matches(isDisplayed()))

    }

    @Test
    fun checkDetailsFilterChangeFilter() {
        onView(withId(R.id.details_filter_change_filter)).check(matches(isDisplayed()))
    }

    @Test
    fun checkDetailsFilterTabs() {
        onView(withId(R.id.details_filter_tabs)).check(matches(isDisplayed()))
    }

    @Test
    fun checkDetailsFilterViewPager() {
        onView(withId(R.id.details_filter_view_pager)).check(matches(isDisplayed()))
    }
}
