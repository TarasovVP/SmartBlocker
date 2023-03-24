package com.tarasovvp.smartblocker.number.create

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class CreateFilterInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var navController: TestNavHostController? = null

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext?.let {
            navController = TestNavHostController(it)
        }
        launchFragmentInHiltContainer<CreateFilterFragment> (fragmentArgs = bundleOf("filterWithCountryCode" to FilterWithCountryCode(filter = Filter()))) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.createFilterFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    /**
     *
     */
    @Test
    fun checkCreateFilterItemFilter() {
        onView(withId(R.id.create_filter_item_filter)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkCreateFilterInputContainer() {
        onView(withId(R.id.create_filter_input_container)).check(matches(isDisplayed()))

    }

    /**
     *
     */
    @Test
    fun checkCreateFilterCountryCodeSpinner() {
        onView(withId(R.id.create_filter_country_code_spinner)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkCreateFilterCountryCodeValue() {
        onView(withId(R.id.create_filter_country_code_value)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkCreateFilterInput() {
        onView(withId(R.id.create_filter_input)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkCreateFilterSubmit() {
        onView(withId(R.id.create_filter_submit)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkCreateFilterNumberList() {
        //TODO
        //onView(withId(R.id.create_filter_number_list)).check(matches(isDisplayed()))
    }

    /**
     *
     */
    @Test
    fun checkCreateFilterEmptyList() {
        onView(withId(R.id.create_filter_empty_list)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        navController = null
    }
}
