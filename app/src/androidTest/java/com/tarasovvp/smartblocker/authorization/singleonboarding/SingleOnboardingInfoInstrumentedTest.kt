package com.tarasovvp.smartblocker.authorization.singleonboarding

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class SingleOnboardingInfoInstrumentedTest : BaseSingleOnboardingInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
