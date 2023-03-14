package com.tarasovvp.smartblocker.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.MASK_CHAR
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.constants.Constants.SECOND
import com.tarasovvp.smartblocker.databinding.DialogInfoBinding

private var lastClickTime = 0L

fun View.setSafeOnClickListener(action: () -> Unit) {
    setOnClickListener {
        SystemClock.elapsedRealtime().takeIf { it - lastClickTime > 500L }
            ?.run {
                action()
                lastClickTime = this
            }
    }
}

fun Activity.showMessage(message: String, isError: Boolean) {
    if (isFinishing) return
    val dialogView = DialogInfoBinding.inflate(LayoutInflater.from(this))
    dialogView.dialogInfoIcon.setImageResource(if (isError) R.drawable.ic_result_error else R.drawable.ic_result_success)
    dialogView.dialogInfoDescription.text = message
    val dialog = AlertDialog.Builder(this).setView(dialogView.root).create()
    dialog.show()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window?.attributes?.apply {
        this.width = (Resources.getSystem().displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.attributes = this
    }
    Handler(Looper.getMainLooper()).postDelayed({
        dialog.dismiss()
    }, SECOND * 2)
}

fun <T> ViewGroup.getViewsFromLayout(
    viewType: Class<T>
): ArrayList<T> {
    return this.getViewsFromLayout(ArrayList(), viewType)
}

private fun <T> ViewGroup.getViewsFromLayout(
    views: ArrayList<T>,
    viewType: Class<T>
): ArrayList<T> {
    val childCount = this.childCount
    for (i in 0 until childCount) {
        val view = this.getChildAt(i)
        if (viewType.isInstance(view)) {
            @Suppress("UNCHECKED_CAST")
            val targetView = this.getChildAt(i) as T
            views.add(targetView)
        } else if (view is ViewGroup) {
            view.getViewsFromLayout(views, viewType)
        }
    }
    return views
}

fun EditText?.inputText(): String {
    return this?.text?.toString().orEmpty()
}

@BindingAdapter(value = ["text", "filterToInput"], requireAll = false)
fun EditText.setTextToInput(inputText: String?, filterToInput: Boolean) {
    if (filterToInput) setText(inputText)
}

@BindingAdapter(value = ["imageUrl", "placeHolder"], requireAll = false)
fun ImageView.loadCircleImage(imageUrl: String?, placeHolder: Drawable?) {
    Glide
        .with(this.context)
        .load(imageUrl)
        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placeHolder)
            .error(placeHolder))
        .into(this)
}

@BindingAdapter(value = ["searchText", "mainText"], requireAll = false)
fun TextView.highlightText(searchText: String?, mainText: String?) {
    if (searchText.isNullOrEmpty().not()
        && mainText.isNullOrEmpty().not()
        && mainText.orEmpty().lowercase().contains(searchText.orEmpty().lowercase())
    ) {
        SpannableString(mainText).apply {
            var index: Int =
                mainText.orEmpty().lowercase().indexOf(searchText.orEmpty().lowercase())
            while (index >= 0 && index < mainText?.length.orZero()) {
                val highlightSpan = TextAppearanceSpan(null,
                    Typeface.BOLD,
                    -1,
                    ColorStateList(arrayOf(intArrayOf()),
                        intArrayOf(ContextCompat.getColor(context, R.color.text_color_black))),
                    null)
                setSpan(highlightSpan,
                    index,
                    index + searchText?.length.orZero(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                index = mainText.orEmpty().lowercase()
                    .indexOf(searchText.orEmpty().lowercase(), index + 1)
            }
            text = this
        }
    } else {
        text = mainText
    }
}

fun String?.highlightedSpanned(searchNumberText: String?, countryCode: String?, color: Int): SpannableString {
    val mainText = if (countryCode.isNullOrEmpty().not()) {
        String.format("%s? %s", countryCode, this)
    } else this
    val spannableString = SpannableString(mainText)
    if (countryCode.isNullOrEmpty().not()) {
        spannableString.apply {
            val highlightSpan = TextAppearanceSpan(null,
                Typeface.ITALIC,
                -1,
                ColorStateList(arrayOf(intArrayOf()), intArrayOf(color)),
                null)
            setSpan(highlightSpan,
                0,
                countryCode?.length.orZero() + 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }
    val highlightedTextList: ArrayList<String> = ArrayList()
    if (searchNumberText.isNullOrEmpty().not()
        && this.isNullOrEmpty().not()
        && this.digitsTrimmed().lowercase()
            .contains(searchNumberText.orEmpty().lowercase())
    ) {
        val highlightedText: StringBuilder = StringBuilder()
        var searchIndex = 0
        this?.forEachIndexed { index, char ->
            if (char.isDigit() || char == PLUS_CHAR) {
                if (searchIndex < searchNumberText?.length.orZero() && char == searchNumberText?.get(
                        searchIndex)
                ) {
                    highlightedText.append(char)
                    if (index == this.lastIndex && highlightedText.toString()
                            .digitsTrimmed().length >= searchNumberText.length.orZero()
                    ) {
                        highlightedTextList.add(highlightedText.toString())
                        searchIndex = 0
                        highlightedText.clear()
                    } else {
                        searchIndex++
                    }
                } else {
                    if (highlightedText.toString()
                            .digitsTrimmed().length >= searchNumberText?.length.orZero()
                    ) {
                        highlightedTextList.add(highlightedText.toString())
                    }
                    searchIndex = 0
                    highlightedText.clear()
                    if (char == searchNumberText?.get(searchIndex)) {
                        highlightedText.append(char)
                        searchIndex++
                    }
                }
            } else if (char.isDigit().not() && highlightedText.isNotEmpty()) {
                highlightedText.append(char)
            }
        }
        highlightedTextList.forEach { searchText ->
            spannableString.apply {
                var index: Int =
                    mainText.orEmpty().lowercase().indexOf(searchText.lowercase())
                while (index >= 0 && index < this@highlightedSpanned?.length.orZero()) {
                    val highlightSpan = TextAppearanceSpan(null,
                        Typeface.BOLD,
                        -1,
                        ColorStateList(arrayOf(intArrayOf()), intArrayOf(color)),
                        null)
                    setSpan(highlightSpan,
                        index,
                        index + searchText.length.orZero(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    index = mainText.orEmpty().lowercase()
                        .indexOf(searchText.lowercase(), index + 1)
                }
            }
        }
        return spannableString
    } else {
        return spannableString
    }
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.setupClearButtonWithAction() {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val clearIcon = if (editable.toString().replace(MASK_CHAR.toString(), String.EMPTY)
                    .isNotBlank()
            ) R.drawable.ic_clear else 0
            setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    })

    setOnTouchListener(View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                this.setText(String.EMPTY)
                return@OnTouchListener true
            }
        }
        return@OnTouchListener false
    })
}