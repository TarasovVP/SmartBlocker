package com.tarasovvp.smartblocker.number.details.details_single

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.LIST_EMPTY
import com.tarasovvp.smartblocker.TestUtils.atPosition
import com.tarasovvp.smartblocker.TestUtils.filteredCallList
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withBitmap
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.utils.extensions.isNotTrue
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
class SingleDetailsFilteredCallsInstrumentedTest: BaseSingleDetailsInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        dataList = if (name.methodName.contains(LIST_EMPTY)) arrayListOf() else filteredCallList()
        launchFragmentInHiltContainer<SingleDetailsFragment> (fragmentArgs = bundleOf(NUMBER_TYPE to FilteredCallWithFilter::class.simpleName.orEmpty())) {
            (this as SingleDetailsFragment).updateNumberDataList(dataList, true)
        }
    }

    override fun checkListItem(position: Int) {
        val callWithFilter = dataList[position] as? CallWithFilter

        onView(withId(R.id.single_details_list)).apply {
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_container),
                isDisplayed(),
                withAlpha(if (callWithFilter?.call?.isDeleteMode.isTrue() && callWithFilter?.call?.isFilteredCall.isNotTrue()) 0.5f else 1f))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_avatar),
                isDisplayed(),
                withBitmap(callWithFilter?.call?.placeHolder(targetContext)?.toBitmap()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_filter),
                if (callWithFilter?.call?.isExtract.isNotTrue() || callWithFilter?.filterWithCountryCode?.filter?.filterType == 0) not(isDisplayed()) else isDisplayed(),
                withDrawable(callWithFilter?.filterWithCountryCode?.filter?.filterTypeIcon()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_number),
                isDisplayed(),
                withText(if (callWithFilter?.call?.number.isNullOrEmpty()) targetContext.getString(R.string.details_number_hidden) else callWithFilter?.highlightedSpanned.toString()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_time),
                if (callWithFilter?.call?.isExtract.isTrue() || callWithFilter?.call?.isFilteredCallDetails.isTrue() || callWithFilter?.call?.isFilteredCallDelete().isTrue()) not(isDisplayed()) else isDisplayed(),
                withText(callWithFilter?.call?.timeFromCallDate()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_name),
                isDisplayed(),
                withText(if (callWithFilter?.call?.isNameEmpty().isTrue()) if (callWithFilter?.call?.isExtract.isTrue()) targetContext.getString(R.string.details_number_from_call_log) else targetContext.getString(R.string.details_number_not_from_contacts) else callWithFilter?.call?.callName))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_type_icon),
                if (callWithFilter?.call?.isDeleteMode.isTrue() && callWithFilter?.call?.isCallFiltered().isTrue() || callWithFilter?.call?.isExtract.isTrue()) not(isDisplayed()) else isDisplayed(),
                withDrawable(callWithFilter?.call?.callIcon()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_delete),
                if (callWithFilter?.call?.isDeleteMode.isTrue() && callWithFilter?.call?.isCallFiltered().isTrue()) isDisplayed() else not(isDisplayed()),
                if (callWithFilter?.call?.isCheckedForDelete.isTrue()) isChecked() else not(isChecked()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_divider),
                isDisplayed(),
                withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue)))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_filter_title),
                isDisplayed(),
                withText(callWithFilter?.call?.callFilterTitle(callWithFilter.filterWithCountryCode?.filter).orZero()),
                withTextColor(callWithFilter?.call?.callFilterTint(callWithFilter.filterWithCountryCode?.filter).orZero()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_filter_value),
                isDisplayed(),
                withText(callWithFilter?.call?.callFilterValue().orEmpty()),
                withTextColor(callWithFilter?.call?.callFilterTint(callWithFilter.filterWithCountryCode?.filter).orZero()),
                withDrawable(callWithFilter?.call?.callFilterIcon()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_call_container),
                isDisplayed(),
                withAlpha(if (callWithFilter?.call?.isDeleteMode.isTrue() && callWithFilter?.call?.isFilteredCall.isNotTrue()) 0.5f else 1f))))))
            perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
        }
    }
}
