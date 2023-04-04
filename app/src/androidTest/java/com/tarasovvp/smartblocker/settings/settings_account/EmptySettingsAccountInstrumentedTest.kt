package com.tarasovvp.smartblocker.settings.settings_account

import com.tarasovvp.smartblocker.SmartBlockerApp
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
class EmptySettingsAccountInstrumentedTest: BaseSettingsAccountInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    override fun setUp() {
        SmartBlockerApp.instance = SmartBlockerApp().apply {
            auth = null
        }
        super.setUp()
    }
}
