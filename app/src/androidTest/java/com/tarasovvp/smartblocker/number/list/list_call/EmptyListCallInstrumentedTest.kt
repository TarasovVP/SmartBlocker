package com.tarasovvp.smartblocker.number.list.list_call

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class EmptyListCallInstrumentedTest: BaseListCallInstrumentedTest(){

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
