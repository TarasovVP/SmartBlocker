package com.tarasovvp.smartblocker.fragments

import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Ignore
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Ignore
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
class InfoUnitTest {

   /* @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        launchFragmentInContainer<InfoFragment>(bundleOf("info" to InfoData("testTitle", "testDescription")))
    }

    @Test
    fun checkContainer() {
        onView(withId(R.id.container)).check(matches(isDisplayed())).perform(click())
    }*/

}
