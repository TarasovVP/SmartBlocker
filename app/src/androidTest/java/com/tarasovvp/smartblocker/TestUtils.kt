package com.tarasovvp.smartblocker

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.apps.common.testing.accessibility.framework.replacements.Spans
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.IS_INSTRUMENTAL_TEST
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher


object TestUtils {

    const val TEST_EMAIL = "testEmail"
    const val TEST_PASSWORD = "testPassword"
    const val IS_LOG_OUT = "isLogOut"

    inline fun <reified T : Fragment> launchFragmentInHiltContainer(
        fragmentArgs: Bundle? = null,
        @StyleRes themeResId: Int = R.style.Theme_SmartBlocker,
        crossinline action: Fragment.() -> Unit = {},
    ) {
        val startActivityIntent = Intent.makeMainActivity(
            ComponentName(
                ApplicationProvider.getApplicationContext(),
                MainActivity::class.java
            )
        ).putExtra(
            IS_INSTRUMENTAL_TEST,
            true
        ).putExtra(
            "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
            themeResId
        )

        ActivityScenario.launch<MainActivity>(startActivityIntent).onActivity { activity ->
            val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
                Preconditions.checkNotNull(T::class.java.classLoader) as ClassLoader,
                T::class.java.name
            ) as T
            SharedPrefs.init(activity)
            fragment.arguments = fragmentArgs
            activity.supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragment, "")
                .commitNow()

            fragment.action()
        }
    }

    fun withDrawable(@DrawableRes id: Int) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("ImageView with drawable same as drawable with id $id")
        }

        override fun matchesSafely(view: View): Boolean {
            val context = view.context
            val expectedBitmap = context.getDrawable(id)?.toBitmap()
            return if (view is ImageView) view.drawable?.toBitmap()?.sameAs(expectedBitmap).isTrue()
            else if (view is TextView && view.compoundDrawables.any { it.isNotNull() }) view.compoundDrawables[view.compoundDrawables.indexOfFirst { it.isNotNull() }].toBitmap().sameAs(expectedBitmap)
            else false
        }
    }

    fun withBackgroundColor(@ColorInt color: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun matchesSafely(view: View): Boolean {
                val background = view.background
                return checkColor(background, color)
            }

            override fun describeTo(description: Description) {
                description.appendText("View with background color: $color")
            }

            private fun checkColor(drawable: Drawable?, @ColorInt color: Int): Boolean {
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

    fun clickLinkWithText(linkText: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(TextView::class.java))
            }

            override fun getDescription(): String {
                return "click on link with text: $linkText"
            }

            override fun perform(uiController: UiController?, view: View?) {
                if (view is TextView) {
                    val text = view.text.toString()
                    val start = text.indexOf(linkText)
                    val end = start + linkText.length
                    view.movementMethod.onTouchEvent(view, view.text as Spannable,
                        MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN,
                            view.layout.getPrimaryHorizontal(start),
                            view.layout.getLineBaseline(view.layout.getLineForOffset(start)).toFloat(), 0))
                    view.movementMethod.onTouchEvent(view, view.text as Spannable,
                        MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP,
                            view.layout.getPrimaryHorizontal(end),
                            view.layout.getLineBaseline(view.layout.getLineForOffset(end)).toFloat(), 0))
                }
            }
        }
    }
}