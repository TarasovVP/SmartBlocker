package com.tarasovvp.smartblocker.authorization.single_onboarding

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class SingleOnboardingIntroInstrumentedTest : BaseSingleOnboardingInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
