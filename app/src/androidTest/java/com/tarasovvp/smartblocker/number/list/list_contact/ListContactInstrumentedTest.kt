package com.tarasovvp.smartblocker.number.list.list_contact

import com.tarasovvp.smartblocker.TestUtils
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
        contactWithFilterList = TestUtils.contactWithFilterList()
        super.setUp()
    }
}
