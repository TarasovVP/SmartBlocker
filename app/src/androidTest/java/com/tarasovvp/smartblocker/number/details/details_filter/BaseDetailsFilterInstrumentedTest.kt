package com.tarasovvp.smartblocker.number.details.details_filter

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.FILTER_WITH_COUNTRY_CODE
import com.tarasovvp.smartblocker.TestUtils.filteredCallList
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.numberDataList
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.presentation.main.number.details.details_filter.DetailsFilterFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseDetailsFilterInstrumentedTest: BaseInstrumentedTest() {

    private var filterWithCountryCode: FilterWithCountryCode? = null

    @Before
    override fun setUp() {
        super.setUp()
        filterWithCountryCode = TestUtils.filterWithCountryCode().apply {
            filter?.conditionType = if (this@BaseDetailsFilterInstrumentedTest is DetailsFilterBlockerInstrumentedTest) FilterCondition.FILTER_CONDITION_CONTAIN.ordinal else FilterCondition.FILTER_CONDITION_START.ordinal
            filter?.filterType = if (this@BaseDetailsFilterInstrumentedTest is DetailsFilterBlockerInstrumentedTest) BLOCKER else PERMISSION
        }
        launchFragmentInHiltContainer<DetailsFilterFragment> (fragmentArgs = bundleOf(FILTER_WITH_COUNTRY_CODE to filterWithCountryCode)) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.detailsFilterFragment)
            Navigation.setViewNavController(requireView(), navController)
            (this as? DetailsFilterFragment)?.apply {
                viewModel.filteredNumberDataListLiveDataUIModel.postValue(numberDataList())
                viewModel.filteredCallListLiveData.postValue(filteredCallList())
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
            .check(matches(withDrawable(filterWithCountryCode?.filter?.conditionTypeIcon())))
    }

    @Test
    fun checkItemDetailsFilterFilter() {
        onView(withId(R.id.item_details_filter_filter))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(filterWithCountryCode?.filter?.filterTypeIcon())))
    }

    @Test
    fun checkItemDetailsFilterValue() {
        onView(withId(R.id.item_details_filter_value))
            .check(matches(isDisplayed()))
            .check(matches(withText(filterWithCountryCode?.createFilterValue(targetContext))))
    }

    @Test
    fun checkItemDetailsFilterTypeTitle() {
        onView(withId(R.id.item_details_filter_type_title)).check(matches(isDisplayed()))
            .check(matches(withText(if (filterWithCountryCode?.filter.isNull()) filterWithCountryCode?.filter?.filter else targetContext.getString(filterWithCountryCode?.filter?.conditionTypeName().orZero()))))
    }

    @Test
    fun checkItemDetailsFilterDivider() {
        onView(withId(R.id.item_details_filter_divider)).check(matches(isDisplayed()))
            .check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue))))
    }

    @Test
    fun checkItemDetailsFilterContactsDetails() {
        onView(withId(R.id.item_details_filter_details)).check(matches(isDisplayed()))
            .check(matches(withText(if (filterWithCountryCode?.filter?.filterAction.isNull())
                filterWithCountryCode?.filter?.filteredContactsText(targetContext) else filterWithCountryCode?.filterActionText(targetContext))))
            .check(matches(withTextColor(if (filterWithCountryCode?.filter?.filterAction.isNull()) filterWithCountryCode?.filter?.filterTypeTint().orZero() else filterWithCountryCode?.filter?.filterDetailTint().orZero())))
    }

    @Test
    fun checkItemDetailsFilterFilteredCallsDetails() {
        onView(withId(R.id.details_filter_view_pager)).perform(swipeLeft())
        onView(withId(R.id.item_details_filter_details)).check(matches(isDisplayed()))
            .check(matches(withText(if (filterWithCountryCode?.filter?.filterAction.isNull())
                filterWithCountryCode?.filter?.filteredCallsText(targetContext) else filterWithCountryCode?.filterActionText(targetContext))))
            .check(matches(withTextColor(if (filterWithCountryCode?.filter?.filterAction.isNull()) filterWithCountryCode?.filter?.filterTypeTint().orZero() else filterWithCountryCode?.filter?.filterDetailTint().orZero())))
    }

    @Test
    fun checkItemDetailsFilterCreated() {
        onView(withId(R.id.item_details_filter_created))
            .check(matches(isDisplayed()))
            .check(matches(withText(if (filterWithCountryCode?.filter?.created == 0L) String.EMPTY else String.format(targetContext.getString(R.string.filter_action_created), filterWithCountryCode?.filter?.filterCreatedDate()))))
    }

    @Test
    fun checkDetailsFilterDeleteFilter() {
        onView(withId(R.id.details_filter_delete_filter))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.filter_action_delete)))
            .check(matches(withTextColor(R.color.sunset)))
            .perform(click())
        assertEquals(R.id.filterActionDialog, navController?.currentDestination?.id)
        assertEquals(filterWithCountryCode.apply { this?.filter?.filterAction = if (filterWithCountryCode?.filter?.isBlocker().isTrue())
            FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE },
            navController?.backStack?.last()?.arguments?.parcelable<FilterWithCountryCode>(FILTER_WITH_COUNTRY_CODE))
    }

    @Test
    fun checkDetailsFilterChangeFilter() {
        onView(withId(R.id.details_filter_change_filter))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.filter_action_transfer)))
            .perform(click())
        assertEquals(R.id.filterActionDialog, navController?.currentDestination?.id)
        assertEquals(filterWithCountryCode.apply { this?.filter?.filterAction = if (filterWithCountryCode?.filter?.isBlocker().isTrue())
            FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER },
            navController?.backStack?.last()?.arguments?.parcelable<FilterWithCountryCode>(FILTER_WITH_COUNTRY_CODE))
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
}
