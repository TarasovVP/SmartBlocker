package com.tarasovvp.smartblocker.number.list.list_contact

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class EmptyListContactInstrumentedTest: BaseListContactInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
