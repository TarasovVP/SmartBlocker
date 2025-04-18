package com.tarasovvp.smartblocker.number.list.listcall

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withAlpha
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.atPosition
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withBitmap
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.presentation.main.number.list.listcall.ListCallFragment
import com.tarasovvp.smartblocker.presentation.uimodels.CallWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isNotTrue
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.numberDataFilteringText
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.utils.extensions.parcelable
import com.tarasovvp.smartblocker.waitUntilViewIsDisplayed
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseListCallInstrumentedTest : BaseInstrumentedTest() {
    private var callList: List<CallWithFilterUIModel>? = null
    private var filterIndexList: ArrayList<Int> = arrayListOf()
    private var fragment: Fragment? = null

    @Before
    override fun setUp() {
        super.setUp()
        callList =
            if (this is EmptyListCallInstrumentedTest) listOf() else TestUtils.callWithFilterUIModelList()
        launchFragmentInHiltContainer<ListCallFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listCallFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            (this as ListCallFragment).apply {
                filterIndexList = filterIndexes ?: arrayListOf()
                viewModel.callListLiveData.postValue(callList)
            }
        }
        waitUntilViewIsDisplayed(
            if (this is EmptyListCallInstrumentedTest) {
                withId(R.id.list_call_empty)
            } else {
                withText(
                    callList?.firstOrNull()?.callName,
                )
            },
        )
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkListCallCheck() {
        onView(withId(R.id.list_call_check)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isChecked())))
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            if (callList.isNullOrEmpty()) {
                check(matches(not(isEnabled())))
            } else {
                check(matches(isEnabled()))
                perform(click())
                assertEquals(
                    R.id.numberDataFilteringDialog,
                    navController?.currentDestination?.id,
                )
            }
        }
    }

    @Test
    fun checkListContactCheckOneFilter() {
        onView(withId(R.id.list_call_check)).apply {
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(
                        FILTER_CONDITION_LIST to
                            arrayListOf<Int>().apply {
                                add(NumberDataFiltering.CALL_BLOCKED.ordinal)
                            },
                    ),
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (callList.isNullOrEmpty()) {
                        check(matches(not(isEnabled())))
                    } else {
                        check(matches(isChecked()))
                        check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
                    }
                }
            }
        }
    }

    @Test
    fun checkListContactCheckTwoFilters() {
        onView(withId(R.id.list_call_check)).apply {
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(
                        FILTER_CONDITION_LIST to
                            arrayListOf<Int>().apply {
                                add(NumberDataFiltering.CALL_BLOCKED.ordinal)
                                add(NumberDataFiltering.CALL_PERMITTED.ordinal)
                            },
                    ),
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (callList.isNullOrEmpty()) {
                        check(matches(not(isEnabled())))
                    } else {
                        check(matches(isChecked()))
                        check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
                    }
                }
            }
        }
    }

    @Test
    fun checkListCallInfo() {
        onView(withId(R.id.list_call_info))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(R.drawable.ic_info)))
            .perform(click())
        assertEquals(R.id.infoFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkListCallRefresh() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_call_refresh)).check(matches(isDisplayed()))
            onView(withId(R.id.list_call_refresh)).perform(swipeDown())
        }
    }

    @Test
    fun checkListCallRecyclerView() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_call_empty)).check(matches(not(isDisplayed())))
            onView(withId(R.id.list_call_recycler_view))
                .check(matches(isDisplayed()))
                .check(
                    matches(
                        hasItemCount(
                            callList?.size.orZero() +
                                callList?.groupBy {
                                    it.dateFromCallDate()
                                }?.size.orZero(),
                        ),
                    ),
                )
        }
    }

    @Test
    fun checkCallItemHeaderOne() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
        } else {
            val firstHeader =
                callList?.groupBy {
                    it.dateFromCallDate()
                }?.keys?.first()
            onView(withId(R.id.list_call_recycler_view))
                .check(
                    matches(
                        atPosition(
                            0,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_header_text),
                                    withText(firstHeader),
                                ),
                            ),
                        ),
                    ),
                )
        }
    }

    @Test
    fun checkCallItemOne() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
        } else {
            checkCallItem(1, callList?.get(0))
        }
    }

    @Test
    fun checkCallItemTwo() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
        } else {
            checkCallItem(2, callList?.get(1))
        }
    }

    @Test
    fun checkLogCallDeleteMode() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
        } else {
            val callWithFilter = callList?.get(1)
            onView(withId(R.id.list_call_recycler_view)).apply {
                check(
                    matches(
                        atPosition(
                            1,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_call_delete),
                                    if (callWithFilter?.isDeleteMode.isTrue() &&
                                        callWithFilter?.isCallFiltered()
                                            .isTrue()
                                    ) {
                                        isDisplayed()
                                    } else {
                                        not(isDisplayed())
                                    },
                                    if (callWithFilter?.isCheckedForDelete.isTrue()) {
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
                perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        1,
                        longClick(),
                    ),
                )
                waitUntilViewIsDisplayed(withText(R.string.list_call_delete_info))
            }
        }
    }

    @Test
    fun checkFilteredCallDeleteMode() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
        } else {
            val callWithFilter = callList?.get(1)
            onView(withId(R.id.list_call_recycler_view)).apply {
                check(
                    matches(
                        atPosition(
                            2,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_call_delete),
                                    if (callWithFilter?.isDeleteMode.isTrue() &&
                                        callWithFilter?.isCallFiltered()
                                            .isTrue()
                                    ) {
                                        isDisplayed()
                                    } else {
                                        not(isDisplayed())
                                    },
                                    if (callWithFilter?.isCheckedForDelete.isTrue()) {
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
                perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        2,
                        longClick(),
                    ),
                )
                perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        2,
                        click(),
                    ),
                )
                check(
                    matches(
                        atPosition(
                            2,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_call_delete),
                                    if (callWithFilter?.isDeleteMode.isTrue() &&
                                        callWithFilter?.isCallFiltered()
                                            .isTrue()
                                    ) {
                                        isDisplayed()
                                    } else {
                                        not(isDisplayed())
                                    },
                                    if (callWithFilter?.isCheckedForDelete.isTrue()) {
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
                perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        2,
                        click(),
                    ),
                )
                check(
                    matches(
                        atPosition(
                            2,
                            hasDescendant(
                                allOf(
                                    withId(R.id.item_call_delete),
                                    if (callWithFilter?.isDeleteMode.isTrue() &&
                                        callWithFilter?.isCallFiltered()
                                            .isTrue()
                                    ) {
                                        isDisplayed()
                                    } else {
                                        not(isDisplayed())
                                    },
                                    if (callWithFilter?.isCheckedForDelete.isTrue()) {
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
            }
        }
    }

    @Test
    fun checkListCallEmpty() {
        if (callList.isNullOrEmpty()) {
            onView(withId(R.id.list_call_empty)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_state_description)).check(matches(isDisplayed())).check(
                matches(
                    withText(
                        EmptyState.EMPTY_STATE_CALLS.description(),
                    ),
                ),
            )
            // TODO drawable
            // onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
        } else {
            onView(withId(R.id.list_call_empty)).check(matches(not(isDisplayed())))
        }
    }

    private fun checkCallItem(
        position: Int,
        callWithFilter: CallWithFilterUIModel?,
    ) {
        onView(withId(R.id.list_call_recycler_view)).apply {
            check(
                matches(
                    atPosition(
                        position,
                        hasDescendant(
                            allOf(
                                withId(R.id.item_call_container),
                                isDisplayed(),
                                withAlpha(
                                    if (callWithFilter?.isDeleteMode.isTrue() && callWithFilter?.isFilteredCall.isNotTrue()) 0.5f else 1f,
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
                                withBitmap(callWithFilter?.placeHolder(targetContext)?.toBitmap()),
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
                                if (
                                    callWithFilter?.isExtract.isNotTrue() ||
                                    callWithFilter?.filterWithFilteredNumberUIModel?.filterType == 0
                                ) {
                                    not(
                                        isDisplayed(),
                                    )
                                } else {
                                    isDisplayed()
                                },
                                withDrawable(callWithFilter?.filterWithFilteredNumberUIModel?.filterTypeIcon()),
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
                                    if (callWithFilter?.number.isNullOrEmpty()) {
                                        targetContext.getString(
                                            R.string.details_number_hidden,
                                        )
                                    } else {
                                        callWithFilter?.number
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
                                if (callWithFilter?.isExtract.isTrue() || callWithFilter?.isFilteredCallDetails.isTrue() ||
                                    callWithFilter?.isFilteredCallDelete()
                                        .isTrue()
                                ) {
                                    not(isDisplayed())
                                } else {
                                    isDisplayed()
                                },
                                withText(callWithFilter?.timeFromCallDate()),
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
                                    if (callWithFilter?.isNameEmpty()
                                            .isTrue()
                                    ) {
                                        if (callWithFilter?.isExtract.isTrue()) {
                                            targetContext.getString(
                                                R.string.details_number_from_call_log,
                                            )
                                        } else {
                                            targetContext.getString(
                                                R.string.details_number_not_from_contacts,
                                            )
                                        }
                                    } else {
                                        callWithFilter?.callName
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
                                if (callWithFilter?.isDeleteMode.isTrue() &&
                                    callWithFilter?.isCallFiltered()
                                        .isTrue() || callWithFilter?.isExtract.isTrue()
                                ) {
                                    not(isDisplayed())
                                } else {
                                    isDisplayed()
                                },
                                withDrawable(callWithFilter?.callIcon()),
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
                                if (callWithFilter?.isDeleteMode.isTrue() &&
                                    callWithFilter?.isCallFiltered()
                                        .isTrue()
                                ) {
                                    isDisplayed()
                                } else {
                                    not(isDisplayed())
                                },
                                if (callWithFilter?.isCheckedForDelete.isTrue()) {
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
                                withText(callWithFilter?.callFilterTitle().orZero()),
                                withTextColor(
                                    callWithFilter?.callFilterTint(callWithFilter.filterWithFilteredNumberUIModel)
                                        .orZero(),
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
                                    if (callWithFilter?.isEmptyFilter()
                                            .isTrue()
                                    ) {
                                        String.EMPTY
                                    } else {
                                        callWithFilter?.filteredNumber
                                    },
                                ),
                                withTextColor(
                                    callWithFilter?.callFilterTint(callWithFilter.filterWithFilteredNumberUIModel)
                                        .orZero(),
                                ),
                                withDrawable(callWithFilter?.callFilterIcon()),
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
                                    if (callWithFilter?.isDeleteMode.isTrue() && callWithFilter?.isFilteredCall.isNotTrue()) 0.5f else 1f,
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
            assertEquals(R.id.detailsNumberDataFragment, navController?.currentDestination?.id)
            assertEquals(
                callWithFilter,
                navController?.backStack?.last()?.arguments?.parcelable<CallWithFilterUIModel>("numberData"),
            )
        }
    }
}
