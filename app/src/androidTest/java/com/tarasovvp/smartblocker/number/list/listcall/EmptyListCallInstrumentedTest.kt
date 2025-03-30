package com.tarasovvp.smartblocker.number.list.listcall

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class EmptyListCallInstrumentedTest : BaseListCallInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
