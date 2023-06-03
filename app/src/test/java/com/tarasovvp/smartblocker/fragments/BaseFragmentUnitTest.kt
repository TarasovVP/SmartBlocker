package com.tarasovvp.smartblocker.fragments

import android.content.Context
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Ignore

@Ignore
open class BaseFragmentUnitTest {

    protected var navController: TestNavHostController? = null
    protected val targetContext by lazy {  ApplicationProvider.getApplicationContext<Context>() }

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
