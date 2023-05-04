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
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.numberDataList
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withBitmap
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
class SingleDetailsNumberDataInstrumentedTestUIModel: BaseSingleDetailsInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        dataList = if (name.methodName.contains(LIST_EMPTY)) arrayListOf() else numberDataList()
        launchFragmentInHiltContainer<SingleDetailsFragment> (fragmentArgs = bundleOf(NUMBER_TYPE to NumberDataUIModel::class.simpleName.orEmpty())) {
            (this as SingleDetailsFragment).updateNumberDataList(dataList)
        }
    }

    override fun checkListItem(position: Int) {
        (dataList[position] as? ContactWithFilter)?.let { contactWithFilter ->
        onView(withId(R.id.single_details_list)).apply {
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_avatar),
                isDisplayed(),
                withBitmap(contactWithFilter.contact?.placeHolder(targetContext)?.toBitmap()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_filter),
                isDisplayed(),
                withDrawable(contactWithFilter.filterWithCountryCode?.filter?.filterTypeIcon().orZero()))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_number),
                isDisplayed(),
                //TODO resource not found
                /*withText(contactWithFilter.contact?.number.highlightedSpanned(String.EMPTY, null, ContextCompat.getColor(targetContext, R.color.text_color_black)).toString())*/)))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_validity),
                if (contactWithFilter.contact?.phoneNumberValidity().isNull()) not(isDisplayed()) else isDisplayed(),
                withText(if (contactWithFilter.contact?.phoneNumberValidity().isNull()) String.EMPTY else targetContext.getString(contactWithFilter.contact?.phoneNumberValidity().orZero())))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_name),
                isDisplayed(),
                withText(if (contactWithFilter.contact?.isNameEmpty().isTrue()) targetContext.getString(R.string.details_number_not_from_contacts) else contactWithFilter.contact?.name))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_divider),
                isDisplayed(),
                withBackgroundColor(ContextCompat.getColor(targetContext, R.color.light_steel_blue)))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_filter_title),
                isDisplayed(),
                withText(if (contactWithFilter.contact?.isFilterNullOrEmpty().isTrue()) targetContext.getString(R.string.details_number_contact_without_filter) else if (contactWithFilter.filterWithCountryCode?.filter?.isBlocker().isTrue()) targetContext.getString(R.string.details_number_block_with_filter) else targetContext.getString(R.string.details_number_permit_with_filter)),
                withTextColor(if (contactWithFilter.contact?.isFilterNullOrEmpty().isTrue()) R.color.text_color_grey else if (contactWithFilter.filterWithCountryCode?.filter?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green))))))
            check(matches(atPosition(position, hasDescendant(allOf(withId(R.id.item_contact_filter_value),
                isDisplayed(),
                withText(if (contactWithFilter.contact?.isFilterNullOrEmpty().isTrue()) String.EMPTY else contactWithFilter.filterWithCountryCode?.filter?.filter),
                withTextColor(if (contactWithFilter.filterWithCountryCode?.filter?.isBlocker().isTrue()) R.color.sunset else R.color.islamic_green),
                withDrawable(if (contactWithFilter.filterWithCountryCode?.filter.isNull()) null else contactWithFilter.filterWithCountryCode?.filter?.conditionTypeSmallIcon()))))))
            perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
        }
        }
    }
}
