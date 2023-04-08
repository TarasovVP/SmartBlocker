package com.tarasovvp.smartblocker.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.FILTERING_LIST
import com.tarasovvp.smartblocker.TestUtils.IS_CALL_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.presentation.dialogs.NumberDataFilteringDialog
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseNumberDataFilteringInstrumentedTest: BaseInstrumentedTest() {

    private var fragment: Fragment? = null
    private var isCallList = false

    @Before
    override fun setUp() {
        super.setUp()
        TestUtils.launchFragmentInHiltContainer<NumberDataFilteringDialog>(
            bundleOf(FILTERING_LIST to arrayListOf<Int>(),
                IS_CALL_LIST to (this@BaseNumberDataFilteringInstrumentedTest is CallNumberDataFilteringInstrumentedTest))) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.detailsFilterFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            isCallList = arguments?.getBoolean(IS_CALL_LIST, false).isTrue()
        }
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkDialogNumberDataFilteringTitle() {
        onView(withId(R.id.dialog_number_data_filtering_title)).check(matches(isDisplayed())).check(matches(withText(R.string.filter_condition_title)))
    }

    @Test
    fun checkDialogNumberDataWithBlocker() {
        onView(withId(R.id.dialog_number_data_with_blocker)).apply {
            if (isCallList) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.filter_contact_blocker)))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            }
        }
    }

    @Test
    fun checkDialogNumberDataWithPermission() {
        onView(withId(R.id.dialog_number_data_with_permission)).apply {
            if (isCallList) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.filter_contact_permission)))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            }
        }
    }

    @Test
    fun checkDialogNumberDataByBlocker() {
        onView(withId(R.id.dialog_number_data_by_blocker)).apply {
            if (isCallList) {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.filter_call_blocked)))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @Test
    fun checkDialogNumberDataByPermission() {
        onView(withId(R.id.dialog_number_data_by_permission)).apply {
            if (isCallList) {
                check(matches(isDisplayed()))
                check(matches(withText(R.string.filter_call_permitted)))
                check(matches(not(isChecked())))
                perform(click())
                check(matches(isChecked()))
            } else {
                check(matches(not(isDisplayed())))
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
        onView(withId(R.id.dialog_number_data_confirm))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.button_ok)))
            .perform(click())

    }

    @Test
    fun checkDialogNumberDataConfirmTwoChecked() {
        runBlocking(Dispatchers.Main) {
            fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, bundle ->
                assertEquals(2, bundle.getIntegerArrayList(FILTER_CONDITION_LIST)?.size)
            }
        }
        if (isCallList) {
            onView(withId(R.id.dialog_number_data_by_blocker)).perform(click())
            onView(withId(R.id.dialog_number_data_by_permission)).perform(click())
        } else {
            onView(withId(R.id.dialog_number_data_with_blocker)).perform(click())
            onView(withId(R.id.dialog_number_data_with_permission)).perform(click())
        }
        onView(withId(R.id.dialog_number_data_confirm))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.button_ok)))
            .perform(click())
    }

    @Test
    fun checkDialogNumberDataCancel() {
        onView(withId(R.id.dialog_number_data_cancel))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.button_cancel)))
            .perform(click())
        onView(withId(R.id.container)).check(doesNotExist())
    }
}
