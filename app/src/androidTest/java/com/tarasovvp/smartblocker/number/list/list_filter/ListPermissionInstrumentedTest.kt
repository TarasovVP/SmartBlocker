package com.tarasovvp.smartblocker.number.list.list_filter

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class ListPermissionInstrumentedTest: BaseListFilterInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
