package com.tarasovvp.smartblocker.number.create

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class CreateFilterConditionFullInstrumentedTest: BaseCreateFilterInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
