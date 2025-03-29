package com.tarasovvp.smartblocker.number.list.list_contact

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
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.Suppress
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.atPosition
import com.tarasovvp.smartblocker.TestUtils.contactWithFilterUIModelList
import com.tarasovvp.smartblocker.TestUtils.hasItemCount
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withBitmap
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactFragment
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isNull
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

@Suppress
@HiltAndroidTest
open class BaseListContactInstrumentedTest : BaseInstrumentedTest() {
    private var contactList: List<ContactWithFilterUIModel>? = null
    private var filterIndexList: ArrayList<Int> = arrayListOf()
    private var fragment: Fragment? = null

    @Before
    override fun setUp() {
        super.setUp()
        contactList =
            if (this is EmptyListContactInstrumentedTest) listOf() else contactWithFilterUIModelList()
        launchFragmentInHiltContainer<ListContactFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listContactFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            (this as ListContactFragment).apply {
                filterIndexList = filterIndexes ?: arrayListOf()
                viewModel.contactListLiveData.postValue(contactList)
            }
        }
        waitUntilViewIsDisplayed(
            if (this@BaseListContactInstrumentedTest is EmptyListContactInstrumentedTest) {
                withId(
                    R.id.list_contact_empty,
                )
            } else {
                withText(contactList?.get(0)?.contactName)
            },
        )
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }

    @Test
    fun checkListContactCheck() {
        onView(withId(R.id.list_contact_check)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isChecked())))
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            if (contactList.isNullOrEmpty()) {
                check(matches(not(isEnabled())))
            } else {
                check(matches(isEnabled()))
                perform(click())
                assertEquals(R.id.numberDataFilteringDialog, navController?.currentDestination?.id)
            }
        }
    }

    @Test
    fun checkListContactCheckOneFilter() {
        onView(withId(R.id.list_contact_check)).apply {
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(
                        FILTER_CONDITION_LIST to
                            arrayListOf<Int>().apply {
                                add(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
                            },
                    ),
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (contactList.isNullOrEmpty()) {
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
        onView(withId(R.id.list_contact_check)).apply {
            check(matches(withText(targetContext.numberDataFilteringText(filterIndexList))))
            runBlocking(Dispatchers.Main) {
                fragment?.setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(
                        FILTER_CONDITION_LIST to
                            arrayListOf<Int>().apply {
                                add(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
                                add(NumberDataFiltering.CONTACT_WITH_PERMISSION.ordinal)
                            },
                    ),
                )
                fragment?.setFragmentResultListener(FILTER_CONDITION_LIST) { _, _ ->
                    if (contactList.isNullOrEmpty()) {
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
    fun checkListContactInfo() {
        onView(withId(R.id.list_contact_info))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(R.drawable.ic_info)))
            .perform(click())
        assertEquals(R.id.infoFragment, navController?.currentDestination?.id)
    }

    @Test
    fun checkListContactRefresh() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_contact_refresh)).check(matches(isDisplayed()))
            onView(withId(R.id.list_contact_refresh)).perform(swipeDown())
        }
    }

    @Test
    fun checkListContactRecyclerView() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.list_contact_empty)).check(matches(not(isDisplayed())))
            onView(withId(R.id.list_contact_recycler_view))
                .check(matches(isDisplayed()))
                .check(
                    matches(
                        hasItemCount(
                            contactList?.size.orZero() +
                                contactList?.groupBy {
                                    if (it.contactName.isEmpty()) String.EMPTY else it.contactName.firstOrNull()
                                }?.size.orZero(),
                        ),
                    ),
                )
        }
    }

    @Test
    fun checkContactItemHeaderOne() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            val firstHeader =
                contactList?.groupBy {
                    if (it.contactName.isEmpty()) {
                        String.EMPTY
                    } else {
                        it.contactName.firstOrNull()
                            .toString()
                    }
                }?.keys?.first()
            onView(withId(R.id.list_contact_recycler_view)).check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.item_header_text), withText(firstHeader))),
                    ),
                ),
            )
        }
    }

    @Test
    fun checkContactItemOne() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            checkContactItem(1, contactList?.firstOrNull())
        }
    }

    @Test
    fun checkContactItemTwo() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
        } else {
            checkContactItem(3, contactList?.get(1))
        }
    }

    @Test
    fun checkListContactEmpty() {
        if (contactList.isNullOrEmpty()) {
            onView(withId(R.id.list_contact_empty)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_state_description)).check(matches(isDisplayed()))
                .check(matches(withText(EmptyState.EMPTY_STATE_CONTACTS.description())))
            // TODO drawable
            // onView(withId(R.id.empty_state_icon)).check(matches(isDisplayed())).check(matches(withDrawable(R.drawable.ic_empty_state)))
        } else {
            onView(withId(R.id.list_contact_empty)).check(matches(not(isDisplayed())))
        }
    }

    private fun checkContactItem(
        position: Int,
        contactWithFilter: ContactWithFilterUIModel?,
    ) {
        onView(withId(R.id.list_contact_recycler_view)).apply {
            check(
                matches(
                    atPosition(
                        position,
                        hasDescendant(
                            allOf(
                                withId(R.id.item_contact_avatar),
                                isDisplayed(),
                                withBitmap(
                                    contactWithFilter?.placeHolder(targetContext)?.toBitmap(),
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
                                withDrawable(
                                    contactWithFilter?.filterWithFilteredNumberUIModel?.filterTypeIcon()
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
                                withId(R.id.item_contact_number),
                                isDisplayed(),
                                withText(contactWithFilter?.number),
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
                                if (contactWithFilter?.phoneNumberValidity().isNull()) {
                                    not(
                                        isDisplayed(),
                                    )
                                } else {
                                    isDisplayed()
                                },
                                withText(
                                    if (contactWithFilter?.phoneNumberValidity()
                                            .isNull()
                                    ) {
                                        String.EMPTY
                                    } else {
                                        targetContext.getString(
                                            contactWithFilter?.phoneNumberValidity().orZero(),
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
                                    if (contactWithFilter?.contactName.isNullOrEmpty()) {
                                        targetContext.getString(
                                            R.string.details_number_not_from_contacts,
                                        )
                                    } else {
                                        contactWithFilter?.contactName
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
                                    if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) {
                                        targetContext.getString(
                                            R.string.details_number_contact_without_filter,
                                        )
                                    } else if (contactWithFilter?.filterWithFilteredNumberUIModel?.isBlocker()
                                            .isTrue()
                                    ) {
                                        targetContext.getString(R.string.details_number_block_with_filter)
                                    } else {
                                        targetContext.getString(
                                            R.string.details_number_permit_with_filter,
                                        )
                                    },
                                ),
                                withTextColor(
                                    if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) {
                                        R.color.text_color_grey
                                    } else if (contactWithFilter?.filterWithFilteredNumberUIModel?.isBlocker()
                                            .isTrue()
                                    ) {
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
                                    if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) String.EMPTY else contactWithFilter?.filterWithFilteredNumberUIModel?.filter,
                                ),
                                withTextColor(
                                    if (contactWithFilter?.filterWithFilteredNumberUIModel?.isBlocker()
                                            .isTrue()
                                    ) {
                                        R.color.sunset
                                    } else {
                                        R.color.islamic_green
                                    },
                                ),
                                withDrawable(
                                    if (contactWithFilter?.filterWithFilteredNumberUIModel.isNull()) null else contactWithFilter?.filterWithFilteredNumberUIModel?.conditionTypeSmallIcon(),
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
                contactWithFilter,
                navController?.backStack?.last()?.arguments?.parcelable<ContactWithFilterUIModel>("numberData"),
            )
        }
    }
}
