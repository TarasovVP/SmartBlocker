package com.tarasovvp.smartblocker.number.list.list_call

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.ui.main.number.list.list_call.ListCallFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ListCallInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var navController: TestNavHostController? = null

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext?.let {
            navController = TestNavHostController(it)
        }
        launchFragmentInHiltContainer<ListCallFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listCallFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkListCallCheck() {
        onView(withId(R.id.list_call_check)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkListCallInfo() {
        onView(withId(R.id.list_call_info)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkListCallEmpty() {
        onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkListCallRefresh() {
        onView(withId(R.id.list_call_refresh)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkListCallRecyclerView() {
        onView(withId(R.id.list_call_recycler_view)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        navController = null
    }
}
