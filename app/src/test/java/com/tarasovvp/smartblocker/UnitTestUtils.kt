package com.tarasovvp.smartblocker

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
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
    const val IS_LOG_OUT = "isLogOut"

    inline fun <reified T : Fragment> launchFragmentInHiltContainer(
        fragmentArgs: Bundle? = null,
        @StyleRes themeResId: Int = R.style.Theme_SmartBlocker,
        crossinline action: Fragment.() -> Unit = {},
    ) {
        val startActivityIntent = Intent.makeMainActivity(
            ComponentName(
                ApplicationProvider.getApplicationContext(),
                EmptyActivity::class.java
            )
        ).putExtra(
            "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
            themeResId
        )

        ActivityScenario.launch<EmptyActivity>(startActivityIntent).onActivity { activity ->
            val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
                Preconditions.checkNotNull(T::class.java.classLoader) as ClassLoader,
                T::class.java.name
            )
            fragment.arguments = fragmentArgs
            activity.supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragment, "")
                .commitNow()

            fragment.action()
        }
    }

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

    fun withDrawable(@DrawableRes id: Int?, position: Int? = null) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("View with drawable same as drawable with id $id")
        }

        override fun matchesSafely(view: View): Boolean {
            val context = view.context
            val expectedBitmap = id?.let { context.getDrawable(it)?.toBitmap() }

            return if (view is ImageView) view.drawable?.toBitmap()?.sameAs(expectedBitmap).isTrue()
            else if (view is TextView && id.isNull()) view.compoundDrawables.none { it.isNotNull() }
            else if (view is TextView && view.compoundDrawables.any { it.isNotNull() }) view.compoundDrawables[position ?: view.compoundDrawables.indexOfFirst { it.isNotNull() }].toBitmap().sameAs(expectedBitmap)
            else false
        }
    }

    fun withBitmap(bitmap: Bitmap?) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("ImageView with bitmap same as $bitmap")
        }

        override fun matchesSafely(view: View): Boolean {
            return (view as ImageView).drawable?.toBitmap()?.sameAs(bitmap).isTrue()
        }
    }

    fun withBackgroundColor(color: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun matchesSafely(view: View): Boolean {
                val background = view.background
                return checkColor(background, color)
            }

            override fun describeTo(description: Description) {
                description.appendText("View with background color: $color")
            }

            private fun checkColor(drawable: Drawable?, color: Int): Boolean {
                return when (drawable) {
                    is ColorDrawable -> drawable.color == color
                    is GradientDrawable -> {
                        val colors = drawable.colors
                        colors != null && colors.size == 1 && colors[0] == color
                    }
                    else -> false
                }
            }
        }
    }

    fun withBackgroundTint(@ColorInt color: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun matchesSafely(view: View): Boolean {
                val backgroundTintList = view.backgroundTintList
                return backgroundTintList != null && backgroundTintList.defaultColor == color
            }

            override fun describeTo(description: Description) {
                description.appendText("View with background tint color: $color")
            }
        }
    }

    fun withTextColor(expectedId: Int): Matcher<View?> {
        return object : BoundedMatcher<View?, TextView>(TextView::class.java) {
            override fun matchesSafely(textView: TextView): Boolean {
                val colorId = ContextCompat.getColor(textView.context, expectedId)
                return textView.currentTextColor == colorId
            }

            override fun describeTo(description: Description) {
                description.appendText("with text color: ")
                description.appendValue(expectedId)
            }
        }
    }

    fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}