package com.tarasovvp.smartblocker.number.details.detailsfilter

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class DetailsFilterPermissionInstrumentedTest : BaseDetailsFilterInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
