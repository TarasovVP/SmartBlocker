package com.tarasovvp.smartblocker.number.list.list_contact

import androidx.test.espresso.Espresso.onView
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
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.utils.extensions.orZero

@Suppress
@HiltAndroidTest
open class BaseListContactInstrumentedTest: BaseInstrumentedTest() {

    protected var contactWithFilterList: List<ContactWithFilter>? = null

    @Test
    fun checkListContactCheck() {
        onView(withId(R.id.list_contact_check)).check(matches(isDisplayed()))

    }

    @Test
    fun checkListContactInfo() {
        onView(withId(R.id.list_contact_info)).check(matches(isDisplayed()))

    }

    @Test
    fun checkListContactRefresh() {
        onView(withId(R.id.list_contact_refresh)).check(matches(isDisplayed()))
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
            onView(withId(R.id.list_contact_recycler_view)).check(matches(not(isDisplayed())))
        }
    }
}
