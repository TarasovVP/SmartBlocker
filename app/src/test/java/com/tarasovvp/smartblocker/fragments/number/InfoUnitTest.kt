package com.tarasovvp.smartblocker.fragments.number

import android.os.Build
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.clickLinkWithText
import com.tarasovvp.smartblocker.fragments.FragmentTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.number.info.InfoFragment
import com.tarasovvp.smartblocker.utils.extensions.htmlWithImages
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.O_MR1],
    application = HiltTestApplication::class)
class InfoUnitTest: BaseFragmentUnitTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        super.setUp()
        launchFragmentInHiltContainer<InfoFragment>(bundleOf("info" to Info.INFO_DETAILS_NUMBER_DATA)) {
            navController?.setGraph(R.navigation.navigation)
            navController?.setCurrentDestination(R.id.infoFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun checkContainer() {
        targetContext.apply {
            onView(withId(R.id.info_web_view))
                .check(matches(isDisplayed()))
                .check(matches(withText(htmlWithImages(getString(Info.INFO_DETAILS_NUMBER_DATA.description())).toString())))
                .perform(clickLinkWithText("Номер"))
        }
        assertEquals(R.id.infoFragment, navController?.currentDestination?.id)
        /*assertEquals(
            InfoData(
                title = targetContext.getString(Info.INFO_DETAILS_NUMBER_DATA.title()),
                description = targetContext.getString(Info.INFO_DETAILS_NUMBER_DATA.description())
            ),
            navController?.backStack?.last()?.arguments?.parcelable<InfoData>("info")
        )*/
    }
}
