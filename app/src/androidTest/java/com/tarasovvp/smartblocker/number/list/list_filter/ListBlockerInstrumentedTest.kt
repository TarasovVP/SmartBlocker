package com.tarasovvp.smartblocker.number.list.list_filter

import androidx.navigation.Navigation
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.TestUtils.filterWithFilteredNumberUIModelList
import com.tarasovvp.smartblocker.TestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListBlockerFragment
import com.tarasovvp.smartblocker.waitUntilViewIsDisplayed
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
class ListBlockerInstrumentedTest : BaseListFilterInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        filterList =
            filterWithFilteredNumberUIModelList().onEach {
                it.filterType = Constants.BLOCKER
            }
        launchFragmentInHiltContainer<ListBlockerFragment> {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.listPermissionFragment)
            Navigation.setViewNavController(requireView(), navController)
            fragment = this
            (this as? ListBlockerFragment)?.apply {
                filterIndexList = filterIndexes ?: arrayListOf()
                viewModel.filterListLiveData.postValue(filterList)
            }
        }
        waitUntilViewIsDisplayed(withText(filterList?.get(0)?.filter))
    }
}
