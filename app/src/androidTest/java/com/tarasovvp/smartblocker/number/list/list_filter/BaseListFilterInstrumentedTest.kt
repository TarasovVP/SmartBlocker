package com.tarasovvp.smartblocker.number.list.list_filter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.Suppress
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.numberDataFilteringText
import com.tarasovvp.smartblocker.utils.extensions.orZero
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.Test

@Suppress
@HiltAndroidTest
open class BaseListFilterInstrumentedTest: BaseInstrumentedTest() {

    protected var fragment: Fragment? = null
    protected var filterList: ArrayList<FilterWithCountryCode>? = null
    protected var filterIndexList: ArrayList<Int> = arrayListOf()

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkListFilterCheck() {
        onView(withId(R.id.list_filter_check)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isChecked())))
            check(matches(withText(InstrumentationRegistry.getInstrumentation().targetContext.numberDataFilteringText(filterIndexList))))
            if (filterList.isNullOrEmpty()) {
                check(matches(not(isEnabled())))
            } else {
                check(matches(isEnabled()))
                perform(click())
                assertEquals(R.id.numberDataFilteringDialog, navController?.currentDestination?.id)
            }
        }
    }

    @Test
    fun checkListContactCheckOneFilter() {
        onView(withId(R.id.list_filter_check)).apply {
            check(matches(withText(InstrumentationRegistry.getInstrumentation().targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                        add(NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.ordinal)
                    })
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (filterList.isNullOrEmpty()) {
                        check(matches(not(isEnabled())))
                    } else {
                        check(matches(isChecked()))
                        check(matches(withText(InstrumentationRegistry.getInstrumentation().targetContext.numberDataFilteringText(filterIndexList))))
                    }
                }
            }
        }
    }

    @Test
    fun checkListContactCheckTwoFilters() {
        onView(withId(R.id.list_filter_check)).apply {
            check(matches(withText(InstrumentationRegistry.getInstrumentation().targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                        add(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal)
                        add(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal)
                    })
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (filterList.isNullOrEmpty()) {
                        check(matches(not(isEnabled())))
                    } else {
                        check(matches(isChecked()))
                        check(matches(withText(InstrumentationRegistry.getInstrumentation().targetContext.numberDataFilteringText(filterIndexList))))
                    }
                }
            }
        }
    }

    @Test
    fun checkListFilterInfo() {
        onView(withId(R.id.list_filter_info))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(R.drawable.ic_info)))
            .perform(click())
        assertEquals(R.id.infoFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkFabFull() {
        onView(withId(R.id.fab_full)).apply {
            check(matches(not(isDisplayed())))
            onView(withId(R.id.fab_new)).perform(click())
            check(matches(isDisplayed()))
            check(matches(withText(R.string.filter_condition_full)))
            check(matches(withDrawable(R.drawable.ic_condition_full)))
            perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            assertEquals(FilterWithCountryCode().apply {
                filter = Filter(conditionType = FilterCondition.FILTER_CONDITION_FULL.index,
                    filterType = if (this@BaseListFilterInstrumentedTest is ListPermissionInstrumentedTest) PERMISSION else BLOCKER)
                countryCode = CountryCode()
            }, navController?.backStack?.last()?.arguments?.get("filterWithCountryCode"))
        }
    }

    @Test
    fun checkFabStart() {
        onView(withId(R.id.fab_start)).apply {
            check(matches(not(isDisplayed())))
            onView(withId(R.id.fab_new)).perform(click())
            check(matches(isDisplayed()))
            check(matches(withText(R.string.filter_condition_start)))
            check(matches(withDrawable(R.drawable.ic_condition_start)))
            perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            assertEquals(FilterWithCountryCode().apply {
                filter = Filter(conditionType = FilterCondition.FILTER_CONDITION_START.index,
                    filterType = if (this@BaseListFilterInstrumentedTest is ListPermissionInstrumentedTest) PERMISSION else BLOCKER)
                countryCode = CountryCode()
            }, navController?.backStack?.last()?.arguments?.get("filterWithCountryCode"))
        }
    }

    @Test
    fun checkFabContain() {
        onView(withId(R.id.fab_contain)).apply {
            check(matches(not(isDisplayed())))
            onView(withId(R.id.fab_new)).perform(click())
            check(matches(isDisplayed()))
            check(matches(withText(R.string.filter_condition_contain)))
            check(matches(withDrawable(R.drawable.ic_condition_contain)))
            perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            assertEquals(FilterWithCountryCode().apply {
                filter = Filter(conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.index,
                    filterType = if (this@BaseListFilterInstrumentedTest is ListPermissionInstrumentedTest) PERMISSION else BLOCKER)
                countryCode = CountryCode()
            }, navController?.backStack?.last()?.arguments?.get("filterWithCountryCode"))
        }
    }

    @Test
    fun checkFabNew() {
        onView(withId(R.id.fab_new)).apply {
            check(matches(isDisplayed()))
            check(matches(withDrawable(R.drawable.ic_create)))
            perform(click())
            onView(withId(R.id.fab_full)).check(matches(isDisplayed()))
            onView(withId(R.id.fab_start)).check(matches(isDisplayed()))
            onView(withId(R.id.fab_contain)).check(matches(isDisplayed()))
            check(matches(withDrawable(R.drawable.ic_close)))
            perform(click())
            onView(withId(R.id.fab_full)).check(matches(not(isDisplayed())))
            onView(withId(R.id.fab_start)).check(matches(not(isDisplayed())))
            onView(withId(R.id.fab_contain)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun checkListFilterRefresh() {
        if (filterList.isNullOrEmpty()) {
            onView(withId(R.id.list_filter_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_filter_refresh)).check(matches(isDisplayed()))
            onView(withId(R.id.list_filter_refresh)).perform(ViewActions.swipeDown())
        }
    }

    @Test
    fun checkListFilterRecyclerView() {
        if (filterList.isNullOrEmpty()) {
            onView(withId(R.id.list_filter_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_filter_empty)).check(matches(not(isDisplayed())))
            onView(withId(R.id.list_filter_recycler_view))
                .check(matches(isDisplayed()))
                .check(matches(TestUtils.hasItemCount(filterList?.size.orZero())))
        }
    }

    @Test
    fun checkListFilterEmpty() {
        if (filterList.isNullOrEmpty()) {
            onView(withId(R.id.list_filter_empty)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_state_description)).check(matches(isDisplayed()))
                .check(matches(withText(EmptyState.EMPTY_STATE_BLOCKERS.description)))
            onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
            onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
        } else {
            onView(withId(R.id.list_filter_empty)).check(matches(not(isDisplayed())))
        }
    }
}
