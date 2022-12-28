package com.tarasovvp.smartblocker.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.APP_LANG_RU
import com.tarasovvp.smartblocker.constants.Constants.APP_LANG_UK
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import kotlinx.coroutines.*
import java.io.Serializable
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

fun String.flagDrawable(): Int {
    return when(this) {
        APP_LANG_UK -> R.drawable.ic_flag_ua
        APP_LANG_RU -> R.drawable.ic_flag_ru
        else -> R.drawable.ic_flag_us
    }
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
        if (resourceId > 0) {
            val drawable = ContextCompat.getDrawable(this, resourceId)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable
        } else {
            ColorDrawable(Color.TRANSPARENT)
        }
    })
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    SDK_INT >= 33 -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}