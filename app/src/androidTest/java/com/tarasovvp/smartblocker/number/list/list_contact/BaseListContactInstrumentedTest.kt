package com.tarasovvp.smartblocker.number.list.list_contact

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Test
import androidx.test.filters.Suppress
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.atPosition
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withBitmap
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import com.tarasovvp.smartblocker.waitUntilViewIsDisplayed
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.Before

@Suppress
@HiltAndroidTest
open class BaseListContactInstrumentedTest: BaseInstrumentedTest() {

    private var contactList: List<ContactWithFilter>? = null
    private var filterIndexList: ArrayList<Int> = arrayListOf()
    private var fragment: Fragment? = null

    @Before
    override fun setUp() {
        super.setUp()
        contactList = if (this is EmptyListContactInstrumentedTest) listOf() else TestUtils.contactWithFilterList()
        launchFragmentInHiltContainer<ListContactFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listContactFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            (this as ListContactFragment).apply {
                filterIndexList = filterIndexes ?: arrayListOf()
                viewModel.contactListLiveData.postValue(contactList)
            }
        }
        waitUntilViewIsDisplayed(if (this@BaseListContactInstrumentedTest is EmptyListContactInstrumentedTest) withId(R.id.list_contact_empty) else withText(contactList?.get(0)?.contact?.name))
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkListContactCheck() {
        onView(withId(R.id.list_contact_check)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isChecked())))
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            if (contactList.isNullOrEmpty()) {
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
        onView(withId(R.id.list_contact_check)).apply {
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                        add(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
                    })
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (contactList.isNullOrEmpty()) {
                        check(matches(not(isEnabled())))
                    } else {
                        check(matches(isChecked()))
                        check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
                    }
                }
            }
        }
    }

    @Test
    fun checkListContactCheckTwoFilters() {
        onView(withId(R.id.list_contact_check)).apply {
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                        add(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
                        add(NumberDataFiltering.CONTACT_WITH_PERMISSION.ordinal)
                    })
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (contactList.isNullOrEmpty()) {
                        check(matches(not(isEnabled())))
                    } else {
                        check(matches(isChecked()))
                        check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
                    }
                }
            }
        }
    }

    @Test
    fun checkListContactInfo() {
        onView(withId(R.id.list_contact_info))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(R.drawable.ic_info)))
            .perform(click())
        assertEquals(R.id.infoFragment, navController?.currentDestination?.id)

    }

    @Test
    fun checkListContactRefresh() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_contact_refresh)).check(matches(isDisplayed()))
            onView(withId(R.id.list_contact_refresh)).perform(swipeDown())
        }
    }

    @Test
    fun checkListContactRecyclerView() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_contact_empty)).check(matches(not(isDisplayed())))
            onView(withId(R.id.list_contact_recycler_view))
                .check(matches(isDisplayed()))
                .check(matches(hasItemCount(contactList?.size.orZero() + contactList?.groupBy {
                    if (it.contact?.name.isNullOrEmpty()) String.EMPTY else it.contact?.name?.get(0).toString()
                }?.size.orZero())))
        }
    }

    @Test
    fun checkContactItemHeaderOne() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            val firstHeader = contactList?.groupBy {
                if (it.contact?.name.isNullOrEmpty()) String.EMPTY else it.contact?.name?.get(0).toString()
            }?.keys?.first()
            onView(withId(R.id.list_contact_recycler_view)).check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.item_header_text), withText(firstHeader))))))
        }
    }

    @Test
    fun checkContactItemOne() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            checkContactItem(1, contactList?.get(0))
        }
    }

    @Test
    fun checkContactItemTwo() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            checkContactItem(3, contactList?.get(1))
        }
    }

    @Test
    fun checkListContactEmpty() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_state_description)).check(matches(isDisplayed())).check(matches(withText(EmptyState.EMPTY_STATE_CONTACTS.description)))
            onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
            onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
        } else {
            onView(withId(R.id.list_contact_empty)).check(matches(not(isDisplayed())))
        }
    }

    private fun checkContactItem(position: Int, contactWithFilter: ContactWithFilter?) {
        onView(withId(R.id.list_contact_recycler_view)).apply {
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_avatar),
                isDisplayed(),
                withBitmap(contactWithFilter?.contact?.placeHolder(targetContext)?.toBitmap()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_filter),
                isDisplayed(),
                withDrawable(contactWithFilter?.filterWithCountryCode?.filter?.filterTypeIcon().orZero()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_number),
                isDisplayed(),
                withText(contactWithFilter?.highlightedSpanned.toString()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_validity),
                if (contactWithFilter?.contact?.phoneNumberValidity().isNull()) not(isDisplayed()) else isDisplayed(),
                withText(if (contactWithFilter?.contact?.phoneNumberValidity().isNull()) String.EMPTY else targetContext.getString(contactWithFilter?.contact?.phoneNumberValidity().orZero())))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_name),
                isDisplayed(),
                withText(if (contactWithFilter?.contact?.isNameEmpty().isTrue()) targetContext.getString(R.string.details_number_not_from_contacts) else contactWithFilter?.contact?.name))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_divider),
                isDisplayed(),
                withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue)))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_filter_title),
                isDisplayed(),
                withText(if (contactWithFilter?.contact?.isFilterNullOrEmpty().isTrue()) targetContext.getString(R.string.details_number_contact_without_filter) else if (contactWithFilter?.filterWithCountryCode?.filter?.isBlocker().isTrue()) InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.details_number_block_with_filter) else InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.details_number_permit_with_filter)),
                withTextColor(if (contactWithFilter?.contact?.isFilterNullOrEmpty().isTrue()) R.color.text_color_grey else if (contactWithFilter?.filterWithCountryCode?.filter?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_filter_value),
                isDisplayed(),
                withText(if (contactWithFilter?.contact?.isFilterNullOrEmpty().isTrue()) String.EMPTY else contactWithFilter?.filterWithCountryCode?.filter?.filter),
                withTextColor(if (contactWithFilter?.filterWithCountryCode?.filter?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green),
                withDrawable(if (contactWithFilter?.contact?.isFilterNullOrEmpty().isTrue()) null else contactWithFilter?.filterWithCountryCode?.filter?.conditionTypeSmallIcon()))))))
            perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
            assertEquals(R.id.detailsNumberDataFragment, navController?.currentDestination?.id)
            assertEquals(contactWithFilter, navController?.backStack?.last()?.arguments?.parcelable<CallWithFilter>("numberData"))
        }
    }
}
