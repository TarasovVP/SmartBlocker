package com.tarasovvp.smartblocker.number.details.details_single

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.numberDataWithFilterWithFilteredNumberUIModelList
import com.tarasovvp.smartblocker.TestUtils.LIST_EMPTY
import com.tarasovvp.smartblocker.TestUtils.atPosition
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
class SingleDetailsFiltersInstrumentedTest: BaseSingleDetailsInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        dataList = if (name.methodName.contains(LIST_EMPTY)) arrayListOf() else numberDataWithFilterWithFilteredNumberUIModelList()
        launchFragmentInHiltContainer<SingleDetailsFragment> (fragmentArgs = bundleOf(NUMBER_TYPE to FilterWithFilteredNumberUIModel::class.simpleName.orEmpty())) {
            (this as SingleDetailsFragment).updateNumberDataList(dataList)
        }
    }

    override fun checkListItem(position: Int) {
        val filterWithFilteredNumberUIModel = dataList[position] as? FilterWithFilteredNumberUIModel
        onView(withId(R.id.single_details_list)).apply {
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_avatar),
                isDisplayed(),
                withDrawable(filterWithFilteredNumberUIModel?.conditionTypeIcon()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_filter),
                isDisplayed(),
                withDrawable(filterWithFilteredNumberUIModel?.filterTypeIcon()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_value),
                isDisplayed(),
                withText(filterWithFilteredNumberUIModel?.highlightedSpanned.toString()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_name),
                isDisplayed(),
                withText(if (filterWithFilteredNumberUIModel?.conditionTypeName().isNull()) String.EMPTY else targetContext.getString(filterWithFilteredNumberUIModel?.conditionTypeName().orZero())))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_delete),
                if (filterWithFilteredNumberUIModel?.isDeleteMode.isTrue()) isDisplayed() else not(isDisplayed()),
                if (filterWithFilteredNumberUIModel?.isCheckedForDelete.isTrue()) isChecked() else not(isChecked()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_divider),
                isDisplayed(),
                withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue)))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_contacts),
                isDisplayed(),
                //TODO localisation
                //withText(filterWithFilteredNumberUIModel?.filteredContactsText(targetContext)),
                withTextColor(filterWithFilteredNumberUIModel?.filterTypeTint().orZero()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_filter_created),
                isDisplayed(),
                withText(String.format(targetContext.getString(R.string.filter_action_created), filterWithFilteredNumberUIModel?.filterCreatedDate())))))))
            perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
        }
    }
}
