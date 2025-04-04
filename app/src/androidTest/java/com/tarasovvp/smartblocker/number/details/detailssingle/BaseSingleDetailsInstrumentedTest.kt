package com.tarasovvp.smartblocker.number.details.detailssingle

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.presentation.uimodels.NumberDataUIModel
import com.tarasovvp.smartblocker.utils.extensions.descriptionRes
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

@androidx.test.filters.Suppress
@HiltAndroidTest
abstract class BaseSingleDetailsInstrumentedTest : BaseInstrumentedTest() {
    @get:Rule
    var name: TestName = TestName()

    abstract fun checkListItem(position: Int)

    protected var dataList = arrayListOf<NumberDataUIModel>()

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
                val descriptionText =
                    when (this@BaseSingleDetailsInstrumentedTest) {
                        is SingleDetailsFiltersInstrumentedTest -> EmptyState.EMPTY_STATE_NUMBERS.descriptionRes()
                        is SingleDetailsNumberDataInstrumentedTestUIModel -> EmptyState.EMPTY_STATE_FILTERS.descriptionRes()
                        else -> EmptyState.EMPTY_STATE_FILTERED_CALLS.descriptionRes()
                    }
                onView(withId(R.id.empty_state_description)).check(matches(withText(descriptionText)))
                // TODO drawable
                // onView(withId(R.id.empty_state_icon)).check(matches(withDrawable(R.drawable.ic_empty_state)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @After
    override fun tearDown() {
        super.tearDown()
        dataList.clear()
    }
}
