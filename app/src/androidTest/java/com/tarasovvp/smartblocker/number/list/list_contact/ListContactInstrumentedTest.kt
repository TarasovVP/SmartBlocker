package com.tarasovvp.smartblocker.number.list.list_contact

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//TODO not finished
@androidx.test.filters.Suppress
@HiltAndroidTest
class ListContactInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<ListContactFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listContactFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkListContactCheck() {
        onView(withId(R.id.list_contact_check)).check(matches(isDisplayed()))

    }

    @Test
    fun checkListContactInfo() {
        onView(withId(R.id.list_contact_info)).check(matches(isDisplayed()))

    }

    @Test
    fun checkListContactEmpty() {
        //TODO
        //onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
    }

    @Test
    fun checkListContactRefresh() {
        onView(withId(R.id.list_contact_refresh)).check(matches(isDisplayed()))
    }

    @Test
    fun checkListContactRecyclerView() {
        onView(withId(R.id.list_contact_recycler_view)).check(matches(isDisplayed()))
    }
}
