package com.tarasovvp.smartblocker.settings.settings_account

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class EmptySettingsAccountInstrumentedTest: BaseSettingsAccountInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}
