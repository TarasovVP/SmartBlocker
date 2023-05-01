package com.tarasovvp.smartblocker

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.viewpager2.widget.ViewPager2
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.IN_COMING_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.IS_INSTRUMENTAL_TEST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.MISSED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.OUT_GOING_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.REJECTED_CALL
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher

object TestUtils {

    const val TEST_EMAIL = "testEmail"
    const val TEST_PASSWORD = "testPassword"
    const val IS_LOG_OUT = "isLogOut"
    const val FILTERING_LIST = "filteringList"
    const val FILTER_WITH_COUNTRY_CODE = "filterWithCountryCode"
    const val LIST_EMPTY = "ListEmpty"

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
            )
            SharedPrefs.init(activity)
            SharedPrefs.countryCode = CountryCode()
            fragment.arguments = fragmentArgs
            activity.supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragment, "")
                .commitNow()

            fragment.action()
        }
    }

    fun withDrawable(@DrawableRes id: Int?) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("View with drawable same as drawable with id $id")
        }

        override fun matchesSafely(view: View): Boolean {
            val context = view.context
            val expectedBitmap = id?.let { context.getDrawable(it)?.toBitmap() }

            return if (view is ImageView) view.drawable?.toBitmap()?.sameAs(expectedBitmap).isTrue()
            else if (view is TextView && id.isNull()) view.compoundDrawables.none { it.isNotNull() }
            else if (view is TextView && view.compoundDrawables.any { it.isNotNull() }) view.compoundDrawables[view.compoundDrawables.indexOfFirst { it.isNotNull() }].toBitmap().sameAs(expectedBitmap)
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

    fun atPosition(position: Int, @NonNull itemMatcher: Matcher<View?>): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    fun childOf(parentMatcher: Matcher<View?>?, childPosition: Int): Matcher<View?> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with $childPosition child view of type parentMatcher")
            }

            override fun matchesSafely(view: View): Boolean {
                if (view.parent !is ViewGroup) {
                    return parentMatcher?.matches(view.parent).isTrue()
                }
                val group = view.parent as ViewGroup
                return parentMatcher?.matches(view.parent).isTrue() && group.getChildAt(
                    childPosition) == view
            }
        }
    }

    fun hasItemCount(expectedCount: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("has $expectedCount items")
            }

            override fun matchesSafely(view: View?): Boolean {
                return when(view) {
                    is RecyclerView -> view.adapter?.itemCount == expectedCount
                    is ViewPager2 -> view.adapter?.itemCount == expectedCount
                    else -> false
                }
            }
        }
    }

    fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

    fun filterWithFilterList() = arrayListOf(
            FilterWithCountryCode(Filter(filter = "+380502711344", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal).apply { filteredContacts = 3
                created = 1681315250919}, countryCode = CountryCode("UA")),
            FilterWithCountryCode(Filter(filter = "123", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal).apply {
                filteredContacts = 1
                filteredCalls = 5
                created = 1681314350919}, countryCode = CountryCode("UA")),
            FilterWithCountryCode(Filter(filter = "1234", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal).apply { created = 1681314260919 }, countryCode = CountryCode("UA")),
            FilterWithCountryCode(Filter(filter = "12345", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal).apply { created = 1681354250919 }, countryCode = CountryCode("UA")),
            FilterWithCountryCode(Filter(filter = "123456", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal).apply { created = 1681314850919 }, countryCode = CountryCode("UA")),
            FilterWithCountryCode(Filter(filter = "1234567", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal).apply { created = 1681314251219 }, countryCode = CountryCode("UA"))
    )

    fun callWithFilterList() = listOf(
        CallWithFilter(LogCall(1).apply { callName = "A Name"
            number = "+380502711344"
            type = IN_COMING_CALL
            callDate = "1678603872094"},
            FilterWithCountryCode(Filter(filter = "+380502711344", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal), countryCode = CountryCode("UA"))),
        CallWithFilter(FilteredCall(2).apply { callName = "a Name"
            number = "12345"
            type = BLOCKED_CALL
            callDate = "1678603872094"
            isFilteredCall = true
            filteredNumber = "12345"
            conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal},
            FilterWithCountryCode(Filter(filter = "123", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal), countryCode = CountryCode("UA"))),
        CallWithFilter(FilteredCall(3).apply {  callName = "B Name"
            number = "12345"
            type = MISSED_CALL
            callDate = "1611995776162" },
            FilterWithCountryCode(Filter(filter = "123", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal), countryCode = CountryCode("UA"))),
        CallWithFilter(LogCall(4).apply {  callName = String.EMPTY
            number = "12345"
            type = REJECTED_CALL
            callDate = "1612258705769"},
            FilterWithCountryCode(Filter(filter = "12345", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal), countryCode = CountryCode("UA"))),
        CallWithFilter(FilteredCall(5).apply {  callName = "C Name"
            number = "12345"
            type = OUT_GOING_CALL
            callDate = "1612525268071"},
            FilterWithCountryCode(Filter(filter = "123", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal), countryCode = CountryCode("UA"))),
        CallWithFilter(FilteredCall(6).apply { callName = "D Name"
            number = "12345"
            type = BLOCKED_CALL
            callDate = "1615110430251"},
            FilterWithCountryCode(Filter(filter = "123", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal), countryCode = CountryCode("UA"))),
        CallWithFilter(LogCall(7).apply { callName = "Y Name"
            number = "12345"
            type = IN_COMING_CALL
            callDate = "1619427342586"}, null)
    )

    fun contactWithFilterList() = listOf(
        ContactWithFilter(Contact("1", name = "A Name", number = "+380502711344", filter = "+380502711344"),
            FilterWithCountryCode(Filter(filter = "+380502711344", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal), countryCode = CountryCode("UA"))),
        ContactWithFilter(Contact("2", name = "a Name", number = "12345", filter = "123"),
            FilterWithCountryCode(Filter(filter = "123", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal), countryCode = CountryCode("UA"))),
        ContactWithFilter(Contact("3", name = "B Name", number = "12345", filter = "123"),
            FilterWithCountryCode(Filter(filter = "123", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal), countryCode = CountryCode("UA"))),
        ContactWithFilter(Contact("4", name = "B Name", number = "12345"),
            FilterWithCountryCode(Filter(filter = "12345", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal), countryCode = CountryCode("UA"))),
        ContactWithFilter(Contact("5", name = "C Name", number = "12345", filter = "123"),
            FilterWithCountryCode(Filter(filter = "123", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal), countryCode = CountryCode("UA"))),
        ContactWithFilter(Contact("6", name = " D Name", number = "12345"),
            FilterWithCountryCode(Filter(filter = "123", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal), countryCode = CountryCode("UA"))),
        ContactWithFilter(Contact("7", name = "Y Name", number = "12345"), null)
    )

    fun numberDataList() = arrayListOf(CallWithFilter(LogCall(1).apply { callName = "A Name"
        number = "+380502711344"
        type = IN_COMING_CALL
        callDate = "1678603872094"},
        FilterWithCountryCode(Filter(filter = "+380502711344", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal), countryCode = CountryCode("UA"))),
        ContactWithFilter(Contact("1", name = "A Name", number = "+380502711344", filter = "+380502711344"),
            FilterWithCountryCode(Filter(filter = "+380502711344", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal), countryCode = CountryCode("UA"))),
        ContactWithFilter(Contact("2", name = "a Name", number = "12345"),
            FilterWithCountryCode(Filter(filter = "123", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal), countryCode = CountryCode("UA"))))

    fun filteredCallList() = arrayListOf<NumberData>(CallWithFilter(FilteredCall(5).apply {  callName = "C Name"
        number = "12345"
        type = OUT_GOING_CALL
        callDate = "1612525268071"},
        FilterWithCountryCode(Filter(filter = "123", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal), countryCode = CountryCode("UA"))),
        CallWithFilter(FilteredCall(6).apply { callName = "D Name"
            number = "12345"
            type = BLOCKED_CALL
            callDate = "1615110430251"},
            FilterWithCountryCode(Filter(filter = "123", filterType = PERMISSION, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal), countryCode = CountryCode("UA"))),
        CallWithFilter(FilteredCall(2).apply { callName = "a Name"
            number = "12345"
            type = BLOCKED_CALL
            callDate = "1678603872094"
            isFilteredCall = true
            filteredNumber = "12345"
            conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal},
            FilterWithCountryCode(Filter(filter = "123", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal), countryCode = CountryCode("UA"))))

    fun filterList() = arrayListOf<NumberData>(FilterWithCountryCode(Filter(filter = "+380502711344", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal).apply { filteredContacts = 3
        created = 1681315250919}, countryCode = CountryCode("UA")),
        FilterWithCountryCode(Filter(filter = "123", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_START.ordinal).apply {
            filteredContacts = 1
            filteredCalls = 5
            created = 1681314350919}, countryCode = CountryCode("UA")),
        FilterWithCountryCode(Filter(filter = "1234", filterType = BLOCKER, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal).apply { created = 1681314260919 }, countryCode = CountryCode("UA")))

    fun filterWithCountryCode() = FilterWithCountryCode(filter = Filter(filter = "123",
        conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
        filterType = BLOCKER ).apply {
        filteredContacts = 12
        filteredCalls = 3
        created = 1681314350919
    }, countryCode = CountryCode(country = "UA"))
}