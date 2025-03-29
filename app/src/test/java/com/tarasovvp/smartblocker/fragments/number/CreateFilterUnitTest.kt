package com.tarasovvp.smartblocker.fragments.number

import android.os.Build
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
import com.google.firebase.FirebaseApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.FILTER_WITH_COUNTRY_CODE
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.contactWithFilterUIModelList
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withBackgroundColor
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withDrawable
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.withTextColor
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterFragment
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertEquals
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
class CreateFilterUnitTest : BaseFragmentUnitTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var fragment: CreateFilterFragment? = null
    private var filterWithCountryCodeUIModel: FilterWithCountryCodeUIModel? = null
    private var contactWithFilterUIModels = listOf<ContactWithFilterUIModel>()

    @Before
    override fun setUp() {
        super.setUp()
        FirebaseApp.initializeApp(targetContext)
        contactWithFilterUIModels = contactWithFilterUIModelList()
        val filterCondition =
            when {
                name.methodName.contains("Full") -> FilterCondition.FILTER_CONDITION_FULL.ordinal
                name.methodName.contains("Start") -> FilterCondition.FILTER_CONDITION_START.ordinal
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
        fragment?.viewModel?.contactWithFilterLiveData?.postValue(contactWithFilterUIModelList())
        filterWithCountryCodeUIModel = fragment?.binding?.filterWithCountryCode
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.item_create_filter_container)).check(matches(isDisplayed()))
            .perform(click())
    }

    @Test
    fun checkItemDetailsFilterAvatar() {
        onView(withId(R.id.item_create_filter_avatar))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.conditionTypeIcon())))
    }

    @Test
    fun checkItemDetailsFilterFilter() {
        onView(withId(R.id.item_create_filter_filter))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.filterTypeIcon())))
    }

    @Test
    fun checkItemDetailsFilterValue() {
        onView(withId(R.id.item_create_filter_value))
            .check(matches(isDisplayed()))
            .check(matches(withText(filterWithCountryCodeUIModel?.filterWithFilteredNumberUIModel?.filter)))
    }

    @Test
    fun checkItemDetailsFullFilterName() {
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
    fun checkItemDetailsStartFilterName() {
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
    fun checkItemDetailContainsFilterName() {
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
    fun checkItemDetailsFilterDivider() {
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
    fun checkItemDetailsFilterContactsDetails() {
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
    fun checkCreateFilterFullCountryCodeSpinner() {
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
    fun checkCreateFilterStartCountryCodeSpinner() {
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
    fun checkCreateFilterContainCountryCodeSpinner() {
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
    fun checkCreateFilterFullCountryCodeValue() {
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
    fun checkCreateFilterStartCountryCodeValue() {
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
    fun checkCreateFilterContainCountryCodeValue() {
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
    fun checkCreateFilterFullInput() {
        // TODO
        onView(withId(R.id.create_filter_input)).check(matches(isDisplayed()))
    }

    @Test
    fun checkCreateFilterStartInput() {
        // TODO
        onView(withId(R.id.create_filter_input)).check(matches(isDisplayed()))
    }

    @Test
    fun checkCreateFilterContainInput() {
        // TODO
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

    // TODO numberDataUIModelList?
    /*@Test
    fun checkCreateFilterNumberList() {
        if (numberDataUIModelList.isEmpty()) {
            onView(withId(R.id.create_filter_empty_list)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.create_filter_number_list))
                .check(matches(isDisplayed()))
                .check(matches(hasItemCount(numberDataUIModelList.size)))
        }
    }*/

    // TODO numberDataUIModelList?
    /*@Test
    fun checkCreateFilterListEmpty() {
        onView(withId(R.id.create_filter_empty_list)).apply {
            if (numberDataUIModelList.isEmpty()) {
                check(matches(isDisplayed()))
                onView(withId(R.id.empty_state_description)).check(matches(withText(EmptyState.EMPTY_STATE_CREATE_FILTER.description())))
                onView(withId(R.id.empty_state_tooltip_arrow)).check(matches(withDrawable(R.drawable.ic_tooltip_arrow)))
                onView(withId(R.id.empty_state_icon)).check(matches(withDrawable(R.drawable.ic_empty_state)))
            } else {
                check(matches(not(isDisplayed())))
            }
        }
    }*/

    @After
    override fun tearDown() {
        super.tearDown()
        fragment = null
        filterWithCountryCodeUIModel = null
    }
}
