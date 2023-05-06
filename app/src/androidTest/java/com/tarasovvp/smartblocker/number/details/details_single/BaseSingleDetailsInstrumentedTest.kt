package com.tarasovvp.smartblocker.number.details.details_single

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.utils.extensions.descriptionRes
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

@androidx.test.filters.Suppress
@HiltAndroidTest
abstract class BaseSingleDetailsInstrumentedTest: BaseInstrumentedTest() {

    @get:Rule
    var name: TestName = TestName()

    abstract fun checkListItem(position: Int)

    protected var dataList = arrayListOf<NumberData>()

    @Test
    fun checkSingleDetailsList() {
        onView(withId(R.id.single_details_list)).apply {
            if (dataList.isEmpty()) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(hasItemCount(dataList.size)))
            }
        }
    }

    @Test
    fun checkSingleDetailsItemOne() {
        if (dataList.isEmpty()) {
            onView(withId(R.id.single_details_list_empty)).check(matches(isDisplayed()))
        } else {
            checkListItem(0)
        }
    }

    @Test
    fun checkSingleDetailsItemTwo() {
        if (dataList.isEmpty()) {
            onView(withId(R.id.single_details_list_empty)).check(matches(isDisplayed()))
        } else {
            checkListItem(1)
        }
    }

    @Test
    fun checkSingleDetailsItemThree() {
        if (dataList.isEmpty()) {
            onView(withId(R.id.single_details_list_empty)).check(matches(isDisplayed()))
        } else {
            checkListItem(2)
        }
    }

    @Test
    fun checkFilterDetailsNumberListEmpty() {
        onView(withId(R.id.single_details_list_empty)).apply {
            if (dataList.isEmpty()) {
                check(matches(isDisplayed()))
                val descriptionText = when(this@BaseSingleDetailsInstrumentedTest) {
                    is SingleDetailsFiltersInstrumentedTest -> EmptyState.EMPTY_STATE_NUMBERS.descriptionRes()
                    is SingleDetailsNumberDataInstrumentedTest -> EmptyState.EMPTY_STATE_FILTERS.descriptionRes()
                    else -> EmptyState.EMPTY_STATE_FILTERED_CALLS.descriptionRes()
                }
                onView(withId(R.id.empty_state_description)).check(matches(withText(descriptionText)))
                onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
                //TODO drawable
                //onView(withId(R.id.empty_state_icon)).check(matches(withDrawable(R.drawable.ic_empty_state)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }
}
