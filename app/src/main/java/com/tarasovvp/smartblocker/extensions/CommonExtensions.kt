package com.tarasovvp.smartblocker.extensions

import android.content.Context
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

fun CoroutineScope.launchIO(
    onError: (Throwable, suspend CoroutineScope.() -> Unit) -> Any?,
    block: suspend CoroutineScope.() -> Unit,
): Job =
    launch(CoroutineExceptionHandler { _, exception ->
        onError(exception, block)
    }) {
        withContext(Dispatchers.IO) {
            block()
        }
    }

fun <T> LiveData<T>.safeObserve(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner) {
        it?.let(observer)
    }
}

fun <T> MutableLiveData<T>.safeSingleObserve(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    safeObserve(owner, observer)
    value = null
}

fun String.toDateFromMilliseconds(dateFormat: String): String {
    val millis = this.toMillisecondsFromString()
    val dateFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    return if (millis <= 0) "" else dateFormatter.format(Date(millis))
}

fun String.toMillisecondsFromString(): Long {
    return try {
        this.toLong()
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun Any?.isNull() = this == null

fun Any?.isNotNull() = this != null

fun Boolean?.isTrue() = this == true

fun Boolean?.isNotTrue() = this != true

fun Int?.orZero() = this ?: 0

val String.Companion.EMPTY: String
    get() = ""

fun String?.nameInitial(): String =
    this?.split(Regex(" "))?.take(2)?.filter { it.firstOrNull()?.isLetter().isTrue() }
        ?.mapNotNull { it.firstOrNull() }
        ?.joinToString(String.EMPTY)?.uppercase(Locale.getDefault()).orEmpty()

fun String.flagEmoji(): String {
    if (this.isEmpty()) return String.EMPTY
    val firstLetter = Character.codePointAt(this, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(this, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}

fun String?.digitsTrimmed() = this?.filter { it.isDigit() || it == PLUS_CHAR }.orEmpty()

fun Context.dpToPx(dp: Float): Float {
    return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.spToPx(sp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
}

fun Context.htmlWithImages(htmlText: String): Spanned {
    return htmlText.parseAsHtml(imageGetter = {
        val resourceId = resources.getIdentifier(it, "drawable", packageName)
        val drawable = ContextCompat.getDrawable(this, resourceId)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable
    })
}

fun <T> MutableList<T>.moveToFirst(item: T?): MutableList<T> {
    if (item == null) return this
    val currentIndex = indexOf(item)
    if (currentIndex < 0) return this
    removeAt(currentIndex)
    add(0, item)
    return this
}