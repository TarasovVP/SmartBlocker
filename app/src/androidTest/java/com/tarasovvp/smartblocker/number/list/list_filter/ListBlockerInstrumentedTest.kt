package com.tarasovvp.smartblocker.number.list.list_filter

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListBlockerFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.filters.Suppress
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.TestUtils
import org.hamcrest.Matchers

//TODO not finished
@Suppress
@HiltAndroidTest
class ListBlockerInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<ListBlockerFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listBlockerFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    /*@Test
    fun checkFilterConditionsDialog() {
        if (contactWithFilterList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_check)).check(matches(Matchers.not(isEnabled())))
        } else {
            onView(withId(R.id.list_contact_check)).check(matches(isEnabled())).perform(ViewActions.click())
            onView(withId(R.id.dialog_filter_condition_title)).check(matches(isDisplayed())).check(matches(withText(R.string.filter_condition_title)))
            onView(withId(R.id.dialog_filter_condition_full)).check(matches(isDisplayed())).check(matches(withText(R.string.filter_condition_full)))
                .check(matches(Matchers.not(isChecked()))).perform(ViewActions.click()).check(matches(isChecked()))
            onView(withId(R.id.dialog_filter_condition_start)).check(matches(isDisplayed())).check(matches(withText(R.string.filter_condition_start)))
                .check(matches(Matchers.not(isChecked()))).perform(ViewActions.click()).check(matches(isChecked()))
            onView(withId(R.id.dialog_filter_condition_contain)).check(matches(isDisplayed())).check(matches(withText(R.string.filter_condition_contain)))
                .check(matches(Matchers.not(isChecked()))).perform(ViewActions.click()).check(matches(isChecked()))
            onView(withId(R.id.dialog_filter_condition_cancel)).check(matches(isDisplayed())).check(matches(withText(R.string.button_ok))).perform(
                ViewActions.click()
            )
            onView(withId(R.id.list_contact_check)).check(matches(isDisplayed())).check(matches(withText(callFilteringText()))).perform(
                ViewActions.click()
            )
            onView(withId(R.id.dialog_filter_condition_confirm)).check(matches(isDisplayed())).check(matches(
                TestUtils.withDrawable(R.drawable.ic_close_small)
            )).perform(ViewActions.click())
            onView(withId(R.id.dialog_filter_condition_full)).perform(ViewActions.click())
            onView(withId(R.id.list_contact_check)).check(matches(withText(callFilteringText()))).perform(
                ViewActions.click()
            )
        }
    }*/

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
