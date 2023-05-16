package com.tarasovvp.smartblocker.number.create

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.FILTER_WITH_COUNTRY_CODE
import com.tarasovvp.smartblocker.TestUtils.LIST_EMPTY
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.waitFor
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseCreateFilterInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var name: TestName = TestName()

    private var fragment: CreateFilterFragment? = null
    private var filterWithFilteredNumbers: FilterWithFilteredNumbers? = null
    private var numberDataUIModelList = arrayListOf<NumberDataUIModel>()

    @Before
    override fun setUp() {
        super.setUp()
        numberDataUIModelList = if (name.methodName.contains(LIST_EMPTY)) arrayListOf() else TestUtils.numberDataList()
        val filterCondition = when(this) {
            is CreateFilterConditionFullInstrumentedTest -> FilterCondition.FILTER_CONDITION_FULL.ordinal
            is CreateFilterConditionStartInstrumentedTest -> FilterCondition.FILTER_CONDITION_START.ordinal
            else -> FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
        }
        launchFragmentInHiltContainer<CreateFilterFragment> (fragmentArgs = bundleOf(FILTER_WITH_COUNTRY_CODE to FilterWithFilteredNumbers(filter = Filter(filterType = BLOCKER, conditionType = filterCondition), countryCode = CountryCode()))) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.createFilterFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this as? CreateFilterFragment
        }
        onView(isRoot()).perform(waitFor(2000))
        fragment?.viewModel?.filteredNumberDataListLiveDataUIModel?.postValue(numberDataUIModelList)
        filterWithFilteredNumbers = fragment?.binding?.filterWithCountryCode
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkItemDetailsFilterAvatar() {
        onView(withId(R.id.item_details_filter_avatar))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(filterWithFilteredNumbers?.filter?.conditionTypeIcon())))
    }

    @Test
    fun checkItemDetailsFilterFilter() {
        onView(withId(R.id.item_details_filter_filter))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(filterWithFilteredNumbers?.filter?.filterTypeIcon())))
    }

    @Test
    fun checkItemDetailsFilterValue() {
        onView(withId(R.id.item_details_filter_value))
            .check(matches(isDisplayed()))
            .check(matches(withText(filterWithFilteredNumbers?.createFilterValue(targetContext))))
    }

    @Test
    fun checkItemDetailsFilterTypeTitle() {
        onView(withId(R.id.item_details_filter_type_title)).check(matches(isDisplayed()))
            .check(matches(withText(if (filterWithFilteredNumbers?.filter.isNull()) filterWithFilteredNumbers?.filter?.filter else targetContext.getString(filterWithFilteredNumbers?.filter?.conditionTypeName().orZero()))))
    }

    @Test
    fun checkItemDetailsFilterDivider() {
        onView(withId(R.id.item_details_filter_divider)).check(matches(isDisplayed()))
            .check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue))))
    }

    @Test
    fun checkItemDetailsFilterContactsDetails() {
        onView(withId(R.id.item_details_filter_details)).check(matches(isDisplayed()))
            .check(matches(withText(if (filterWithFilteredNumbers?.filter?.filterAction.isNull())
                filterWithFilteredNumbers?.filter?.filteredContactsText(targetContext) else filterWithFilteredNumbers?.filterActionText(targetContext))))
            .check(matches(withTextColor(if (filterWithFilteredNumbers?.filter?.filterAction.isNull()) filterWithFilteredNumbers?.filter?.filterTypeTint().orZero() else filterWithFilteredNumbers?.filter?.filterDetailTint().orZero())))
    }

    @Test
    fun checkItemDetailsFilterFilteredCallsDetails() {
        onView(withId(R.id.item_details_filter_details)).check(matches(isDisplayed()))
            .check(matches(withText(if (filterWithFilteredNumbers?.filter?.filterAction.isNull())
                filterWithFilteredNumbers?.filter?.filteredCallsText(targetContext) else filterWithFilteredNumbers?.filterActionText(targetContext))))
            .check(matches(withTextColor(if (filterWithFilteredNumbers?.filter?.filterAction.isNull()) filterWithFilteredNumbers?.filter?.filterTypeTint().orZero() else filterWithFilteredNumbers?.filter?.filterDetailTint().orZero())))
    }

    @Test
    fun checkItemDetailsFilterCreated() {
        onView(withId(R.id.item_details_filter_created)).apply {
            if ((filterWithFilteredNumbers?.filter?.created ?: 0) > 0L) {
                check(matches(isDisplayed()))
                check(matches(withText(String.format(targetContext.getString(R.string.filter_action_created), filterWithFilteredNumbers?.filter?.filterCreatedDate()))))
            } else {
                check(matches(withText(String.EMPTY)))
            }
        }
    }


    @Test
    fun checkCreateFilterInputContainer() {
        onView(withId(R.id.create_filter_input_container)).check(matches(isDisplayed()))

    }

    @Test
    fun checkCreateFilterCountryCodeSpinner() {
        onView(withId(R.id.create_filter_country_code_spinner)).apply {
            if (filterWithFilteredNumbers?.filter?.isTypeContain().isTrue()) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                perform(click())
                assertEquals(R.id.countryCodeSearchDialog, navController?.currentDestination?.id)
            }
        }
    }

    @Test
    fun checkCreateFilterCountryCodeValue() {
        onView(withId(R.id.create_filter_country_code_value))
            .check(matches(isDisplayed()))
            .check(matches(withText(filterWithFilteredNumbers?.countryCode?.countryCode)))
    }

    @Test
    fun checkCreateFilterInput() {
        onView(withId(R.id.create_filter_input)).check(matches(isDisplayed()))
    }

    @Test
    fun checkCreateFilterSubmit() {
        onView(withId(R.id.create_filter_submit))
            .check(matches(isDisplayed()))
            .check(matches(withText(filterWithFilteredNumbers?.filter?.filterAction?.title().orZero())))
            .check(matches(withTextColor(filterWithFilteredNumbers?.filter?.filterActionTextTint().orZero())))
            .check(matches(withAlpha(if (filterWithFilteredNumbers?.filter?.isInvalidFilterAction().isTrue()) 0.5f else 1f)))
            .check(matches(if (filterWithFilteredNumbers?.filter?.isInvalidFilterAction().isTrue()) not(isEnabled()) else isEnabled()))
    }

    @Test
    fun checkCreateFilterNumberList() {
        if (numberDataUIModelList.isEmpty()) {
            onView(withId(R.id.create_filter_empty_list)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.create_filter_number_list))
                .check(matches(isDisplayed()))
                .check(matches(hasChildCount(numberDataUIModelList.size)))
        }
    }

    @Test
    fun checkCreateFilterListEmpty() {
        onView(withId(R.id.create_filter_empty_list)).apply {
            if (numberDataUIModelList.isEmpty()) {
                check(matches(isDisplayed()))
                onView(withId(R.id.empty_state_description)).check(matches(withText(EmptyState.EMPTY_STATE_CREATE_FILTER.description())))
                onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
                //TODO drawable
                //onView(withId(R.id.empty_state_icon)).check(matches(withDrawable(R.drawable.ic_empty_state)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }
}
