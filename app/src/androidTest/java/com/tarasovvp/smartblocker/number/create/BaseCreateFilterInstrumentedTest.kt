package com.tarasovvp.smartblocker.number.create

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withAlpha
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.BaseInstrumentedTest
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.FILTER_WITH_COUNTRY_CODE
import com.tarasovvp.smartblocker.TestUtils.LIST_EMPTY
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.TestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.TestUtils.contactWithFilterUIModelList
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.TestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.TestUtils.withDrawable
import com.tarasovvp.smartblocker.TestUtils.withTextColor
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterFragment
import com.tarasovvp.smartblocker.presentation.uimodels.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

@androidx.test.filters.Suppress
@HiltAndroidTest
open class BaseCreateFilterInstrumentedTest : BaseInstrumentedTest() {
    @get:Rule
    var name: TestName = TestName()

    private var fragment: CreateFilterFragment? = null
    private var filterWithCountryCodeUIModel: FilterWithCountryCodeUIModel? = null
    private var contactWithFilterUIModels = listOf<ContactWithFilterUIModel>()

    @Before
    override fun setUp() {
        super.setUp()
        contactWithFilterUIModels =
            if (name.methodName.contains(LIST_EMPTY)) arrayListOf() else contactWithFilterUIModelList()
        val filterCondition =
            when (this) {
                is CreateFilterConditionFullInstrumentedTest -> FilterCondition.FILTER_CONDITION_FULL.ordinal
                is CreateFilterConditionStartInstrumentedTest -> FilterCondition.FILTER_CONDITION_START.ordinal
                else -> FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
            }
        launchFragmentInHiltContainer<CreateFilterFragment>(
            fragmentArgs =
                bundleOf(
                    FILTER_WITH_COUNTRY_CODE to
                        FilterWithCountryCodeUIModel(
                            filterWithFilteredNumberUIModel =
                                FilterWithFilteredNumberUIModel(
                                    filter = TEST_FILTER,
                                    filterType = BLOCKER,
                                    conditionType = filterCondition,
                                ),
                            countryCodeUIModel =
                                CountryCodeUIModel(
                                    country = TEST_COUNTRY,
                                    countryCode = TEST_COUNTRY_CODE,
                                    numberFormat = TEST_NUMBER,
                                ),
                        ),
                ),
        ) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.createFilterFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this as? CreateFilterFragment
        }
        fragment?.viewModel?.contactWithFilterLiveData?.postValue(contactWithFilterUIModels)
        filterWithCountryCodeUIModel = fragment?.binding?.filterWithCountryCode
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.item_create_filter_container)).check(matches(isDisplayed()))
            .perform(click())
    }

    @Test
    fun checkItemCreateFilterAvatar() {
        onView(withId(R.id.item_create_filter_avatar))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.conditionTypeIcon())))
    }

    @Test
    fun checkItemCreateFilterFilter() {
        onView(withId(R.id.item_create_filter_filter))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.filterTypeIcon())))
    }

    @Test
    fun checkItemCreateFilterValue() {
        onView(withId(R.id.item_create_filter_value))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withText(
                        if (filterWithCountryCodeUIModel?.createFilter()
                                .isNullOrEmpty()
                        ) {
                            targetContext.getString(R.string.creating_filter_no_data)
                        } else {
                            filterWithCountryCodeUIModel?.createFilter()
                        },
                    ),
                ),
            )
    }

    @Test
    fun checkItemCreateFilterTypeTitle() {
        onView(withId(R.id.item_create_filter_name)).check(matches(isDisplayed()))
            .check(
                matches(
                    withText(
                        if (filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.filter.isNull()) {
                            filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.filter
                        } else {
                            targetContext.getString(
                                filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.conditionTypeName()
                                    .orZero(),
                            )
                        },
                    ),
                ),
            )
    }

    @Test
    fun checkItemCreateFilterDivider() {
        onView(withId(R.id.item_create_filter_divider)).check(matches(isDisplayed()))
            .check(
                matches(
                    withBackgroundColor(
                        ContextCompat.getColor(
                            targetContext,
                            R.color.light_steel_blue,
                        ),
                    ),
                ),
            )
    }

    @Test
    fun checkItemCreateFilterContactsDetails() {
        onView(withId(R.id.item_create_action_description)).check(matches(isDisplayed()))
            .check(matches(withText(filterWithCountryCodeUIModel?.filterActionText(targetContext))))
            .check(
                matches(
                    withTextColor(
                        filterWithCountryCodeUIModel?.filterCreateTint().orZero(),
                    ),
                ),
            )
    }

    @Test
    fun checkCreateFilterInputContainer() {
        onView(withId(R.id.create_filter_input_container)).check(matches(isDisplayed()))
    }

    @Test
    fun checkCreateFilterCountryCodeSpinner() {
        onView(withId(R.id.create_filter_country_code_spinner)).apply {
            if (filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.isTypeContain()
                    .isTrue()
            ) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                perform(click())
                assertEquals(R.id.countryCodeSearchDialog, navController?.currentDestination?.id)
            }
        }
    }

    @Test
    fun checkCreateFilterCountryCodeValue() {
        onView(withId(R.id.create_filter_country_code_value)).apply {
            if (filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.isTypeContain()
                    .isTrue()
            ) {
                check(matches(not(isDisplayed())))
            } else {
                check(matches(isDisplayed()))
                check(matches(withText(filterWithCountryCodeUIModel?.countryCodeUIModel?.countryCode)))
            }
        }
    }

    @Test
    fun checkCreateFilterInput() {
        onView(withId(R.id.create_filter_input)).check(matches(isDisplayed()))
    }

    @Test
    fun checkCreateFilterSubmit() {
        onView(withId(R.id.create_filter_submit))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withText(
                        filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.filterAction?.title()
                            .orZero(),
                    ),
                ),
            )
            .check(
                matches(
                    withTextColor(
                        filterWithCountryCodeUIModel?.filterActionTextTint().orZero(),
                    ),
                ),
            )
            .check(
                matches(
                    withAlpha(
                        if (filterWithCountryCodeUIModel?.isInvalidFilterAction()
                                .isTrue()
                        ) {
                            0.5f
                        } else {
                            1f
                        },
                    ),
                ),
            )
            .check(
                matches(
                    if (filterWithCountryCodeUIModel?.isInvalidFilterAction().isTrue()) {
                        not(
                            isEnabled(),
                        )
                    } else {
                        isEnabled()
                    },
                ),
            )
    }

    @Test
    fun checkCreateFilterListEmpty() {
        onView(withId(R.id.create_filter_empty_list)).apply {
            if (contactWithFilterUIModels.isEmpty()) {
                check(matches(isDisplayed()))
                onView(withId(R.id.empty_state_description)).check(matches(withText(EmptyState.EMPTY_STATE_CREATE_FILTER.description())))
                // TODO drawable
                // onView(withId(R.id.empty_state_icon)).check(matches(withDrawable(R.drawable.ic_empty_state)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }

    @After
    override fun tearDown() {
        super.tearDown()
        fragment = null
        filterWithCountryCodeUIModel = null
    }
}
