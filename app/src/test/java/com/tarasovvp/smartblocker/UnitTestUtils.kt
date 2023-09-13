package com.tarasovvp.smartblocker

import androidx.annotation.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object UnitTestUtils {

    const val TEST_TOKEN = "testToken"
    const val TEST_NUMBER = "12345678"
    const val TEST_FILTER = "87654321"
    const val TEST_NAME = "testName"
    const val TEST_COUNTRY = "UA"
    const val TEST_COUNTRY_CODE = "+380"
    const val TEST_APP_LANGUAGE = "en"
    const val TEST_APP_THEME = 1
    const val TEST_EMAIL = "test@email.com"
    const val TEST_PASSWORD = "Ua12345!"
    const val TEST_REVIEW = "review"
    const val TEST_USER_ID = "testUserId"
    const val IS_AUTHORISED = "isAuthorised"
    const val IS_GOOGLE_AUTH = "isGoogleAuth"
    const val EMPTY = "Empty"
    const val TEST_ERROR_MESSAGE = "testErrorMessage"

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {},
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)

        try {
            afterObserve.invoke()
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }
        } finally {
            this.removeObserver(observer)
        }
        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}