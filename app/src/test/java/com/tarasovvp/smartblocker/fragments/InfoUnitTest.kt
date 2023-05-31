package com.tarasovvp.smartblocker.fragments

import android.content.Context
import android.os.Build
import androidx.core.os.bundleOf
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tarasovvp.smartblocker.UnitTestUtils.launchFragmentInHiltContainer
import com.tarasovvp.smartblocker.presentation.main.number.info.InfoFragment
import com.tarasovvp.smartblocker.presentation.ui_models.InfoData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.utils.extensions.htmlWithImages
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
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
class InfoUnitTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val targetContext = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        launchFragmentInHiltContainer<InfoFragment>(bundleOf("info" to
                InfoData(targetContext.getString(Info.INFO_DETAILS_NUMBER_DATA.title()),
                    targetContext.getString(Info.INFO_DETAILS_NUMBER_DATA.description()))
        ))
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed()))
            onView(withId(R.id.info_web_view))
                .check(matches(isDisplayed()))
                .check(matches(withText(targetContext.htmlWithImages(targetContext.getString(Info.INFO_DETAILS_NUMBER_DATA.description())).toString())))
    }

}
