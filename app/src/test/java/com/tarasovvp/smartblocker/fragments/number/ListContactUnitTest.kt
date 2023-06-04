package com.tarasovvp.smartblocker.fragments.number

import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.firebase.FirebaseApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.UnitTestUtils.EMPTY
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.atPosition
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.contactWithFilterUIModelList
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.hasItemCount
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.waitFor
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withDrawable
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withTextColor
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactFragment
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
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
class ListContactUnitTest: BaseFragmentUnitTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    private var contactList: List<ContactWithFilterUIModel>? = null
    private var filterIndexList: ArrayList<Int> = arrayListOf()
    private var fragment: Fragment? = null

    @Before
    override fun setUp() {
        super.setUp()
        FirebaseApp.initializeApp(targetContext)
        contactList = if (name.methodName.contains(EMPTY)) listOf() else contactWithFilterUIModelList()
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
        onView(isRoot()).perform(waitFor(3000))
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
            filterIndexList.add(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
            fragment?.setFragmentResult(
                FILTER_CONDITION_LIST,
                bundleOf(FILTER_CONDITION_LIST to filterIndexList)
            )
            if (contactList.isNullOrEmpty()) {
                check(matches(not(isEnabled())))
            } else {
                check(matches(isChecked()))
                check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            }
        }
    }

    @Test
    fun checkListContactCheckTwoFilters() {
        onView(withId(R.id.list_contact_check)).apply {
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            filterIndexList.add(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
            filterIndexList.add(NumberDataFiltering.CONTACT_WITH_PERMISSION.ordinal)
            fragment?.setFragmentResult(FILTER_CONDITION_LIST, bundleOf(FILTER_CONDITION_LIST to filterIndexList))
            if (contactList.isNullOrEmpty()) {
                check(matches(not(isEnabled())))
            } else {
                check(matches(isChecked()))
                check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
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
                    if (it.contactName.isEmpty()) String.EMPTY else it.contactName.firstOrNull()
                }?.size.orZero())))
        }
    }

    @Test
    fun checkContactItemHeaderOne() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            val firstHeader = contactList?.groupBy {
                if (it.contactName.isEmpty()) String.EMPTY else it.contactName.firstOrNull().toString()
            }?.keys?.first()
            onView(withId(R.id.list_contact_recycler_view)).check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.item_header_text), withText(firstHeader))))))
        }
    }

    @Test
    fun checkContactItemOne() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            checkContactItem(1, contactList?.firstOrNull())
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
            onView(withId(R.id.empty_state_description)).check(matches(isDisplayed())).check(matches(withText(EmptyState.EMPTY_STATE_CONTACTS.description())))
            onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
            onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
        } else {
            onView(withId(R.id.list_contact_empty)).check(matches(not(isDisplayed())))
        }
    }

    private fun checkContactItem(position: Int, contactWithFilter: ContactWithFilterUIModel?) {
        onView(withId(R.id.list_contact_recycler_view)).apply {
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_avatar),
                isDisplayed(),
                /*withBitmap(contactWithFilter?.placeHolder(targetContext)?.toBitmap()*/)))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_filter),
                isDisplayed(),
                withDrawable(contactWithFilter?.filterWithFilteredNumberUIModel?.filterTypeIcon().orZero()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_number),
                isDisplayed(),
                withText(contactWithFilter?.number))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_validity),
                if (contactWithFilter?.phoneNumberValidity().isNull()) not(isDisplayed()) else isDisplayed(),
                withText(if (contactWithFilter?.phoneNumberValidity().isNull()) String.EMPTY else targetContext.getString(contactWithFilter?.phoneNumberValidity().orZero())))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_name),
                isDisplayed(),
                withText(if (contactWithFilter?.contactName.isNullOrEmpty()) targetContext.getString(R.string.details_number_not_from_contacts) else contactWithFilter?.contactName))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_divider),
                isDisplayed(),
                withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue)))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_filter_title),
                isDisplayed(),
                withText(if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) targetContext.getString(R.string.details_number_contact_without_filter) else if (contactWithFilter?.filterWithFilteredNumberUIModel?.isBlocker().isTrue()) targetContext.getString(R.string.details_number_block_with_filter) else targetContext.getString(R.string.details_number_permit_with_filter)),
                withTextColor(if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) R.color.text_color_grey else if (contactWithFilter?.filterWithFilteredNumberUIModel?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_filter_value),
                isDisplayed(),
                withText(if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) String.EMPTY else contactWithFilter?.filterWithFilteredNumberUIModel?.filter),
                withTextColor(if (contactWithFilter?.filterWithFilteredNumberUIModel?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green),
                withDrawable(if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) null else contactWithFilter?.filterWithFilteredNumberUIModel?.conditionTypeSmallIcon()))))))
            perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
            assertEquals(R.id.detailsNumberDataFragment, navController?.currentDestination?.id)
            assertEquals(contactWithFilter, navController?.backStack?.last()?.arguments?.parcelable<ContactWithFilterUIModel>("numberData"))
        }
    }
}
