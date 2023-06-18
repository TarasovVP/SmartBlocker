package com.tarasovvp.smartblocker.utils.extensions

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.tarasovvp.smartblocker.EmptyActivity
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogInfoBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.MASK_CHAR
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SECOND
import dagger.hilt.android.internal.managers.ViewComponentManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var lastClickTime = 0L

fun View.setSafeOnClickListener(action: () -> Unit) {
    setOnClickListener {
        if ((context as? ViewComponentManager.FragmentContextWrapper)?.baseContext is EmptyActivity) {
            action.invoke()
        } else {
            System.currentTimeMillis().takeIf { it - lastClickTime > 500L }
                ?.run {
                    action()
                    lastClickTime = this
                }
        }
    }
}

fun AppCompatActivity.showMessage(message: String, isError: Boolean) {
    if (window.isNull() || isFinishing) return
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
    lifecycleScope.launch {
        delay(SECOND * 2)
        dialog.dismiss()
    }
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
    if (searchText.isNullOrEmpty().not() && mainText.isNullOrEmpty().not() && mainText.orEmpty().lowercase().contains(searchText.orEmpty().lowercase())) {
        SpannableString(mainText).apply {
            var index: Int = mainText.orEmpty().lowercase().indexOf(searchText.orEmpty().lowercase())
            while (index >= 0 && index < mainText?.length.orZero()) {
                val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, ColorStateList(arrayOf(intArrayOf()), intArrayOf(ContextCompat.getColor(context, R.color.text_color_black))), null)
                setSpan(highlightSpan, index, index + searchText?.length.orZero(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                index = mainText.orEmpty().lowercase().indexOf(searchText.orEmpty().lowercase(), index + 1)
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
            val highlightSpan = TextAppearanceSpan(null, Typeface.ITALIC, -1, ColorStateList(arrayOf(intArrayOf()), intArrayOf(color)), null)
            setSpan(highlightSpan, 0, countryCode?.length.orZero() + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }
    return if (searchNumberText.isNullOrEmpty().not() && this.isNullOrEmpty().not() && this.digitsTrimmed().lowercase().contains(searchNumberText.orEmpty().lowercase())) {
        val firstIndex = mainText.getFirstIndex(searchNumberText)
        val lastIndex = mainText?.substring(firstIndex).orEmpty().getLastIndex(searchNumberText) + firstIndex + 1
        if (firstIndex in 0 until lastIndex) {
            spannableString.apply {
                val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, ColorStateList(arrayOf(intArrayOf()), intArrayOf(color)), null)
                setSpan(highlightSpan, firstIndex, lastIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
        spannableString
    } else {
        spannableString
    }
}

private fun String?.getFirstIndex(searchText: String?): Int {
    this?.forEachIndexed { index, _ ->
        if (substring(index).digitsTrimmed().indexOf(searchText.orEmpty()) == 0) {
            return index
        }
    }
    return -1
}

private fun String?.getLastIndex(searchText: String?): Int {
    val searchedText = StringBuilder()
    this?.forEachIndexed { index, char ->
        if (char.isDigit() || char == PLUS_CHAR) {
            searchedText.append(char)
        }
        if (searchedText.toString() == searchText) return index
    }
    return -1
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

fun MaterialButton.changeFilterTypeButtonState(isButtonEnabled: Boolean, isClose: Boolean) {
    backgroundTintList = ContextCompat.getColorStateList(
        context,
        if (isButtonEnabled) R.color.button_bg else R.color.transparent
    )
    strokeColor = ContextCompat.getColorStateList(
        context,
        if (isButtonEnabled) R.color.button_bg else R.color.comet
    )
    compoundDrawables.onEach {
        iconTint = ContextCompat.getColorStateList(
            context,
            if (isButtonEnabled) R.color.white else R.color.comet
        )
    }
    setTextColor(
        ContextCompat.getColorStateList(
            context,
            if (isButtonEnabled) R.color.white else R.color.comet
        )
    )
    isEnabled = isButtonEnabled
    alpha = if (isButtonEnabled) 1f else 0.5f
    setText(if (isClose) R.string.number_details_close else R.string.filter_action_create)
}

fun ExtendedFloatingActionButton.changeFilterConditionButtonState(iconRes: Int?, isShown: Boolean) {
    iconRes?.let { setIconResource(it) }
    if (isShown) hide() else show()
}