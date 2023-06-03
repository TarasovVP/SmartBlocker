package com.tarasovvp.smartblocker.fragments.number

import android.os.Build
import android.text.Spannable
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.UnitTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.fragments.BaseFragmentUnitTest
import com.tarasovvp.smartblocker.presentation.main.number.info.InfoFragment
import com.tarasovvp.smartblocker.presentation.ui_models.InfoData
import com.tarasovvp.smartblocker.utils.extensions.htmlWithImages
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.hamcrest.Matcher
import org.hamcrest.Matchers
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
        launchFragmentInHiltContainer<InfoFragment>(bundleOf("info" to
                InfoData(targetContext.getString(Info.INFO_DETAILS_NUMBER_DATA.title()),
                    targetContext.getString(Info.INFO_DETAILS_NUMBER_DATA.description()))
        )) {
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
        /*assertEquals(R.id.infoFragment, navController?.currentDestination?.id)
        assertEquals(
            InfoData(
                title = targetContext.getString(Info.INFO_BLOCKER_LIST.title()),
                description = targetContext.getString(Info.INFO_BLOCKER_LIST.description())
            ),
            navController?.backStack?.last()?.arguments?.parcelable<InfoData>("info")
        )*/
    }

    private fun clickLinkWithText(linkText: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(isDisplayed(), isAssignableFrom(TextView::class.java))
            }

            override fun getDescription(): String {
                return "click on link with text: $linkText"
            }

            override fun perform(uiController: UiController?, view: View?) {
                if (view is TextView) {
                    val text = view.text.toString()
                    val start = text.indexOf(linkText)
                    val end = start + linkText.length
                    view.movementMethod.onTouchEvent(view, view.text as Spannable,
                        MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN,
                            view.layout.getPrimaryHorizontal(start),
                            view.layout.getLineBaseline(view.layout.getLineForOffset(start)).toFloat(), 0))
                    view.movementMethod.onTouchEvent(view, view.text as Spannable,
                        MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP,
                            view.layout.getPrimaryHorizontal(end),
                            view.layout.getLineBaseline(view.layout.getLineForOffset(end)).toFloat(), 0))
                }
            }
        }
    }
}
