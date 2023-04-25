package com.tarasovvp.smartblocker.dialogs

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class ContactNumberDataFilteringInstrumentedTest: BaseNumberDataFilteringInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
