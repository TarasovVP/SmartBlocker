/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.blacklister

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test


class BottomNavigationTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun bottomNavView_clickOnAllItems() {

        assertStartScreen()

        moveToFirstScreen()

        assertFirstScreen()

        openThirdScreen()

        assertThirdScreen()

        openSecondScreen()

        assertSecondScreen()

        pressBack()

        assertStartScreen()

        moveToFirstScreen()

        openForthScreen()

        assertForthScreen()
    }

    @Test
    fun bottomNavView_backGoesToFirstItem() {

        moveToFirstScreen()

        pressBack()

        assertStartScreen()
    }

    @Test(expected = NoActivityResumedException::class)
    fun bottomNavView_backfromFirstItemExits() {

        assertStartScreen()

        pressBack()

        fail()
    }

    @Test
    fun bottomNavView_backstackMaintained() {

        moveToFirstScreen()

        openThirdScreen()

        openFirstScreen()

        assertFirstScreen()

        pressBack()

        assertStartScreen()

        pressBackUnconditionally()

        assertTrue(activityTestRule.activity.isDestroyed)
    }

    private fun assertStartScreen() {
        onView(withText(R.string.login))
            .check(matches(isDisplayed()))
    }

    private fun moveToFirstScreen() {
        onView(withText(R.string.next))
            .perform(click())
    }

    private fun openFirstScreen() {
        onView(allOf(withContentDescription(R.string.log_list), isDisplayed()))
            .perform(click())
    }

    private fun assertFirstScreen() {
        onView(withText(R.string.log_list_))
            .check(matches(isDisplayed()))
    }

    private fun openSecondScreen() {
        onView(allOf(withContentDescription(R.string.contact_list), isDisplayed()))
            .perform(click())
    }

    private fun assertSecondScreen() {
        onView(withText(R.string.contact_list_))
            .check(matches(isDisplayed()))
    }

    private fun openThirdScreen() {
        onView(allOf(withContentDescription(R.string.number_list), isDisplayed()))
            .perform(click())
    }

    private fun assertThirdScreen() {
        onView(withText(R.string.number_list_))
            .check(matches(isDisplayed()))
    }

    private fun openForthScreen() {
        onView(allOf(withContentDescription(R.string.settings), isDisplayed()))
            .perform(click())
    }

    private fun assertForthScreen() {
        onView(withText(R.string.settings_))
            .check(matches(isDisplayed()))
    }
}
