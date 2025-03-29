package com.tarasovvp.smartblocker.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.FILTERING_LIST
import com.tarasovvp.smartblocker.TestUtils.childOf
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.presentation.dialogs.NumberDataFilteringDialog
import com.tarasovvp.smartblocker.utils.extensions.orZero
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseNumberDataUIModelFilteringInstrumentedTest : BaseInstrumentedTest() {
    private var fragment: Fragment? = null
    private var previousDestination = 0

    @Before
    override fun setUp() {
        super.setUp()
        val previousDestinationId =
            when (this) {
                is CallNumberDataUIModelFilteringInstrumentedTest -> R.id.listCallFragment
                is ContactNumberDataUIModelFilteringInstrumentedTest -> R.id.listContactFragment
                else -> R.id.listBlockerFragment
            }
        launchFragmentInHiltContainer<NumberDataFilteringDialog>(
            bundleOf(
                "previousDestinationId" to previousDestinationId,
                FILTERING_LIST to arrayListOf<Int>(),
            ),
        ) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.detailsFilterFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            previousDestination = arguments?.getInt("previousDestinationId").orZero()
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkDialogNumberDataFilteringTitle() {
        onView(withId(R.id.dialog_number_data_filtering_title)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.filter_condition_title)))
    }

    @Test
    fun checkDialogNumberDataConditionFull() {
        onView(
            allOf(
                withId(R.id.item_check_box),
                childOf(withId(R.id.dialog_number_data_filtering_container), 0),
            ),
        ).apply {
            if (previousDestination == R.id.listBlockerFragment) {
                check(matches(isDisplayed()))
                check(matches(withText(NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.title())))
                check(matches(withTagValue(equalTo(NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.ordinal))))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            } else {
                check(matches(not(withText(NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.title()))))
            }
        }
    }

    @Test
    fun checkDialogNumberDataConditionStart() {
        onView(
            allOf(
                withId(R.id.item_check_box),
                childOf(withId(R.id.dialog_number_data_filtering_container), 1),
            ),
        ).apply {
            if (previousDestination == R.id.listBlockerFragment) {
                check(matches(isDisplayed()))
                check(matches(withText(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.title())))
                check(matches(withTagValue(equalTo(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal))))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            } else {
                check(matches(not(withText(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.title()))))
            }
        }
    }

    @Test
    fun checkDialogNumberDataConditionContain() {
        onView(
            allOf(
                withId(R.id.item_check_box),
                childOf(withId(R.id.dialog_number_data_filtering_container), 2),
            ),
        ).apply {
            if (previousDestination == R.id.listBlockerFragment) {
                check(matches(isDisplayed()))
                check(matches(withText(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.title())))
                check(matches(withTagValue(equalTo(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal))))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            } else {
                check(doesNotExist())
            }
        }
    }

    @Test
    fun checkDialogNumberDataWithBlocker() {
        onView(
            allOf(
                withId(R.id.item_check_box),
                childOf(withId(R.id.dialog_number_data_filtering_container), 0),
            ),
        ).apply {
            if (previousDestination == R.id.listContactFragment) {
                check(matches(isDisplayed()))
                check(matches(withText(NumberDataFiltering.CONTACT_WITH_BLOCKER.title())))
                check(matches(withTagValue(equalTo(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal))))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            } else {
                check(matches(not(withText(NumberDataFiltering.CONTACT_WITH_BLOCKER.title()))))
            }
        }
    }

    @Test
    fun checkDialogNumberDataWithPermission() {
        onView(
            allOf(
                withId(R.id.item_check_box),
                childOf(withId(R.id.dialog_number_data_filtering_container), 1),
            ),
        ).apply {
            if (previousDestination == R.id.listContactFragment) {
                check(matches(isDisplayed()))
                check(matches(withText(NumberDataFiltering.CONTACT_WITH_PERMISSION.title())))
                check(matches(withTagValue(equalTo(NumberDataFiltering.CONTACT_WITH_PERMISSION.ordinal))))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            } else {
                check(matches(not(withText(NumberDataFiltering.CONTACT_WITH_PERMISSION.title()))))
            }
        }
    }

    @Test
    fun checkDialogNumberDataByBlocker() {
        onView(
            allOf(
                withId(R.id.item_check_box),
                childOf(withId(R.id.dialog_number_data_filtering_container), 0),
            ),
        ).apply {
            if (previousDestination == R.id.listCallFragment) {
                check(matches(isDisplayed()))
                check(matches(withText(NumberDataFiltering.CALL_BLOCKED.title())))
                check(matches(withTagValue(equalTo(NumberDataFiltering.CALL_BLOCKED.ordinal))))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            } else {
                check(matches(not(withText(NumberDataFiltering.CALL_BLOCKED.title()))))
            }
        }
    }

    @Test
    fun checkDialogNumberDataByPermission() {
        onView(
            allOf(
                withId(R.id.item_check_box),
                childOf(withId(R.id.dialog_number_data_filtering_container), 1),
            ),
        ).apply {
            if (previousDestination == R.id.listCallFragment) {
                check(matches(isDisplayed()))
                check(matches(withText(NumberDataFiltering.CALL_PERMITTED.title())))
                check(matches(withTagValue(equalTo(NumberDataFiltering.CALL_PERMITTED.ordinal))))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            } else {
                check(matches(not(withText(NumberDataFiltering.CALL_PERMITTED.title()))))
            }
        }
    }

    @Test
    fun checkDialogNumberDataConfirm() {
        runBlocking(Dispatchers.Main) {
            fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, bundle ->
                assertEquals(0, bundle.getIntegerArrayList(FILTER_CONDITION_LIST)?.size)
            }
        }
        onView(withId(R.id.dialog_number_data_filtering_confirm))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.button_ok)))
            .perform(click())
    }

    @Test
    fun checkDialogNumberDataCancel() {
        onView(withId(R.id.dialog_number_data_filtering_cancel))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.button_cancel)))
            .perform(click())
        onView(withId(R.id.container)).check(doesNotExist())
    }
}
