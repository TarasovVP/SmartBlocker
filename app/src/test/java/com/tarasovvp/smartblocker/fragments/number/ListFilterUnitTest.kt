package com.tarasovvp.smartblocker.fragments.number

import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.firebase.FirebaseApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.UnitTestUtils.EMPTY
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.FILTER_WITH_COUNTRY_CODE
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.FILTER_WITH_FILTERED_NUMBER
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.atPosition
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.filterWithFilteredNumberUIModelList
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.hasItemCount
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.waitFor
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withDrawable
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withTextColor
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListBlockerFragment
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.O_MR1],
    application = HiltTestApplication::class)
class ListFilterUnitTest: BaseFragmentUnitTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var fragment: Fragment? = null
    private var filterList: ArrayList<FilterWithFilteredNumberUIModel>? = null
    private var filterIndexList: ArrayList<Int> = arrayListOf()

    @Before
    override fun setUp() {
        super.setUp()
        FirebaseApp.initializeApp(targetContext)
        filterList = if (name.methodName.contains(EMPTY)) arrayListOf() else filterWithFilteredNumberUIModelList().onEach {
            it.filterType =  BLOCKER
        }
        launchFragmentInHiltContainer<ListBlockerFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listPermissionFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            (this as? ListBlockerFragment)?.apply {
                filterIndexList = filterIndexes ?: arrayListOf()
                viewModel.filterListLiveData.postValue(filterList)
            }
        }
        onView(isRoot()).perform(waitFor(3000))
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkListFilterCheck() {
        onView(withId(R.id.list_filter_check)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isChecked())))
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
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
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            filterIndexList.add(NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.ordinal)
            fragment?.setFragmentResult(
                FILTER_CONDITION_LIST,
                bundleOf(FILTER_CONDITION_LIST to filterIndexList)
            )
            if (filterList.isNullOrEmpty()) {
                check(matches(not(isEnabled())))
            } else {
                check(matches(isChecked()))
                check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            }
        }
    }

    @Test
    fun checkListContactCheckTwoFilters() {
        onView(withId(R.id.list_filter_check)).apply {
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            filterIndexList.add(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal)
            filterIndexList.add(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal)
            fragment?.setFragmentResult(
                FILTER_CONDITION_LIST,
                bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                    add(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal)
                    add(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal)
                })
            )
            if (filterList.isNullOrEmpty()) {
                check(matches(not(isEnabled())))
            } else {
                check(matches(isChecked()))
                check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
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
            onView(isRoot()).perform(waitFor(501))
            perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            //TODO if (this@ListFilterUnitTest is ListPermissionInstrumentedTest) PERMISSION else
            assertEquals(
                FilterWithCountryCodeUIModel(filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                filterType = BLOCKER)),
                navController?.backStack?.last()?.arguments?.parcelable(FILTER_WITH_COUNTRY_CODE))
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
            onView(isRoot()).perform(waitFor(501))
            perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            //TODO if (this@ListFilterUnitTest is ListPermissionInstrumentedTest) PERMISSION else
            assertEquals(
                FilterWithCountryCodeUIModel(filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                filterType = BLOCKER)),
                navController?.backStack?.last()?.arguments?.parcelable(FILTER_WITH_COUNTRY_CODE))
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
            onView(isRoot()).perform(waitFor(501))
            perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            //TODO if (this@ListFilterUnitTest is ListPermissionInstrumentedTest) PERMISSION else
            assertEquals(
                FilterWithCountryCodeUIModel(filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                filterType = BLOCKER)),
                navController?.backStack?.last()?.arguments?.parcelable(FILTER_WITH_COUNTRY_CODE))
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
            onView(isRoot()).perform(waitFor(501))
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
                .check(matches(hasItemCount(filterList?.size.orZero())))
        }
    }

    @Test
    fun checkFilterItemOne() {
        if (filterList.isNullOrEmpty()) {
            onView(withId(R.id.list_filter_empty)).check(matches(isDisplayed()))
        } else {
            checkFilterItem(0, filterList?.get(0))
        }
    }

    @Test
    fun checkFilterItemTwo() {
        if (filterList.isNullOrEmpty()) {
            onView(withId(R.id.list_filter_empty)).check(matches(isDisplayed()))
        } else {
            checkFilterItem(1, filterList?.get(1))
        }
    }

    @Test
    fun checkDeleteMode() {
        if (filterList.isNullOrEmpty()) {
            onView(withId(R.id.list_filter_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_filter_recycler_view)).apply {
                check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.item_filter_delete),
                    if (filterList?.firstOrNull()?.isDeleteMode.isTrue()) isDisplayed() else not(isDisplayed()),
                    if (filterList?.firstOrNull()?.isCheckedForDelete.isTrue()) isChecked() else not(isChecked()))))))
                perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
                perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
                check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.item_filter_delete),
                    if (filterList?.firstOrNull()?.isDeleteMode.isTrue()) isDisplayed() else not(isDisplayed()),
                    if (filterList?.firstOrNull()?.isCheckedForDelete.isTrue()) isChecked() else not(isChecked()))))))
                perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
                check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.item_filter_delete),
                    if (filterList?.firstOrNull()?.isDeleteMode.isTrue()) isDisplayed() else not(isDisplayed()),
                    if (filterList?.firstOrNull()?.isCheckedForDelete.isTrue()) isChecked() else not(isChecked()))))))
            }
        }
    }

    @Test
    fun checkListFilterEmpty() {
        if (filterList.isNullOrEmpty()) {
            onView(withId(R.id.list_filter_empty)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_state_description)).check(matches(isDisplayed()))
                .check(matches(withText(EmptyState.EMPTY_STATE_BLOCKERS.description())))
            onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
            //TODO drawable
            //onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
        } else {
            onView(withId(R.id.list_filter_empty)).check(matches(not(isDisplayed())))
        }
    }

    private fun checkFilterItem(position: Int, filterWithFilteredNumber: FilterWithFilteredNumberUIModel?) {
        onView(withId(R.id.list_filter_recycler_view)).apply {
            //TODO drawable
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_avatar),
                isDisplayed(),
                withDrawable(filterWithFilteredNumber?.conditionTypeIcon()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_filter),
                isDisplayed(),
                withDrawable(filterWithFilteredNumber?.filterTypeIcon()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_value),
                isDisplayed(),
                withText(filterWithFilteredNumber?.filter))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_name),
                isDisplayed(),
                withText(targetContext.getString(filterWithFilteredNumber?.conditionTypeName().orZero())))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_delete),
                if (filterWithFilteredNumber?.isDeleteMode.isTrue()) isDisplayed() else not(isDisplayed()),
                if (filterWithFilteredNumber?.isCheckedForDelete.isTrue()) isChecked() else not(isChecked()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_divider),
                isDisplayed(),
                withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue)))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_contacts),
                isDisplayed(),
                withText(filterWithFilteredNumber?.filteredContactsText(targetContext)),
                withTextColor(if (filterWithFilteredNumber?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_created),
                isDisplayed(),
                withText(String.format(targetContext.getString(R.string.filter_action_created), filterWithFilteredNumber?.filterCreatedDate())))))))
            perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
            assertEquals(R.id.detailsFilterFragment, navController?.currentDestination?.id)
            assertEquals(filterWithFilteredNumber, navController?.backStack?.last()?.arguments?.parcelable<FilterWithFilteredNumberUIModel>(FILTER_WITH_FILTERED_NUMBER))
        }
    }
}
