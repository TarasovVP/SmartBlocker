package com.tarasovvp.smartblocker.number.list.list_contact

import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Test
import androidx.test.filters.Suppress
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactFragment
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import junit.framework.TestCase.assertEquals
import org.junit.Before
import java.util.ArrayList

@Suppress
@HiltAndroidTest
open class BaseListContactInstrumentedTest: BaseInstrumentedTest() {

    protected var contactWithFilterList: List<ContactWithFilter>? = null
    private var conditionFilters: ArrayList<Int>? = null

    @Before
    override fun setUp() {
        super.setUp()
        contactWithFilterList = listOf()
        TestUtils.launchFragmentInHiltContainer<ListContactFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listContactFragment)
            Navigation.setViewNavController(requireView(), navController)
            (this as ListContactFragment).apply {
                conditionFilters = conditionFilterIndexes
                viewModel.contactLiveData.postValue(contactWithFilterList)
            }
        }
    }

    @Test
    fun checkListContactCheck() {
        onView(withId(R.id.list_contact_check)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isChecked())))
            check(matches(withText(callFilteringText())))
            if (contactWithFilterList.isNullOrEmpty()) {
                check(matches(not(isEnabled())))
            } else {
                check(matches(isEnabled()))
                perform(click())
                assertEquals(R.id.filterConditionsDialog, navController?.currentDestination?.id.orZero())
            }
        }
    }

   /* @Test
    fun checkFilterConditionsDialog() {
        if (contactWithFilterList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_check)).check(matches(not(isEnabled())))
        } else {
            onView(withId(R.id.list_contact_check)).check(matches(isEnabled())).perform(click())
            onView(withId(R.id.dialog_filter_condition_title)).check(matches(isDisplayed())).check(matches(withText(R.string.filter_condition_title)))
            onView(withId(R.id.dialog_filter_condition_full)).check(matches(isDisplayed())).check(matches(withText(R.string.filter_condition_full)))
                .check(matches(not(isChecked()))).perform(click()).check(matches(isChecked()))
            onView(withId(R.id.dialog_filter_condition_start)).check(matches(isDisplayed())).check(matches(withText(R.string.filter_condition_start)))
                .check(matches(not(isChecked()))).perform(click()).check(matches(isChecked()))
            onView(withId(R.id.dialog_filter_condition_contain)).check(matches(isDisplayed())).check(matches(withText(R.string.filter_condition_contain)))
                .check(matches(not(isChecked()))).perform(click()).check(matches(isChecked()))
            onView(withId(R.id.dialog_filter_condition_cancel)).check(matches(isDisplayed())).check(matches(withText(R.string.button_ok))).perform(click())
            onView(withId(R.id.list_contact_check)).check(matches(isDisplayed())).check(matches(withText(callFilteringText()))).perform(click())
            onView(withId(R.id.dialog_filter_condition_confirm)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_close_small))).perform(click())
            onView(withId(R.id.dialog_filter_condition_full)).perform(click())
            onView(withId(R.id.list_contact_check)).check(matches(withText(callFilteringText()))).perform(click())
        }
    }*/

    @Test
    fun checkListContactInfo() {
        onView(withId(R.id.list_contact_info))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(R.drawable.ic_info)))
            .perform(click())
        assertEquals(R.id.infoFragment, navController?.currentDestination?.id.orZero())

    }

    @Test
    fun checkListContactRefresh() {
        if (contactWithFilterList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_contact_refresh)).check(matches(isDisplayed()))
            onView(withId(R.id.list_contact_refresh)).perform(swipeDown())
        }
    }

    @Test
    fun checkListContactRecyclerView() {
        if (contactWithFilterList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_contact_empty)).check(matches(not(isDisplayed())))
            onView(withId(R.id.list_contact_recycler_view))
                .check(matches(isDisplayed()))
                .check(matches(hasItemCount(contactWithFilterList?.size.orZero() + contactWithFilterList?.groupBy {
                    if (it.contact?.name.isNullOrEmpty()) String.EMPTY else it.contact?.name?.get(0).toString()
                }?.size.orZero())))
        }
    }

    @Test
    fun checkListContactEmpty() {
        if (contactWithFilterList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_state_description)).check(matches(isDisplayed())).check(matches(withText(EmptyState.EMPTY_STATE_CONTACTS.description)))
            onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
            onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
        } else {
            onView(withId(R.id.list_contact_empty)).check(matches(not(isDisplayed())))
        }
    }

    private fun callFilteringText(): String {
        val callFilteringText = arrayListOf<String>()
        InstrumentationRegistry.getInstrumentation().targetContext.apply {
            if (conditionFilters.isNullOrEmpty())
                callFilteringText.add(getString(R.string.filter_no_filter))
            else {
                if (conditionFilters?.contains(Constants.BLOCKER).isTrue())
                    callFilteringText.add(getString(R.string.filter_contact_blocker))
                if (conditionFilters?.contains(Constants.PERMISSION).isTrue())
                    callFilteringText.add(getString(R.string.filter_contact_permission))
            }
        }
        return callFilteringText.joinToString()
    }
}
