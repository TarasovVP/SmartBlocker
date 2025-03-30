package com.tarasovvp.smartblocker.number.details.detailsfilter

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.FILTER_WITH_FILTERED_NUMBER
import com.tarasovvp.smartblocker.TestUtils.filterWithFilteredNumberUIModelList
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.numberDataUIModelList
import com.tarasovvp.smartblocker.TestUtils.numberDataWithFilteredCallUIModelList
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.presentation.main.number.details.detailsfilter.DetailsFilterFragment
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.utils.extensions.parcelable
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseDetailsFilterInstrumentedTest : BaseInstrumentedTest() {
    private var filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel? = null

    @Before
    override fun setUp() {
        super.setUp()
        filterWithFilteredNumberUIModel =
            filterWithFilteredNumberUIModelList().firstOrNull()?.apply {
                conditionType =
                    if (this@BaseDetailsFilterInstrumentedTest is DetailsFilterBlockerInstrumentedTest) {
                        FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
                    } else {
                        FilterCondition.FILTER_CONDITION_START.ordinal
                    }
                filterType =
                    if (this@BaseDetailsFilterInstrumentedTest is DetailsFilterBlockerInstrumentedTest) {
                        BLOCKER
                    } else {
                        PERMISSION
                    }
            }
        launchFragmentInHiltContainer<DetailsFilterFragment>(
            fragmentArgs =
                bundleOf(
                    FILTER_WITH_FILTERED_NUMBER to filterWithFilteredNumberUIModel,
                ),
        ) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.detailsFilterFragment)
            Navigation.setViewNavController(requireView(), navController)
            (this as? DetailsFilterFragment)?.apply {
                viewModel.numberDataListLiveDataUIModel.postValue(numberDataUIModelList())
                viewModel.filteredCallListLiveData.postValue(numberDataWithFilteredCallUIModelList())
            }
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkItemDetailsFilterAvatar() {
        onView(withId(R.id.item_details_filter_avatar))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(filterWithFilteredNumberUIModel?.conditionTypeIcon())))
    }

    @Test
    fun checkItemDetailsFilterFilter() {
        onView(withId(R.id.item_details_filter_filter))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(filterWithFilteredNumberUIModel?.filterTypeIcon())))
    }

    @Test
    fun checkItemDetailsFilterValue() {
        onView(withId(R.id.item_details_filter_value))
            .check(matches(isDisplayed()))
            .check(matches(withText(filterWithFilteredNumberUIModel?.filter)))
    }

    @Test
    fun checkItemDetailsFilterTypeTitle() {
        onView(withId(R.id.item_details_filter_name)).check(matches(isDisplayed()))
            .check(matches(withText(filterWithFilteredNumberUIModel?.conditionTypeName().orZero())))
    }

    @Test
    fun checkItemDetailsFilterDivider() {
        onView(withId(R.id.item_details_filter_divider)).check(matches(isDisplayed()))
            .check(
                matches(
                    withBackgroundColor(
                        ContextCompat.getColor(
                            targetContext,
                            R.color.light_steel_blue,
                        ),
                    ),
                ),
            )
    }

    @Test
    fun checkItemDetailsFilterContactsDetails() {
        onView(withId(R.id.item_details_filter_details)).check(matches(isDisplayed()))
            .check(
                matches(
                    withText(
                        if (filterWithFilteredNumberUIModel?.filterAction.isNull()) {
                            filterWithFilteredNumberUIModel?.filteredNumbersText(targetContext)
                        } else {
                            filterWithFilteredNumberUIModel?.filter
                        },
                    ),
                ),
            )
            .check(
                matches(
                    withTextColor(
                        filterWithFilteredNumberUIModel?.filterTypeTint().orZero(),
                    ),
                ),
            )
    }

    @Test
    fun checkItemDetailsFilterFilteredCallsDetails() {
        onView(withId(R.id.details_filter_view_pager)).perform(swipeLeft())
        onView(withId(R.id.item_details_filter_details)).check(matches(isDisplayed()))
            .check(matches(withText(filterWithFilteredNumberUIModel?.filteredCallsText(targetContext))))
            .check(
                matches(
                    withTextColor(
                        filterWithFilteredNumberUIModel?.filterTypeTint().orZero(),
                    ),
                ),
            )
    }

    @Test
    fun checkItemDetailsFilterCreated() {
        onView(withId(R.id.item_details_filter_created))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withText(
                        if (filterWithFilteredNumberUIModel?.created == 0L) {
                            String.EMPTY
                        } else {
                            String.format(
                                targetContext.getString(R.string.filter_action_created),
                                filterWithFilteredNumberUIModel?.filterCreatedDate(),
                            )
                        },
                    ),
                ),
            )
    }

    @Test
    fun checkDetailsFilterDeleteFilter() {
        onView(withId(R.id.details_filter_delete_filter))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.filter_action_delete)))
            .check(matches(withTextColor(R.color.sunset)))
            .perform(click())
        assertEquals(R.id.filterActionDialog, navController?.currentDestination?.id)
        assertEquals(
            filterWithFilteredNumberUIModel.apply {
                this?.filterAction =
                    if (filterWithFilteredNumberUIModel?.isBlocker().isTrue()) {
                        FilterAction.FILTER_ACTION_BLOCKER_DELETE
                    } else {
                        FilterAction.FILTER_ACTION_PERMISSION_DELETE
                    }
            },
            navController?.backStack?.last()?.arguments?.parcelable<FilterWithFilteredNumberUIModel>(
                FILTER_WITH_FILTERED_NUMBER,
            ),
        )
    }

    @Test
    fun checkDetailsFilterChangeFilter() {
        onView(withId(R.id.details_filter_change_filter))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.filter_action_transfer)))
            .perform(click())
        assertEquals(R.id.filterActionDialog, navController?.currentDestination?.id)
        assertEquals(
            filterWithFilteredNumberUIModel.apply {
                this?.filterAction =
                    if (filterWithFilteredNumberUIModel?.isBlocker().isTrue()) {
                        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER
                    } else {
                        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER
                    }
            },
            navController?.backStack?.last()?.arguments?.parcelable<FilterWithFilteredNumberUIModel>(
                FILTER_WITH_FILTERED_NUMBER,
            ),
        )
    }

    @Test
    fun checkDetailsFilterTabs() {
        onView(withId(R.id.details_filter_tabs)).apply {
            check(matches(isDisplayed()))
            check(matches(withDrawable(R.drawable.ic_filter_details_tab_1)))
            onView(withId(R.id.details_filter_view_pager)).perform(swipeLeft())
            check(matches(withDrawable(R.drawable.ic_filter_details_tab_2)))
            onView(withId(R.id.details_filter_view_pager)).perform(swipeRight())
            check(matches(withDrawable(R.drawable.ic_filter_details_tab_1)))
        }
    }

    @Test
    fun checkDetailsFilterViewPager() {
        onView(withId(R.id.details_filter_view_pager))
            .check(matches(isDisplayed()))
            .check(matches(hasItemCount(2)))
    }

    @After
    override fun tearDown() {
        super.tearDown()
        filterWithFilteredNumberUIModel = null
    }
}
