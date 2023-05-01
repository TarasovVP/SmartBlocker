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
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
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
    private var filterWithCountryCode: FilterWithCountryCode? = null
    private var numberDataList = arrayListOf<NumberData>()

    @Before
    override fun setUp() {
        super.setUp()
        numberDataList = if (name.methodName.contains(LIST_EMPTY)) arrayListOf() else TestUtils.numberDataList()
        val filterCondition = when(this) {
            is CreateFilterConditionFullInstrumentedTest -> FilterCondition.FILTER_CONDITION_FULL.index
            is CreateFilterConditionStartInstrumentedTest -> FilterCondition.FILTER_CONDITION_START.index
            else -> FilterCondition.FILTER_CONDITION_CONTAIN.index
        }
        launchFragmentInHiltContainer<CreateFilterFragment> (fragmentArgs = bundleOf(FILTER_WITH_COUNTRY_CODE to FilterWithCountryCode(filter = Filter(filterType = BLOCKER, conditionType = filterCondition), countryCode = CountryCode()))) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.createFilterFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this as? CreateFilterFragment
        }
        onView(isRoot()).perform(waitFor(2000))
        fragment?.viewModel?.filteredNumberDataListLiveData?.postValue(numberDataList)
        filterWithCountryCode = fragment?.binding?.filterWithCountryCode
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
        onView(withId(R.id.item_details_filter_details)).check(matches(isDisplayed()))
            .check(matches(withText(if (filterWithCountryCode?.filter?.filterAction.isNull())
                filterWithCountryCode?.filter?.filteredCallsText(targetContext) else filterWithCountryCode?.filterActionText(targetContext))))
            .check(matches(withTextColor(if (filterWithCountryCode?.filter?.filterAction.isNull()) filterWithCountryCode?.filter?.filterTypeTint().orZero() else filterWithCountryCode?.filter?.filterDetailTint().orZero())))
    }

    @Test
    fun checkItemDetailsFilterCreated() {
        onView(withId(R.id.item_details_filter_created)).apply {
            if ((filterWithCountryCode?.filter?.created ?: 0) > 0L) {
                check(matches(isDisplayed()))
                check(matches(withText(String.format(targetContext.getString(R.string.filter_action_created), filterWithCountryCode?.filter?.filterCreatedDate()))))
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
            if (filterWithCountryCode?.filter?.isTypeContain().isTrue()) {
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
            .check(matches(withText(filterWithCountryCode?.countryCode?.countryCode)))
    }

    @Test
    fun checkCreateFilterInput() {
        onView(withId(R.id.create_filter_input)).check(matches(isDisplayed()))
    }

    @Test
    fun checkCreateFilterSubmit() {
        onView(withId(R.id.create_filter_submit))
            .check(matches(isDisplayed()))
            .check(matches(withText(filterWithCountryCode?.filter?.filterAction?.title.orZero())))
            .check(matches(withTextColor(filterWithCountryCode?.filter?.filterActionTextTint().orZero())))
            .check(matches(withAlpha(if (filterWithCountryCode?.filter?.isInvalidFilterAction().isTrue()) 0.5f else 1f)))
            .check(matches(if (filterWithCountryCode?.filter?.isInvalidFilterAction().isTrue()) not(isEnabled()) else isEnabled()))
    }

    @Test
    fun checkCreateFilterNumberList() {
        if (numberDataList.isEmpty()) {
            onView(withId(R.id.create_filter_empty_list)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.create_filter_number_list))
                .check(matches(isDisplayed()))
                .check(matches(hasChildCount(numberDataList.size)))
        }
    }

    @Test
    fun checkCreateFilterListEmpty() {
        onView(withId(R.id.create_filter_empty_list)).apply {
            if (numberDataList.isEmpty()) {
                check(matches(isDisplayed()))
                onView(withId(R.id.empty_state_description)).check(matches(withText(EmptyState.EMPTY_STATE_CREATE_FILTER.descriptionRes)))
                onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
                //TODO drawable
                //onView(withId(R.id.empty_state_icon)).check(matches(withDrawable(R.drawable.ic_empty_state)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }
}
