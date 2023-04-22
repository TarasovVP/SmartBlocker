package com.tarasovvp.smartblocker.number.details.details_number_data

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.FILTER_WITH_COUNTRY_CODE
import com.tarasovvp.smartblocker.TestUtils.filterList
import com.tarasovvp.smartblocker.TestUtils.filteredCallList
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withBitmap
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseDetailsNumberDataInstrumentedTest: BaseInstrumentedTest() {

    private var contactWithFilter: ContactWithFilter? = null
    private var isHiddenCall = false

    @Before
    override fun setUp() {
        super.setUp()
        isHiddenCall = this is DetailsNumberDataHiddenInstrumentedTest
        val numberData = if (isHiddenCall) CallWithFilter(
            FilteredCall(2).apply { callId = 5
                callName = "a Name"
            number = String.EMPTY
            type = BLOCKED_CALL
            callDate = "1678603872094"
            isFilteredCall = true
            filteredNumber = "12345"
            conditionType = FilterCondition.FILTER_CONDITION_FULL.index},
            FilterWithCountryCode(Filter(), CountryCode()))
        else ContactWithFilter(
            Contact("5",
                name = "C Name",
                number = "+380502711344",
                filter = "123"),
            if (isHiddenCall) FilterWithCountryCode(Filter(), countryCode = CountryCode()) else FilterWithCountryCode(Filter(filter = "38050", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_START.index), countryCode = CountryCode("UA")))
        launchFragmentInHiltContainer<DetailsNumberDataFragment> (fragmentArgs = bundleOf("numberData" to numberData)) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.detailsNumberDataFragment)
            Navigation.setViewNavController(requireView(), navController)
            val numberDataFromBundle = arguments?.parcelable<NumberData>("numberData")
            contactWithFilter = if (numberDataFromBundle is CallWithFilter) ContactWithFilter(filterWithCountryCode = numberDataFromBundle.filterWithCountryCode,
                contact = Contact(name = getString(R.string.details_number_from_call_log),
                    photoUrl = numberDataFromBundle.call?.photoUrl,
                    number = numberDataFromBundle.call?.number.orEmpty(),
                    filter = numberDataFromBundle.filterWithCountryCode?.filter?.filter.orEmpty())) else numberDataFromBundle as ContactWithFilter
            (this as? DetailsNumberDataFragment)?.apply {
                viewModel.filterListLiveData.postValue(filterList())
                viewModel.filteredCallListLiveData.postValue(filteredCallList())
            }
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container))
            .check(matches(isDisplayed()))
            .perform(click())
    }

    @Test
    fun checkDetailsNumberDataAvatar() {
        onView(withId(R.id.item_contact_avatar))
            .check(matches(isDisplayed()))
            .check(matches(withBitmap(contactWithFilter?.contact?.placeHolder(targetContext)?.toBitmap())))
    }

    @Test
    fun checkDetailsNumberDataFilter() {
        onView(withId(R.id.item_contact_filter))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(contactWithFilter?.filterWithCountryCode?.filter?.filterTypeIcon().orZero())))
    }

    @Test
    fun checkDetailsNumberDataNumber() {
        onView(withId(R.id.item_contact_number))
            .check(matches(isDisplayed()))
            .check(matches(withText(if (isHiddenCall) targetContext.getString(R.string.details_number_hidden) else contactWithFilter?.highlightedSpanned.toString())))
    }

    @Test
    fun checkDetailsNumberDataValidity() {
        onView(withId(R.id.item_contact_validity)).apply {
            if (contactWithFilter?.contact?.phoneNumberValidity().isNull()) {
                check(matches(withText(String.EMPTY)))
            } else {
                check(matches(isDisplayed()))
                check(matches(withText(targetContext.getString(contactWithFilter?.contact?.phoneNumberValidity().orZero()))))
            }
        }
    }

    @Test
    fun checkDetailsNumberDataName() {
        onView(withId(R.id.item_contact_name))
            .check(matches(isDisplayed()))
            .check(matches(withText(if (contactWithFilter?.contact?.isNameEmpty().isTrue()) targetContext.getString(R.string.details_number_not_from_contacts) else contactWithFilter?.contact?.name)))
    }

    @Test
    fun checkDetailsNumberDataDivider() {
        onView(withId(R.id.item_contact_divider))
            .check(matches(isDisplayed()))
            .check(matches(withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue))))
    }

    @Test
    fun checkDetailsNumberDataFilterTitle() {
        val filterTitleText = when {
            isHiddenCall -> if (SharedPrefs.blockHidden.isTrue()) targetContext.getString(R.string.details_number_hidden_on) else targetContext.getString(R.string.details_number_hidden_off)
            contactWithFilter?.contact?.isFilterNullOrEmpty().isTrue() -> targetContext.getString(R.string.details_number_contact_without_filter)
            contactWithFilter?.filterWithCountryCode?.filter?.isBlocker().isTrue() -> targetContext.getString(R.string.details_number_block_with_filter)
            else -> targetContext.getString(R.string.details_number_permit_with_filter)
        }
        onView(withId(R.id.item_contact_filter_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(filterTitleText)))
            .check(matches(withTextColor(if (contactWithFilter?.contact?.isFilterNullOrEmpty().isTrue()) R.color.text_color_grey else if (contactWithFilter?.filterWithCountryCode?.filter?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green)))
    }

    @Test
    fun checkDetailsNumberDataFilterValue() {
        onView(withId(R.id.item_contact_filter_value))
            .check(matches(isDisplayed()))
            .check(matches(withText(if (contactWithFilter?.contact?.isFilterNullOrEmpty().isTrue()) String.EMPTY else contactWithFilter?.filterWithCountryCode?.filter?.filter)))
            .check(matches(withTextColor(if (contactWithFilter?.filterWithCountryCode?.filter?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green)))
            .check(matches(withDrawable(if (contactWithFilter?.filterWithCountryCode.isNull()) null else contactWithFilter?.filterWithCountryCode?.filter?.conditionTypeSmallIcon())))
    }

    @Test
    fun checkDetailsNumberDataCreateBlocker() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_create_blocker))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.settings)))
                .check(matches(withTextColor(R.color.text_color_grey)))
                .check(matches(withDrawable(R.drawable.ic_settings)))
                .check(matches(isEnabled()))
                .perform(click())
            assertEquals(R.id.settingsBlockerFragment, navController?.currentDestination?.id)
        } else {
            onView(withId(R.id.number_data_detail_add_filter_start)).check(matches(not(isDisplayed())))
            onView(withId(R.id.details_number_data_create_blocker))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_action_create)))
                .check(matches(withTextColor(R.color.white)))
                .check(matches(withDrawable(R.drawable.ic_blocker_white)))
                .check(matches(isEnabled()))
                .check(matches(withAlpha(1f)))
                .perform(click())
            onView(withId(R.id.number_data_detail_add_filter_start)).check(matches(isDisplayed()))
            onView(withId(R.id.details_number_data_create_blocker))
                .check(matches(withText(R.string.number_details_close)))
            onView(withId(R.id.details_number_data_create_permission))
                .check(matches(withTextColor(R.color.comet)))
                .check(matches(not(isEnabled())))
                .check(matches(withAlpha(0.5f)))
        }
    }

    @Test
    fun checkDetailsNumberDataCreatePermission() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_create_permission)).check(matches(not(isDisplayed())))
        } else {
            onView(withId(R.id.number_data_detail_add_filter_start)).check(matches(not(isDisplayed())))
            onView(withId(R.id.details_number_data_create_permission))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_action_create)))
                .check(matches(withTextColor(R.color.white)))
                .check(matches(withDrawable(R.drawable.ic_permission_white)))
                .check(matches(isEnabled()))
                .check(matches(withAlpha(1f)))
                .perform(click())
            onView(withId(R.id.number_data_detail_add_filter_start)).check(matches(isDisplayed()))
            onView(withId(R.id.details_number_data_create_permission))
                .check(matches(withText(R.string.number_details_close)))
            onView(withId(R.id.details_number_data_create_blocker))
                .check(matches(withTextColor(R.color.comet)))
                .check(matches(not(isEnabled())))
                .check(matches(withAlpha(0.5f)))
        }
    }

    @Test
    fun checkNumberDataDetailAddFilterFull() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_blocker)).perform(click())
            onView(withId(R.id.number_data_detail_add_filter_full))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_full)))
                .check(matches(withDrawable(FilterCondition.FILTER_CONDITION_FULL.mainIcon)))
                .perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_FULL.index, BLOCKER)
        }
    }

    @Test
    fun checkNumberDataDetailAddFilterStart() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_blocker)).perform(click())
            onView(withId(R.id.number_data_detail_add_filter_start))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_start)))
                .check(matches(withDrawable(R.drawable.ic_condition_start)))
                .perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_START.index, BLOCKER)
        }
    }

    @Test
    fun checkNumberDataDetailAddFilterContain() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_permission)).perform(click())
            onView(withId(R.id.number_data_detail_add_filter_contain))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_contain)))
                .check(matches(withDrawable(R.drawable.ic_condition_contain)))
                .perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_CONTAIN.index, PERMISSION)
        }
    }

    @Test
    fun checkDetailsNumberDataHidden() {
        onView(withId(R.id.details_number_data_hidden)).apply {
            if (isHiddenCall) {
                check(matches(isDisplayed()))
                onView(withId(R.id.empty_state_description)).check(matches(withText(EmptyState.EMPTY_STATE_HIDDEN.description)))
                onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
                //TODO drawable
                onView(withId(R.id.empty_state_icon)).check(matches(withDrawable(R.drawable.ic_empty_state)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkDetailsNumberDataTabs() {
        onView(withId(R.id.details_number_data_tabs)).apply {
            if (isHiddenCall) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(withDrawable(R.drawable.ic_filter_details_tab_1)))
                onView(withId(R.id.details_number_data_view_pager)).perform(ViewActions.swipeLeft())
                check(matches(withDrawable(R.drawable.ic_filter_details_tab_2)))
                onView(withId(R.id.details_number_data_view_pager)).perform(ViewActions.swipeRight())
                check(matches(withDrawable(R.drawable.ic_filter_details_tab_1)))
            }
        }
    }

    @Test
    fun checkDetailsNumberDataViewPager() {
        onView(withId(R.id.details_number_data_view_pager)).apply {
            if (isHiddenCall) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(hasItemCount(2)))
            }
        }
    }

    private fun checkFilterWithCountryCodeArg(filterCondition: Int, filterType: Int) {
        val phoneNumber = if (contactWithFilter?.contact?.number.orEmpty().getPhoneNumber(String.EMPTY).isNull())
            contactWithFilter?.contact?.number.orEmpty().getPhoneNumber(targetContext.getUserCountry().orEmpty().uppercase())
        else contactWithFilter?.contact?.number.orEmpty().getPhoneNumber(String.EMPTY)
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(
            filter = contactWithFilter?.contact?.number.orEmpty(),
            conditionType = filterCondition,
            filterType = filterType
        ))
        if (phoneNumber.isNull() || filterCondition == FilterCondition.FILTER_CONDITION_CONTAIN.index) {
            assertEquals(filterWithCountryCode,
                navController?.backStack?.last()?.arguments?.parcelable<FilterWithCountryCode>(FILTER_WITH_COUNTRY_CODE))
        } else {
            filterWithCountryCode.apply {
                filter?.country = "UA"
                filter?.filter = phoneNumber?.nationalNumber.toString()
                countryCode = TestUtils.countryCode()
            }
            assertEquals(filterWithCountryCode,
                navController?.backStack?.last()?.arguments?.parcelable<FilterWithCountryCode>(FILTER_WITH_COUNTRY_CODE))
        }
    }
}
