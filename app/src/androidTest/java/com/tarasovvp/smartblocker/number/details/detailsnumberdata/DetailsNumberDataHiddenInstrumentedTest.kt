package com.tarasovvp.smartblocker.number.details.detailsnumberdata

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class DetailsNumberDataHiddenInstrumentedTest : BaseDetailsNumberDataInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
