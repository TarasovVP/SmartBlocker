package com.tarasovvp.smartblocker

import android.content.Context
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.filters.Suppress
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass

@Suppress
open class BaseInstrumentedTest {

    protected var navController: TestNavHostController? = null
    protected val targetContext: Context by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    companion object {
        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            AccessibilityChecks.enable().setRunChecksFromRootView(true)
        }
    }

    @Before
    open fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext?.let {
            navController = TestNavHostController(it)
        }
    }

    @After
    open fun tearDown() {
        navController = null
    }
}
