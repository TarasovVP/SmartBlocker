package com.tarasovvp.smartblocker.number.list.list_call

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//TODO not finished
@androidx.test.filters.Suppress
@HiltAndroidTest
class ListCallInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<ListCallFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listCallFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkListCallCheck() {
        onView(withId(R.id.list_call_check)).check(matches(isDisplayed()))

    }

    @Test
    fun checkListCallInfo() {
        onView(withId(R.id.list_call_info)).check(matches(isDisplayed()))

    }

    @Test
    fun checkListCallEmpty() {
        //TODO
        //onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
    }

    @Test
    fun checkListCallRefresh() {
        onView(withId(R.id.list_call_refresh)).check(matches(isDisplayed()))
    }

    @Test
    fun checkListCallRecyclerView() {
        onView(withId(R.id.list_call_recycler_view)).check(matches(isDisplayed()))
    }
}
