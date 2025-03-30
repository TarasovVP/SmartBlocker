package com.tarasovvp.smartblocker.fragments.number

import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withAlpha
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
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.numberDataWithFilteredCallUIModelList
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withBitmap
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withDrawable
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withTextColor
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.presentation.uimodels.CallWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.NumberDataUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.descriptionRes
import com.tarasovvp.smartblocker.utils.extensions.isNotTrue
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
class SingleDetailsFilteredCallsUnitTest : BaseFragmentUnitTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var dataList = arrayListOf<NumberDataUIModel>()

    @Before
    override fun setUp() {
        super.setUp()
        FirebaseApp.initializeApp(targetContext)
        dataList =
            if (name.methodName.contains(EMPTY)) arrayListOf() else numberDataWithFilteredCallUIModelList()
        launchFragmentInHiltContainer<SingleDetailsFragment>(
            fragmentArgs = bundleOf(NUMBER_TYPE to CallWithFilterUIModel::class.simpleName.orEmpty()),
        ) {
            (this as SingleDetailsFragment).updateNumberDataList(dataList, true)
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
                onView(
                    withId(R.id.empty_state_description),
                ).check(matches(withText(EmptyState.EMPTY_STATE_FILTERED_CALLS.descriptionRes())))
                onView(withId(R.id.empty_state_icon)).check(matches(withDrawable(R.drawable.ic_empty_state)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    private fun checkListItem(position: Int) {
        val callWithFilterUIModel = dataList[position] as? CallWithFilterUIModel
        onView(withId(R.id.single_details_list)).apply {
            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
            check(
                matches(
                    atPosition(
                        position,
                        hasDescendant(
                            allOf(
                                withId(R.id.item_call_container),
                                isDisplayed(),
                                withAlpha(
                                    if (callWithFilterUIModel?.isDeleteMode.isTrue() &&
                                        callWithFilterUIModel?.isFilteredCall.isNotTrue()
                                    ) {
                                        0.5f
                                    } else {
                                        1f
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
                                withId(R.id.item_call_avatar),
                                isDisplayed(),
                                withBitmap(
                                    callWithFilterUIModel?.placeHolder(targetContext)?.toBitmap(),
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
                                withId(R.id.item_call_filter),
                                if (callWithFilterUIModel?.isExtract.isNotTrue() ||
                                    callWithFilterUIModel?.filterWithFilteredNumberUIModel?.filterType == 0
                                ) {
                                    not(
                                        isDisplayed(),
                                    )
                                } else {
                                    isDisplayed()
                                },
                                withDrawable(callWithFilterUIModel?.filterWithFilteredNumberUIModel?.filterTypeIcon()),
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
                                withId(R.id.item_call_number),
                                isDisplayed(),
                                withText(
                                    if (callWithFilterUIModel?.number.isNullOrEmpty()) {
                                        targetContext.getString(
                                            R.string.details_number_hidden,
                                        )
                                    } else {
                                        callWithFilterUIModel?.highlightedSpanned.toString()
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
                                withId(R.id.item_call_time),
                                if (callWithFilterUIModel?.isExtract.isTrue() || callWithFilterUIModel?.isFilteredCallDetails.isTrue() ||
                                    callWithFilterUIModel?.isFilteredCallDelete()
                                        .isTrue()
                                ) {
                                    not(isDisplayed())
                                } else {
                                    isDisplayed()
                                },
                                withText(callWithFilterUIModel?.timeFromCallDate()),
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
                                withId(R.id.item_call_name),
                                isDisplayed(),
                                withText(
                                    if (callWithFilterUIModel?.isNameEmpty()
                                            .isTrue()
                                    ) {
                                        if (callWithFilterUIModel?.isExtract.isTrue()) {
                                            targetContext.getString(
                                                R.string.details_number_from_call_log,
                                            )
                                        } else {
                                            targetContext.getString(R.string.details_number_not_from_contacts)
                                        }
                                    } else {
                                        callWithFilterUIModel?.callName
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
                                withId(R.id.item_call_type_icon),
                                if (callWithFilterUIModel?.isDeleteMode.isTrue() &&
                                    callWithFilterUIModel?.isCallFiltered()
                                        .isTrue() || callWithFilterUIModel?.isExtract.isTrue()
                                ) {
                                    not(isDisplayed())
                                } else {
                                    isDisplayed()
                                },
                                withDrawable(callWithFilterUIModel?.callIcon()),
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
                                withId(R.id.item_call_delete),
                                if (callWithFilterUIModel?.isDeleteMode.isTrue() &&
                                    callWithFilterUIModel?.isCallFiltered()
                                        .isTrue()
                                ) {
                                    isDisplayed()
                                } else {
                                    not(isDisplayed())
                                },
                                if (callWithFilterUIModel?.isCheckedForDelete.isTrue()) {
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
                                withId(R.id.item_call_divider),
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
                                withId(R.id.item_call_filter_title),
                                isDisplayed(),
                                withText(callWithFilterUIModel?.callFilterTitle().orZero()),
                                withTextColor(
                                    callWithFilterUIModel?.callFilterTint(
                                        callWithFilterUIModel.filterWithFilteredNumberUIModel,
                                    ).orZero(),
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
                                withId(R.id.item_call_filter_value),
                                isDisplayed(),
                                withText(
                                    if (callWithFilterUIModel?.isEmptyFilter()
                                            .isTrue()
                                    ) {
                                        String.EMPTY
                                    } else {
                                        callWithFilterUIModel?.filteredNumber
                                    },
                                ),
                                withTextColor(
                                    callWithFilterUIModel?.callFilterTint(
                                        callWithFilterUIModel.filterWithFilteredNumberUIModel,
                                    ).orZero(),
                                ),
                                withDrawable(callWithFilterUIModel?.callFilterIcon()),
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
                                withId(R.id.item_call_container),
                                isDisplayed(),
                                withAlpha(
                                    if (callWithFilterUIModel?.isDeleteMode.isTrue() &&
                                        callWithFilterUIModel?.isFilteredCall.isNotTrue()
                                    ) {
                                        0.5f
                                    } else {
                                        1f
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

    @After
    override fun tearDown() {
        super.tearDown()
        dataList.clear()
    }
}
