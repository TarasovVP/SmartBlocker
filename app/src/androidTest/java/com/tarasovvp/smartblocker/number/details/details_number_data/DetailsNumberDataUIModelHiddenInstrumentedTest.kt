package com.tarasovvp.smartblocker.number.details.details_number_data

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class DetailsNumberDataUIModelHiddenInstrumentedTest: BaseDetailsNumberDataInstrumentedTestUIModel() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
