package com.tarasovvp.smartblocker.extensions

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.TextAppearanceSpan
import android.view.*
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
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.HASH_CHAR
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.databinding.PopUpWindowInfoBinding
import com.tarasovvp.smartblocker.databinding.SnackBarInfoBinding
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener


fun View.showMessage(message: String, isError: Boolean) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .apply {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).isInvisible =
                true
            val snackView = SnackBarInfoBinding.inflate(LayoutInflater.from(context))
            snackView.snackBarInfoIcon.setImageResource(if (isError) R.drawable.ic_result_error else R.drawable.ic_result_success)
            snackView.snackBarInfoDescription.text = message
            (view as Snackbar.SnackbarLayout).addView(snackView.root)
            (snackView.root.layoutParams as FrameLayout.LayoutParams).apply {
                gravity = Gravity.CENTER
                width = FrameLayout.LayoutParams.MATCH_PARENT
                height = FrameLayout.LayoutParams.WRAP_CONTENT
                setMargins(context.dpToPx(16f).toInt(),
                    context.dpToPx(4f).toInt(),
                    context.dpToPx(16f).toInt(),
                    context.dpToPx(4f).toInt())
                view.layoutParams = this
            }
        }.show()
}

fun View.showPopUpWindow(info: Info) {
    val popupView = PopUpWindowInfoBinding.inflate(LayoutInflater.from(context))
    info.descriptionResource
    popupView.info = info
    popupView.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val popupWindow = PopupWindow(
        popupView.root,
        (Resources.getSystem().displayMetrics.widthPixels * 0.9).toInt(),
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )
    popupWindow.elevation = 2f
    popupWindow.showAtLocation(this,
        Gravity.CENTER,
        0,
        -(Resources.getSystem().displayMetrics.heightPixels * 0.05).toInt())
    popupView.popUpWindowClose.setSafeOnClickListener {
        popupWindow.dismiss()
    }
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

fun String?.highlightedSpanned(searchNumberText: String?, countryCode: String?): SpannableString {
    val mainText = if (countryCode.isNullOrEmpty().not()) {
        String.format("%s? %s", countryCode, this)
    } else this
    val spannableString = SpannableString(mainText)
    if (countryCode.isNullOrEmpty().not()) {
        spannableString.apply {
            val highlightSpan = TextAppearanceSpan(null,
                Typeface.ITALIC,
                -1,
                ColorStateList(arrayOf(intArrayOf()),
                    intArrayOf(Color.LTGRAY)),
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
                        ColorStateList(arrayOf(intArrayOf()),
                            intArrayOf(Color.BLUE)),
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
            val clearIcon = if (editable.toString().replace(HASH_CHAR.toString(), String.EMPTY)
                    .isNotBlank()
            ) R.drawable.ic_close else 0
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