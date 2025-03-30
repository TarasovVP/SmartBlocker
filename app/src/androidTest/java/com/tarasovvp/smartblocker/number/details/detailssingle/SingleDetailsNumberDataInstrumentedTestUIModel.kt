package com.tarasovvp.smartblocker.number.details.detailssingle

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.LIST_EMPTY
import com.tarasovvp.smartblocker.TestUtils.atPosition
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.numberDataUIModelList
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withBitmap
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.presentation.uimodels.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.NumberDataUIModel
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
class SingleDetailsNumberDataInstrumentedTestUIModel : BaseSingleDetailsInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        dataList =
            if (name.methodName.contains(LIST_EMPTY)) arrayListOf() else numberDataUIModelList()
        launchFragmentInHiltContainer<SingleDetailsFragment>(
            fragmentArgs = bundleOf(NUMBER_TYPE to NumberDataUIModel::class.simpleName.orEmpty()),
        ) {
            (this as SingleDetailsFragment).updateNumberDataList(dataList)
        }
    }

    override fun checkListItem(position: Int) {
        (dataList[position] as? ContactWithFilterUIModel)?.let { contactWithFilterUIModel ->
            onView(withId(R.id.single_details_list)).apply {
                check(
                    matches(
                        atPosition(
                            position,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_contact_avatar),
                                    isDisplayed(),
                                    withBitmap(
                                        contactWithFilterUIModel.placeHolder(targetContext)
                                            ?.toBitmap(),
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
                check(
                    matches(
                        atPosition(
                            position,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_contact_filter),
                                    isDisplayed(),
                                    withDrawable(contactWithFilterUIModel.filterWithFilteredNumberUIModel.filterTypeIcon()),
                                ),
                            ),
                        ),
                    ),
                )
                check(
                    matches(
                        atPosition(
                            position,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_contact_number),
                                    isDisplayed(),
                                    // TODO resource not found
                                    // withText(contactWithFilter.contact?.number.highlightedSpanned(String.EMPTY, null, ContextCompat.getColor(targetContext, R.color.text_color_black)).toString())
                                ),
                            ),
                        ),
                    ),
                )
                check(
                    matches(
                        atPosition(
                            position,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_contact_validity),
                                    if (contactWithFilterUIModel.phoneNumberValidity()
                                            .isNull()
                                    ) {
                                        not(isDisplayed())
                                    } else {
                                        isDisplayed()
                                    },
                                    withText(
                                        if (contactWithFilterUIModel.phoneNumberValidity()
                                                .isNull()
                                        ) {
                                            String.EMPTY
                                        } else {
                                            targetContext.getString(
                                                contactWithFilterUIModel.phoneNumberValidity()
                                                    .orZero(),
                                            )
                                        },
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
                check(
                    matches(
                        atPosition(
                            position,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_contact_name),
                                    isDisplayed(),
                                    withText(
                                        contactWithFilterUIModel.contactName.ifEmpty {
                                            targetContext.getString(
                                                R.string.details_number_not_from_contacts,
                                            )
                                        },
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
                check(
                    matches(
                        atPosition(
                            position,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_contact_divider),
                                    isDisplayed(),
                                    withBackgroundColor(
                                        ContextCompat.getColor(
                                            targetContext,
                                            R.color.light_steel_blue,
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
                check(
                    matches(
                        atPosition(
                            position,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_contact_filter_title),
                                    isDisplayed(),
                                    withText(
                                        if (contactWithFilterUIModel.filterWithFilteredNumberUIModel.isNull()) {
                                            targetContext.getString(
                                                R.string.details_number_contact_without_filter,
                                            )
                                        } else if (contactWithFilterUIModel.filterWithFilteredNumberUIModel.isBlocker()) {
                                            targetContext.getString(
                                                R.string.details_number_block_with_filter,
                                            )
                                        } else {
                                            targetContext.getString(R.string.details_number_permit_with_filter)
                                        },
                                    ),
                                    withTextColor(
                                        if (contactWithFilterUIModel.filterWithFilteredNumberUIModel.isNull()) {
                                            R.color.text_color_grey
                                        } else if (contactWithFilterUIModel.filterWithFilteredNumberUIModel.isBlocker()) {
                                            R.color.sunset
                                        } else {
                                            R.color.islamic_green
                                        },
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
                check(
                    matches(
                        atPosition(
                            position,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_contact_filter_value),
                                    isDisplayed(),
                                    withText(
                                        if (contactWithFilterUIModel.filterWithFilteredNumberUIModel.isNull()) {
                                            String.EMPTY
                                        } else {
                                            contactWithFilterUIModel.filterWithFilteredNumberUIModel.filter
                                        },
                                    ),
                                    withTextColor(
                                        if (contactWithFilterUIModel.filterWithFilteredNumberUIModel.isBlocker()
                                                .isTrue()
                                        ) {
                                            R.color.sunset
                                        } else {
                                            R.color.islamic_green
                                        },
                                    ),
                                    withDrawable(
                                        if (contactWithFilterUIModel.filterWithFilteredNumberUIModel.filter.isNull()) {
                                            null
                                        } else {
                                            contactWithFilterUIModel.filterWithFilteredNumberUIModel.conditionTypeSmallIcon()
                                        },
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
                perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        position,
                        click(),
                    ),
                )
            }
        }
    }
}
