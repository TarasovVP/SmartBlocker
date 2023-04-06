package com.tarasovvp.smartblocker.number.list.list_contact

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
class ListContactInstrumentedTest: BaseListContactInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        contactWithFilterList = listOf()
        super.setUp()
    }
}
