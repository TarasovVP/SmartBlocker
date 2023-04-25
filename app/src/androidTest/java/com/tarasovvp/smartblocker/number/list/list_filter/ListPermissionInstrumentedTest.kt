package com.tarasovvp.smartblocker.number.list.list_filter

import androidx.navigation.Navigation
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListPermissionFragment
import com.tarasovvp.smartblocker.waitUntilViewIsDisplayed
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
class ListPermissionInstrumentedTest: BaseListFilterInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        filterList = TestUtils.filterWithFilterList().onEach {
            it.filter?.filterType = Constants.PERMISSION
        }
        TestUtils.launchFragmentInHiltContainer<ListPermissionFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listPermissionFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            (this as? ListPermissionFragment)?.apply {
                filterIndexList = filterIndexes ?: arrayListOf()
                viewModel.filterListLiveData.postValue(filterList)
            }
        }
        waitUntilViewIsDisplayed(withText(filterList?.get(0)?.filter?.filter))
    }
}
