package com.tarasovvp.smartblocker.number.list.list_filter

import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.Suppress
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListBlockerFragment
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListPermissionFragment
import com.tarasovvp.smartblocker.waitUntilViewIsDisplayed
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@Suppress
@HiltAndroidTest
open class BaseListFilterInstrumentedTest: BaseInstrumentedTest() {

    private var filterList: ArrayList<FilterWithCountryCode>? = null
    private var filterIndexList: ArrayList<Int> = arrayListOf()
    private var fragment: Fragment? = null

    @Before
    override fun setUp() {
        super.setUp()
        filterList = if (this is EmptyListFilterInstrumentedTest) arrayListOf() else TestUtils.filterWithFilterList().onEach {
            it.filter?.filterType = if (this is ListBlockerInstrumentedTest) BLOCKER else PERMISSION
        }
        launchFragmentInHiltContainer<ListPermissionFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listPermissionFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            (this as? ListBlockerFragment)?.apply {
                filterIndexList = filterIndexes ?: arrayListOf()
                viewModel.filterListLiveData.postValue(filterList)
            }
            (this as? ListPermissionFragment)?.apply {
                filterIndexList = filterIndexes ?: arrayListOf()
                viewModel.filterListLiveData.postValue(filterList)
            }
        }
        waitUntilViewIsDisplayed(if (this is EmptyListFilterInstrumentedTest) withId(R.id.list_filter_empty) else withText(filterList?.get(0)?.filter?.filter))
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkListFilterCheck() {
        onView(withId(R.id.list_filter_check)).check(matches(isDisplayed()))

    }

    @Test
    fun checkListFilterInfo() {
        onView(withId(R.id.list_filter_info)).check(matches(isDisplayed()))

    }

    @Test
    fun checkListFilterEmpty() {
        onView(withId(R.id.list_filter_empty)).check(matches(isDisplayed()))
    }

    @Test
    fun checkFabFull() {
        onView(withId(R.id.fab_full)).check(matches(isDisplayed()))
    }

    @Test
    fun checkFabStart() {
        onView(withId(R.id.fab_start)).check(matches(isDisplayed()))
    }

    @Test
    fun checkFabContain() {
        onView(withId(R.id.fab_contain)).check(matches(isDisplayed()))
    }

    @Test
    fun checkFabNew() {
        onView(withId(R.id.fab_new)).check(matches(isDisplayed()))
    }

    @Test
    fun checkListFilterRefresh() {
        onView(withId(R.id.list_filter_refresh)).check(matches(isDisplayed()))
    }

    @Test
    fun checkListFilterRecyclerView() {
        onView(withId(R.id.list_filter_recycler_view)).check(matches(isDisplayed()))
    }
}
