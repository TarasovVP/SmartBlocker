package com.tarasovvp.smartblocker.fragments.number

import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.firebase.FirebaseApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.UnitTestUtils.EMPTY
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.atPosition
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.numberDataWithFilterWithFilteredNumberUIModelList
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withDrawable
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withTextColor
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.descriptionRes
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.O_MR1],
    application = HiltTestApplication::class,
)
class SingleDetailsFiltersUnitTest : BaseFragmentUnitTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var dataList = arrayListOf<NumberDataUIModel>()

    @Before
    override fun setUp() {
        super.setUp()
        FirebaseApp.initializeApp(targetContext)
        dataList =
            if (name.methodName.contains(EMPTY)) arrayListOf() else numberDataWithFilterWithFilteredNumberUIModelList()
        launchFragmentInHiltContainer<SingleDetailsFragment>(
            fragmentArgs = bundleOf(NUMBER_TYPE to FilterWithFilteredNumberUIModel::class.simpleName.orEmpty()),
        ) {
            (this as SingleDetailsFragment).updateNumberDataList(dataList)
        }
    }

    @Test
    fun checkSingleDetailsList() {
        onView(withId(R.id.single_details_list)).apply {
            if (dataList.isEmpty()) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(FragmentTestUtils.hasItemCount(dataList.size)))
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
                onView(withId(R.id.empty_state_description)).check(matches(withText(EmptyState.EMPTY_STATE_NUMBERS.descriptionRes())))
                onView(withId(R.id.empty_state_icon)).check(matches(withDrawable(R.drawable.ic_empty_state)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    private fun checkListItem(position: Int) {
        val filterWithFilteredNumberUIModel = dataList[position] as? FilterWithFilteredNumberUIModel
        onView(withId(R.id.single_details_list)).apply {
            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
            check(
                matches(
                    atPosition(
                        position,
                        hasDescendant(
                            allOf(
                                withId(R.id.item_filter_avatar),
                                isDisplayed(),
                                withDrawable(filterWithFilteredNumberUIModel?.conditionTypeIcon()),
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
                                withId(R.id.item_filter_filter),
                                isDisplayed(),
                                withDrawable(filterWithFilteredNumberUIModel?.filterTypeIcon()),
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
                                withId(R.id.item_filter_value),
                                isDisplayed(),
                                withText(filterWithFilteredNumberUIModel?.highlightedSpanned.toString()),
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
                                withId(R.id.item_filter_name),
                                isDisplayed(),
                                withText(
                                    if (filterWithFilteredNumberUIModel?.conditionTypeName()
                                            .isNull()
                                    ) {
                                        String.EMPTY
                                    } else {
                                        targetContext.getString(
                                            filterWithFilteredNumberUIModel?.conditionTypeName()
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
                                withId(R.id.item_filter_delete),
                                if (filterWithFilteredNumberUIModel?.isDeleteMode.isTrue()) {
                                    isDisplayed()
                                } else {
                                    not(
                                        isDisplayed(),
                                    )
                                },
                                if (filterWithFilteredNumberUIModel?.isCheckedForDelete.isTrue()) {
                                    isChecked()
                                } else {
                                    not(
                                        isChecked(),
                                    )
                                },
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
                                withId(R.id.item_filter_divider),
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
                                withId(R.id.item_filter_contacts),
                                isDisplayed(),
                                withText(
                                    filterWithFilteredNumberUIModel?.filteredNumbersText(
                                        targetContext,
                                    ),
                                ),
                                withTextColor(
                                    filterWithFilteredNumberUIModel?.filterTypeTint().orZero(),
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
                                withId(R.id.item_filter_created),
                                isDisplayed(),
                                withText(
                                    String.format(
                                        targetContext.getString(R.string.filter_action_created),
                                        filterWithFilteredNumberUIModel?.filterCreatedDate(),
                                    ),
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

    @After
    override fun tearDown() {
        super.tearDown()
        dataList.clear()
    }
}
