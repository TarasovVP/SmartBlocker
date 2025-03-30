package com.tarasovvp.smartblocker.number.list.listcontact

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class ListContactInstrumentedTest : BaseListContactInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
