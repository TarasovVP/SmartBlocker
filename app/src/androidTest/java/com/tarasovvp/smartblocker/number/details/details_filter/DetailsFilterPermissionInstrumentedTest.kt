package com.tarasovvp.smartblocker.number.details.details_filter

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class DetailsFilterPermissionInstrumentedTest : BaseDetailsFilterInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
