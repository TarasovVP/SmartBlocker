package com.tarasovvp.smartblocker.fragments.number

import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.firebase.FirebaseApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.hasItemCount
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.numberDataWithFilterWithFilteredNumberUIModelList
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.numberDataWithFilteredCallUIModelList
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.waitFor
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withBitmap
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withDrawable
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withTextColor
import com.tarasovvp.smartblocker.fragments.ScrollActions.nestedScrollTo
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataFragment
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.After
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
class DetailsNumberDataUnitTest: BaseFragmentUnitTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var contactWithFilter: ContactWithFilterUIModel? = null
    private var isHiddenCall = false
    private var fragment: DetailsNumberDataFragment? = null

    @Before
    override fun setUp() {
        super.setUp()
        FirebaseApp.initializeApp(targetContext)
        isHiddenCall = name.methodName.contains("Hidden")
        val numberData = if (isHiddenCall) CallWithFilterUIModel(
            callId = 2, callName = "a Name", number = String.EMPTY, type = BLOCKED_CALL, callDate = "1678603872094", isFilteredCall = true, filteredNumber = "12345", conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
            filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel())
        else ContactWithFilterUIModel(
            contactId = "5", contactName = "C Name", number = "+380502711344",
            filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = "38050", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal)
        )
        launchFragmentInHiltContainer<DetailsNumberDataFragment> (fragmentArgs = bundleOf("numberData" to numberData)) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.detailsNumberDataFragment)
            Navigation.setViewNavController(requireView(), navController)
            val numberDataUIModelFromBundle = arguments?.parcelable<NumberDataUIModel>("numberData")
            contactWithFilter = if (numberDataUIModelFromBundle is CallWithFilterUIModel) ContactWithFilterUIModel(
                contactName = getString(R.string.details_number_from_call_log), photoUrl = numberDataUIModelFromBundle.photoUrl, number = numberDataUIModelFromBundle.number,
                    filterWithFilteredNumberUIModel = numberDataUIModelFromBundle.filterWithFilteredNumberUIModel)
            else numberDataUIModelFromBundle as ContactWithFilterUIModel
            (this as? DetailsNumberDataFragment)?.apply {
                viewModel.filterListLiveData.postValue(numberDataWithFilterWithFilteredNumberUIModelList())
                viewModel.filteredCallListLiveData.postValue(numberDataWithFilteredCallUIModelList())
                if (isHiddenCall) viewModel.blockHiddenLiveData.postValue(isHiddenCall)
                fragment = this
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
            .check(matches(withBitmap(contactWithFilter?.placeHolder(targetContext)?.toBitmap())))
    }

    @Test
    fun checkDetailsNumberDataFilter() {
        onView(withId(R.id.item_contact_filter))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(contactWithFilter?.filterWithFilteredNumberUIModel?.filterTypeIcon().orZero())))
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
            if (contactWithFilter?.phoneNumberValidity().isNull()) {
                check(matches(withText(String.EMPTY)))
            } else {
                check(matches(isDisplayed()))
                check(matches(withText(targetContext.getString(contactWithFilter?.phoneNumberValidity().orZero()))))
            }
        }
    }

    @Test
    fun checkDetailsNumberDataName() {
        onView(withId(R.id.item_contact_name))
            .check(matches(isDisplayed()))
            .check(matches(withText(if (contactWithFilter?.contactName.isNull()) targetContext.getString(R.string.details_number_not_from_contacts) else contactWithFilter?.contactName)))
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
            isHiddenCall ->targetContext.getString(R.string.details_number_hidden_on)
            contactWithFilter?.filterWithFilteredNumberUIModel.isNull() -> targetContext.getString(R.string.details_number_contact_without_filter)
            contactWithFilter?.filterWithFilteredNumberUIModel?.isBlocker().isTrue() -> targetContext.getString(R.string.details_number_block_with_filter)
            else -> targetContext.getString(R.string.details_number_permit_with_filter)
        }
        onView(withId(R.id.item_contact_filter_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(filterTitleText)))
            .check(matches(withTextColor(if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) R.color.text_color_grey else if (contactWithFilter?.filterWithFilteredNumberUIModel?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green)))
    }

    @Test
    fun checkDetailsNumberDataFilterValue() {
        onView(withId(R.id.item_contact_filter_value))
            .check(matches(isDisplayed()))
            .check(matches(withText(if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) String.EMPTY else contactWithFilter?.filterWithFilteredNumberUIModel?.filter)))
            .check(matches(withTextColor(if (contactWithFilter?.filterWithFilteredNumberUIModel?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green)))
            .check(matches(withDrawable(if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) null else contactWithFilter?.filterWithFilteredNumberUIModel?.conditionTypeSmallIcon())))
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
    fun checkDetailsNumberDataHiddenCreateBlocker() {
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
    fun checkDetailsNumberDataHiddenCreatePermission() {
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
    fun checkNumberDataDetailCreateFilterFullBlocker() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_blocker)).perform(click())
            onView(isRoot()).perform(waitFor(501))
            onView(withId(R.id.number_data_detail_add_filter_full))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_full)))
                .check(matches(withDrawable(FilterCondition.FILTER_CONDITION_FULL.mainIcon())))
                .perform(click())
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_FULL.ordinal, BLOCKER)
        }
    }

    @Test
    fun checkNumberDataDetailCreateFilterFullPermission() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_permission)).perform(click())
            onView(isRoot()).perform(waitFor(501))
            onView(withId(R.id.number_data_detail_add_filter_full))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_full)))
                .check(matches(withDrawable(FilterCondition.FILTER_CONDITION_FULL.mainIcon())))
                .perform(click())
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_FULL.ordinal, PERMISSION)
        }
    }

    @Test
    fun checkNumberDataDetailCreateFilterFullHidden() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_blocker)).perform(click())
            onView(isRoot()).perform(waitFor(501))
            onView(withId(R.id.number_data_detail_add_filter_full))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_full)))
                .check(matches(withDrawable(FilterCondition.FILTER_CONDITION_FULL.mainIcon())))
                .perform(click())
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_FULL.ordinal, BLOCKER)
        }
    }

    @Test
    fun checkNumberDataDetailCreateFilterStartBlocker() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_blocker)).perform(click())
            onView(isRoot()).perform(waitFor(501))
            onView(withId(R.id.number_data_detail_add_filter_start))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_start)))
                .check(matches(withDrawable(R.drawable.ic_condition_start)))
                .perform(click())
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_START.ordinal, BLOCKER)
        }
    }

    @Test
    fun checkNumberDataDetailCreateFilterStartPermission() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_permission)).perform(click())
            onView(isRoot()).perform(waitFor(501))
            onView(withId(R.id.number_data_detail_add_filter_start))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_start)))
                .check(matches(withDrawable(R.drawable.ic_condition_start)))
                .perform(click())
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_START.ordinal, PERMISSION)
        }
    }

    @Test
    fun checkNumberDataDetailCreateFilterStartHidden() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_blocker)).perform(click())
            onView(isRoot()).perform(waitFor(3000))
            onView(withId(R.id.number_data_detail_add_filter_start))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_start)))
                .check(matches(withDrawable(R.drawable.ic_condition_start)))
                .perform(click())
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_START.ordinal, BLOCKER)
        }
    }

    @Test
    fun checkNumberDataDetailCreateFilterContainBlocker() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_blocker)).perform(click())
            onView(isRoot()).perform(waitFor(3000))
            onView(withId(R.id.number_data_detail_add_filter_contain))
                .perform(nestedScrollTo())
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_contain)))
                .check(matches(withDrawable(R.drawable.ic_condition_contain)))
                .perform(click())
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_CONTAIN.ordinal, BLOCKER)
        }
    }

    @Test
    fun checkNumberDataDetailCreateFilterContainPermission() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_permission)).perform(click())
            onView(isRoot()).perform(waitFor(3000))
            onView(withId(R.id.number_data_detail_add_filter_contain))
                .perform(nestedScrollTo())
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_contain)))
                .check(matches(withDrawable(R.drawable.ic_condition_contain)))
                .perform(click())
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_CONTAIN.ordinal, PERMISSION)
        }
    }

    @Test
    fun checkNumberDataDetailAddFilterContainHidden() {
        if (isHiddenCall) {
            onView(withId(R.id.details_number_data_hidden)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.details_number_data_create_permission)).perform(click())
            onView(isRoot()).perform(waitFor(3000))
            onView(withId(R.id.number_data_detail_add_filter_contain))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.filter_condition_contain)))
                .check(matches(withDrawable(R.drawable.ic_condition_contain)))
                .perform(click())
            checkFilterWithCountryCodeArg(FilterCondition.FILTER_CONDITION_CONTAIN.ordinal, PERMISSION)
        }
    }

    @Test
    fun checkDetailsNumberDataHidden() {
        onView(withId(R.id.details_number_data_hidden)).apply {
            if (isHiddenCall) {
                check(matches(isDisplayed()))
                onView(withId(R.id.empty_state_description)).check(matches(withText(EmptyState.EMPTY_STATE_HIDDEN.description())))
                onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
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
                onView(withId(R.id.details_number_data_view_pager)).perform(swipeLeft())
                check(matches(withDrawable(R.drawable.ic_filter_details_tab_2)))
                onView(withId(R.id.details_number_data_view_pager)).perform(ViewActions.swipeRight())
                check(matches(withDrawable(R.drawable.ic_filter_details_tab_1)))
            }
        }
    }

    @Test
    fun checkDetailsNumberHiddenDataTabs() {
        onView(withId(R.id.details_number_data_tabs)).apply {
            if (isHiddenCall) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(withDrawable(R.drawable.ic_filter_details_tab_1)))
                onView(withId(R.id.details_number_data_view_pager)).perform(swipeLeft())
                check(matches(withDrawable(R.drawable.ic_filter_details_tab_2)))
                onView(withId(R.id.details_number_data_view_pager)).perform(ViewActions.swipeRight())
                check(matches(withDrawable(R.drawable.ic_filter_details_tab_1)))
            }
        }
    }

    @Test
    fun checkDetailsNumberDataHiddenViewPager() {
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
        val number = contactWithFilter?.number.orEmpty()
        val filterWithCountryCode = FilterWithCountryCodeUIModel(filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(
            filter = number,
            conditionType = filterCondition,
            filterType = filterType)
        )
        val appPhoneNumberUtil = AppPhoneNumberUtil()
        val phoneNumber = if (appPhoneNumberUtil.getPhoneNumber(number, String.EMPTY).isNull()) appPhoneNumberUtil.getPhoneNumber(number, targetContext.getUserCountry().orEmpty().uppercase())
        else appPhoneNumberUtil.getPhoneNumber(number, String.EMPTY)
        if (phoneNumber.isNull() || filterCondition == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal) {
            assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
        } else {
            fragment?.viewModel?.countryCodeLiveData?.getOrAwaitValue()?.let {
                assertEquals(R.id.createFilterFragment, navController?.currentDestination?.id)
            }
        }
    }

    @After
    override fun tearDown() {
        super.tearDown()
        contactWithFilter = null
    }
}
