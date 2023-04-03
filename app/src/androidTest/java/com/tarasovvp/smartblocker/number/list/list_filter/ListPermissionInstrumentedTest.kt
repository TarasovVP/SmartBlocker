package com.tarasovvp.smartblocker.number.list.list_filter

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.Suppress
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListPermissionFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//TODO not finished
@Suppress
@HiltAndroidTest
class ListPermissionInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<ListPermissionFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listPermissionFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun check_list_filter_filter() {
        onView(withId(R.id.list_filter_filter)).check(matches(isDisplayed()))

    }

    @Test
    fun check_list_filter_info() {
        onView(withId(R.id.list_filter_info)).check(matches(isDisplayed()))

    }

    @Test
    fun check_list_filter_empty() {
        onView(withId(R.id.list_filter_empty)).check(matches(isDisplayed()))
    }

    @Test
    fun check_fab_full() {
        onView(withId(R.id.fab_full)).check(matches(isDisplayed()))
    }

    @Test
    fun check_fab_start() {
        onView(withId(R.id.fab_start)).check(matches(isDisplayed()))
    }

    @Test
    fun check_fab_contain() {
        onView(withId(R.id.fab_contain)).check(matches(isDisplayed()))
    }

    @Test
    fun check_fab_new() {
        onView(withId(R.id.fab_new)).check(matches(isDisplayed()))
    }

    @Test
    fun check_list_filter_refresh() {
        onView(withId(R.id.list_filter_refresh)).check(matches(isDisplayed()))
    }

    @Test
    fun check_list_filter_recycler_view() {
        onView(withId(R.id.list_filter_recycler_view)).check(matches(isDisplayed()))
    }
}
