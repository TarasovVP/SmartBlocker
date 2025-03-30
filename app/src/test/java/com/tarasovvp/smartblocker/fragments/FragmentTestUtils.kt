package com.tarasovvp.smartblocker.fragments

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
import androidx.test.espresso.matcher.ViewMatchers
import androidx.viewpager2.widget.ViewPager2
import com.tarasovvp.smartblocker.EmptyActivity
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.uimodels.CallWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.NumberDataUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

object FragmentTestUtils {
    const val TEST_NUMBER = "12345678"
    const val TEST_FILTER = "87654321"
    const val TEST_COUNTRY = "UA"
    const val TEST_EMAIL = "test@email.com"
    const val TEST_PASSWORD = "Ua12345!"
    const val FILTER_WITH_COUNTRY_CODE = "filterWithCountryCodeUIModel"
    const val FILTER_WITH_FILTERED_NUMBER = "filterWithFilteredNumberUIModel"

    inline fun <reified T : Fragment> launchFragmentInHiltContainer(
        fragmentArgs: Bundle? = null,
        @StyleRes themeResId: Int = R.style.Theme_SmartBlocker,
        crossinline action: Fragment.() -> Unit = {},
    ) {
        val startActivityIntent =
            Intent.makeMainActivity(
                ComponentName(
                    ApplicationProvider.getApplicationContext(),
                    EmptyActivity::class.java,
                ),
            ).putExtra(
                "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
                themeResId,
            )

        ActivityScenario.launch<EmptyActivity>(startActivityIntent).onActivity { activity ->
            val fragment: Fragment =
                activity.supportFragmentManager.fragmentFactory.instantiate(
                    Preconditions.checkNotNull(T::class.java.classLoader) as ClassLoader,
                    T::class.java.name,
                )
            fragment.arguments = fragmentArgs
            activity.supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragment, "")
                .commitNow()

            fragment.action()
        }
    }

    fun withDrawable(
        @DrawableRes id: Int?,
        position: Int? = null,
    ) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("View with drawable same as drawable with id $id")
        }

        override fun matchesSafely(view: View): Boolean {
            val context = view.context
            val expectedBitmap = id?.let { context.getDrawable(it)?.toBitmap() }

            return if (view is ImageView) {
                view.drawable?.toBitmap()?.sameAs(expectedBitmap)
                    .isTrue()
            } else if (view is TextView && id.isNull()) {
                view.compoundDrawables.none { it.isNotNull() }
            } else if (view is TextView && view.compoundDrawables.any { it.isNotNull() }) {
                view.compoundDrawables[
                    position
                        ?: view.compoundDrawables.indexOfFirst { it.isNotNull() },
                ].toBitmap()
                    .sameAs(expectedBitmap)
            } else {
                false
            }
        }
    }

    fun withBitmap(bitmap: Bitmap?) =
        object : TypeSafeMatcher<View>() {
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

            private fun checkColor(
                drawable: Drawable?,
                color: Int,
            ): Boolean {
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

    fun withBackgroundTint(
        @ColorInt color: Int,
    ): Matcher<View> {
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

            override fun perform(
                uiController: UiController,
                v: View?,
            ) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

    fun atPosition(
        position: Int,
        @NonNull itemMatcher: Matcher<View?>,
    ): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder =
                    view.findViewHolderForAdapterPosition(position)
                        ?: return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    fun hasItemCount(expectedCount: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("has $expectedCount items")
            }

            override fun matchesSafely(view: View?): Boolean {
                return when (view) {
                    is RecyclerView -> view.adapter?.itemCount == expectedCount
                    is ViewPager2 -> view.adapter?.itemCount == expectedCount
                    else -> false
                }
            }
        }
    }

    fun clickLinkWithText(linkText: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.isAssignableFrom(TextView::class.java),
                )
            }

            override fun getDescription(): String {
                return "click on link with text: $linkText"
            }

            override fun perform(
                uiController: UiController?,
                view: View?,
            ) {
                if (view is TextView) {
                    val text = view.text.toString()
                    val start = text.indexOf(linkText)
                    val end = start + linkText.length
                    view.movementMethod.onTouchEvent(
                        view,
                        view.text as Spannable,
                        MotionEvent.obtain(
                            0,
                            0,
                            MotionEvent.ACTION_DOWN,
                            view.layout.getPrimaryHorizontal(start),
                            view.layout.getLineBaseline(view.layout.getLineForOffset(start))
                                .toFloat(),
                            0,
                        ),
                    )
                    view.movementMethod.onTouchEvent(
                        view,
                        view.text as Spannable,
                        MotionEvent.obtain(
                            0,
                            0,
                            MotionEvent.ACTION_UP,
                            view.layout.getPrimaryHorizontal(end),
                            view.layout.getLineBaseline(view.layout.getLineForOffset(end))
                                .toFloat(),
                            0,
                        ),
                    )
                }
            }
        }
    }

    fun filterWithFilteredNumberUIModelList() =
        arrayListOf(
            FilterWithFilteredNumberUIModel(
                filter = "+380502711344",
                filterType = Constants.BLOCKER,
                conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                created = 1681315250919,
                filteredContacts = 1,
            ),
            FilterWithFilteredNumberUIModel(
                filter = "123",
                filterType = Constants.BLOCKER,
                conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                created = 1681314350919,
                filteredContacts = 1,
            ),
            FilterWithFilteredNumberUIModel(
                filter = "1234",
                filterType = Constants.BLOCKER,
                conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                created = 1681314260919,
            ),
            FilterWithFilteredNumberUIModel(
                filter = "12345",
                filterType = Constants.PERMISSION,
                conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                created = 1681354250919,
                filteredContacts = 0,
            ),
            FilterWithFilteredNumberUIModel(
                filter = "123456",
                filterType = Constants.PERMISSION,
                conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                created = 1681314850919,
                filteredContacts = 0,
            ),
            FilterWithFilteredNumberUIModel(
                filter = "1234567",
                filterType = Constants.PERMISSION,
                conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                created = 1681314251219,
            ),
        )

    fun callWithFilterUIModelList() =
        listOf(
            CallWithFilterUIModel(
                callId = 1,
                callName = "A Name",
                number = "+380502711344",
                type = Constants.IN_COMING_CALL,
                callDate = "1678603872094",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "+380502711344",
                        filterType = Constants.BLOCKER,
                        conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                    ),
            ),
            CallWithFilterUIModel(
                callId = 2,
                callName = "a Name",
                number = "12345",
                type = Constants.BLOCKED_CALL,
                callDate = "1678603872094",
                isFilteredCall = true,
                filteredNumber = "12345",
                conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.BLOCKER,
                        conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                    ),
            ),
            CallWithFilterUIModel(
                callId = 3,
                callName = "B Name",
                number = "12345",
                type = Constants.MISSED_CALL,
                callDate = "1611995776162",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.BLOCKER,
                        conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                        filteredContacts = 11,
                        filteredCalls = 52,
                    ),
            ),
            CallWithFilterUIModel(
                callId = 4,
                callName = String.EMPTY,
                number = "12345",
                type = Constants.REJECTED_CALL,
                callDate = "1612258705769",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "12345",
                        filterType = Constants.PERMISSION,
                        conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                        filteredContacts = 11,
                        filteredCalls = 52,
                    ),
            ),
            CallWithFilterUIModel(
                callId = 5,
                callName = "C Name",
                number = "12345",
                type = "2",
                callDate = "1612525268071",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.PERMISSION,
                        conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                    ),
            ),
            CallWithFilterUIModel(
                callId = 6,
                callName = "D Name",
                number = "12345",
                type = Constants.BLOCKED_CALL,
                callDate = "1615110430251",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.PERMISSION,
                        conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                    ),
            ),
            CallWithFilterUIModel(
                callId = 7,
                callName = "Y Name",
                number = "12345",
                type = Constants.IN_COMING_CALL,
                callDate = "1619427342586",
                filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(),
            ),
        )

    fun contactWithFilterUIModelList() =
        listOf(
            ContactWithFilterUIModel(
                contactId = "1",
                contactName = "A Name",
                number = "+380502711344",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "+380502711344",
                        filterType = Constants.BLOCKER,
                        conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                    ),
            ),
            ContactWithFilterUIModel(
                contactId = "2",
                contactName = "a Name",
                number = "12345",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.PERMISSION,
                        conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                    ),
            ),
            ContactWithFilterUIModel(
                contactId = "3",
                contactName = "B Name",
                number = "12345",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.BLOCKER,
                        conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                    ),
            ),
            ContactWithFilterUIModel(
                contactId = "4",
                contactName = "B Name",
                number = "12345",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "12345",
                        filterType = Constants.PERMISSION,
                        conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                    ),
            ),
            ContactWithFilterUIModel(
                contactId = "5",
                contactName = "C Name",
                number = "12345",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.PERMISSION,
                        conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                    ),
            ),
            ContactWithFilterUIModel(
                contactId = "6",
                contactName = " D Name",
                number = "12345",
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.PERMISSION,
                        conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                    ),
            ),
            ContactWithFilterUIModel(
                contactId = "7",
                contactName = "Y Name",
                number = "12345",
                filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(),
            ),
        )

    fun numberDataWithFilteredCallUIModelList() =
        arrayListOf<NumberDataUIModel>(
            CallWithFilterUIModel(
                callId = 5,
                callName = "C Name",
                number = "12345",
                type = "2",
                callDate = "1612525268071",
                isFilteredCall = true,
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.PERMISSION,
                        conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                    ),
            ),
            CallWithFilterUIModel(
                callId = 6,
                callName = "D Name",
                number = "12345",
                type = Constants.BLOCKED_CALL,
                callDate = "1615110430251",
                isFilteredCall = true,
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.PERMISSION,
                        conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                    ),
            ),
            CallWithFilterUIModel(
                callId = 2,
                callName = "a Name",
                number = "12345",
                type = Constants.BLOCKED_CALL,
                callDate = "1678603872094",
                isFilteredCall = true,
                filteredNumber = "12345",
                conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter = "123",
                        filterType = Constants.BLOCKER,
                        conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                    ),
            ),
        )

    fun numberDataWithFilterWithFilteredNumberUIModelList() =
        arrayListOf<NumberDataUIModel>(
            FilterWithFilteredNumberUIModel(
                filter = "+380502711344",
                filterType = Constants.BLOCKER,
                conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                created = 1681315250919,
                filteredContacts = 3,
            ),
            FilterWithFilteredNumberUIModel(
                filter = "123",
                filterType = Constants.BLOCKER,
                conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                created = 1681314350919,
            ),
            FilterWithFilteredNumberUIModel(
                filter = "1234",
                filterType = Constants.BLOCKER,
                conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                created = 1681314260919,
            ),
        )
}
