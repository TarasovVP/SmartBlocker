package com.tarasovvp.smartblocker.number.list.list_call

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallFragment
import com.tarasovvp.smartblocker.utils.extensions.numberDataFilteringText
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.waitUntilViewIsDisplayed
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseListCallInstrumentedTest: BaseInstrumentedTest() {

    private var callList: List<CallWithFilter>? = null
    private var filterIndexList: ArrayList<Int> = arrayListOf()
    private var fragment: Fragment? = null

    @Before
    override fun setUp() {
        super.setUp()
        callList = if (this is EmptyListCallInstrumentedTest) listOf() else TestUtils.callWithFilterList()
        launchFragmentInHiltContainer<ListCallFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listCallFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            (this as ListCallFragment).apply {
                filterIndexList = filterIndexes ?: arrayListOf()
                viewModel.callListLiveData.postValue(callList)
            }
        }
        waitUntilViewIsDisplayed(if (this is EmptyListCallInstrumentedTest) withId(R.id.list_call_empty) else withText(callList?.get(0)?.call?.callName))
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkListCallCheck() {
        onView(withId(R.id.list_call_check)).apply {
            check(matches(isDisplayed()))
            check(matches(Matchers.not(isChecked())))
            check(matches(withText(InstrumentationRegistry.getInstrumentation().targetContext.numberDataFilteringText(filterIndexList))))
            if (callList.isNullOrEmpty()) {
                check(matches(Matchers.not(isEnabled())))
            } else {
                check(matches(isEnabled()))
                perform(click())
                assertEquals(
                    R.id.numberDataFilteringDialog,
                    navController?.currentDestination?.id.orZero()
                )
            }
        }
    }

    @Test
    fun checkListContactCheckOneFilter() {
        onView(withId(R.id.list_call_check)).apply {
            check(matches(withText(InstrumentationRegistry.getInstrumentation().targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                        add(NumberDataFiltering.CALL_BLOCKED.ordinal)
                    })
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (callList.isNullOrEmpty()) {
                        check(matches(Matchers.not(isEnabled())))
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
        onView(withId(R.id.list_call_check)).apply {
            check(matches(withText(InstrumentationRegistry.getInstrumentation().targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                        add(NumberDataFiltering.CALL_BLOCKED.ordinal)
                        add(NumberDataFiltering.CALL_PERMITTED.ordinal)
                    })
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (callList.isNullOrEmpty()) {
                        check(matches(Matchers.not(isEnabled())))
                    } else {
                        check(matches(isChecked()))
                        check(matches(withText(InstrumentationRegistry.getInstrumentation().targetContext.numberDataFilteringText(filterIndexList))))
                    }
                }
            }
        }
    }

    @Test
    fun checkListCallInfo() {
        onView(withId(R.id.list_call_info))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(R.drawable.ic_info)))
            .perform(click())
        assertEquals(R.id.infoFragment, navController?.currentDestination?.id.orZero())

    }

    @Test
    fun checkListCallRefresh() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_call_refresh)).check(matches(isDisplayed()))
            onView(withId(R.id.list_call_refresh)).perform(ViewActions.swipeDown())
        }
    }

    @Test
    fun checkListCallRecyclerView() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_call_empty)).check(matches(Matchers.not(isDisplayed())))
            onView(withId(R.id.list_call_recycler_view))
                .check(matches(isDisplayed()))
                .check(matches(hasItemCount(callList?.size.orZero() + callList?.groupBy {
                    it.call?.dateFromCallDate().toString()
                }?.size.orZero())))
        }
    }

    @Test
    fun checkListCallEmpty() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_state_description)).check(matches(isDisplayed())).check(matches(withText(
                EmptyState.EMPTY_STATE_CALLS.description)))
            onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
            onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
        } else {
            onView(withId(R.id.list_call_empty)).check(matches(Matchers.not(isDisplayed())))
        }
    }
}
