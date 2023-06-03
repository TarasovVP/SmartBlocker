package com.tarasovvp.smartblocker.fragments

import android.content.Context
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.rules.TestName

@Ignore
open class BaseFragmentUnitTest {

    protected var navController: TestNavHostController? = null
    protected val targetContext: Context by lazy {  ApplicationProvider.getApplicationContext() }

    @get:Rule
    val name by lazy {  TestName() }

    @Before
    open fun setUp() {
        ApplicationProvider.getApplicationContext<Context>()?.let {
            navController = TestNavHostController(it)
        }
    }

    @After
    open fun tearDown() {
        navController = null
    }
}
