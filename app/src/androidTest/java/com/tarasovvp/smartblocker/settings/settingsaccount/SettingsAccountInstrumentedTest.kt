package com.tarasovvp.smartblocker.settings.settingsaccount

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class SettingsAccountInstrumentedTest : BaseSettingsAccountInstrumentedTest() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
