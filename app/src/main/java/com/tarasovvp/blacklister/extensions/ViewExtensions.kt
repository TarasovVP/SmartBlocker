package com.tarasovvp.blacklister.extensions

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.isInvisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.databinding.PopUpWindowInfoBinding
import com.tarasovvp.blacklister.databinding.SnackBarInfoBinding
import com.tarasovvp.blacklister.enums.Info
import com.tarasovvp.blacklister.utils.setSafeOnClickListener


fun View.showMessage(message: String, isError: Boolean) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .apply {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).isInvisible = true
            val snackView = SnackBarInfoBinding.inflate(LayoutInflater.from(context))
            snackView.snackBarInfoIcon.setImageResource(if (isError) R.drawable.ic_result_error else R.drawable.ic_result_success)
            snackView.snackBarInfoDescription.text = message
            (view as Snackbar.SnackbarLayout).addView(snackView.root)
            (snackView.root.layoutParams as FrameLayout.LayoutParams).apply {
                gravity = Gravity.CENTER
                width = FrameLayout.LayoutParams.WRAP_CONTENT
                height = FrameLayout.LayoutParams.WRAP_CONTENT
                setMargins(context.dpToPx(16f).toInt(), context.dpToPx(4f).toInt(), context.dpToPx(16f).toInt(), context.dpToPx(4f).toInt())
                view.layoutParams = this
            }
        }.show()
}

fun View.showPopUpWindow(info: Info) {
    val popupView = PopUpWindowInfoBinding.inflate(LayoutInflater.from(context))
    popupView.info = info
    popupView.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val popupWindow = PopupWindow(
        popupView.root,
        (Resources.getSystem().displayMetrics.widthPixels * 0.9).toInt(),
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )
    popupWindow.elevation = 2f
    popupWindow.showAtLocation(this, Gravity.CENTER,0, -(Resources.getSystem().displayMetrics.heightPixels * 0.2).toInt())
    popupView.popUpWindowClose.setSafeOnClickListener {
        popupWindow.dismiss()
    }
}


fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
    this.view.setBackgroundColor(colorInt)
    return this
}

fun <T> ViewGroup.getViewsFromLayout(
    viewType: Class<T>,
): ArrayList<T> {
    return this.getViewsFromLayout(ArrayList(), viewType)
}

private fun <T> ViewGroup.getViewsFromLayout(
    views: ArrayList<T>,
    viewType: Class<T>,
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

@BindingAdapter(value = ["circleImageUrl", "nameInitial"], requireAll = false)
fun ImageView.loadCircleImage(photoUrl: String?, nameInitial: String?) {
    val placeHolder = if (nameInitial.isNullOrEmpty()) ContextCompat.getDrawable(context,
        R.drawable.ic_avatar) else RoundedBitmapDrawableFactory.create(resources,
        nameInitial.let { context.getInitialBitmap(it) }).apply {
        isCircular = true
    }
    Glide
        .with(this.context)
        .load(photoUrl)
        .apply(RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placeHolder)
            .error(placeHolder))
        .into(this)
}

@BindingAdapter("app:tint")
fun ImageView.setImageTint(@ColorInt color: Int) {
    setColorFilter(color)
}

@BindingAdapter(value = ["searchText", "mainText"], requireAll = false)
fun TextView.highlightText(searchText: String?, mainText: String?) {
    if (searchText.isNullOrEmpty().not()
        && mainText.isNullOrEmpty().not()
        && mainText.digitsTrimmed().lowercase().contains(searchText.orEmpty().lowercase())
    ) {
        SpannableString(mainText).apply {
            var index: Int =
                mainText.orEmpty().lowercase().indexOf(searchText.orEmpty().lowercase())
            while (index >= 0 && index < mainText?.length.orZero()) {
                val highlightSpan = TextAppearanceSpan(null,
                    Typeface.BOLD,
                    -1,
                    ColorStateList(arrayOf(intArrayOf()),
                        intArrayOf(ContextCompat.getColor(context, R.color.span_text))),
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

@BindingAdapter(value = ["searchNumberText", "mainNumberText"], requireAll = false)
fun TextView.highlightedText(searchNumberText: String?, mainNumberText: String?) {
    val highlightedTextList: ArrayList<String> = ArrayList()
    if (searchNumberText.isNullOrEmpty().not()
        && mainNumberText.isNullOrEmpty().not()
        && mainNumberText.digitsTrimmed().lowercase()
            .contains(searchNumberText.orEmpty().lowercase())
    ) {
        val highlightedText: StringBuilder = StringBuilder()
        var searchIndex = 0
        mainNumberText?.forEachIndexed { index, char ->
            if (char.isDigit() || char == PLUS_CHAR) {
                if (searchIndex < searchNumberText?.length.orZero() && char == searchNumberText?.get(
                        searchIndex)
                ) {
                    highlightedText.append(char)
                    if (index == mainNumberText.lastIndex && highlightedText.toString()
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
            SpannableString(mainNumberText).apply {
                var index: Int =
                    mainNumberText.orEmpty().lowercase().indexOf(searchText.lowercase())
                while (index >= 0 && index < mainNumberText?.length.orZero()) {
                    val highlightSpan = TextAppearanceSpan(null,
                        Typeface.BOLD,
                        -1,
                        ColorStateList(arrayOf(intArrayOf()),
                            intArrayOf(ContextCompat.getColor(context, R.color.span_text))),
                        null)
                    setSpan(highlightSpan,
                        index,
                        index + searchText.length.orZero(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    index = mainNumberText.orEmpty().lowercase()
                        .indexOf(searchText.lowercase(), index + 1)
                }
                text = this
            }
        }
        if (highlightedTextList.isEmpty()) text = mainNumberText
    } else {
        text = mainNumberText
    }
}