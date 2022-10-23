package com.tarasovvp.blacklister.extensions

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
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
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.PopUpWindowInfoBinding
import com.tarasovvp.blacklister.model.Info
import com.tarasovvp.blacklister.utils.setSafeOnClickListener


fun View.showMessage(message: String, isError: Boolean) {
    ContextCompat.getColor(context, if (isError) android.R.color.holo_red_light else R.color.blue)
        .let { color ->
            Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
                .apply {
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.width = FrameLayout.LayoutParams.MATCH_PARENT
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines =
                        Int.MAX_VALUE
                }.withColor(color).show()
        }
}

fun View.showPopUpWindow(info: Info) {
    val popupView = PopUpWindowInfoBinding.inflate(LayoutInflater.from(context))
    popupView.popUpWindowTitle.text = info.title
    popupView.popUpWindowDescription.text = info.description
    popupView.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val popupWindow = PopupWindow(
        popupView.root,
        (Resources.getSystem().displayMetrics.widthPixels * 0.9).toInt(),
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )
    val locationScreen = intArrayOf(0, 0)
    this.getLocationOnScreen(locationScreen)
    val isBelowScreenMiddle =
        locationScreen[1] > Resources.getSystem().displayMetrics.heightPixels / 2
    popupWindow.setBackgroundDrawable(null)
    popupWindow.elevation = 5f
    popupWindow.showAsDropDown(this,
        0,
        if (isBelowScreenMiddle) - popupView.root.measuredHeight else 0)
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

@BindingAdapter(value = ["searchText", "mainText"], requireAll = false)
fun TextView.highlightText(searchText: String?, mainText: String?) {
    if (searchText.isNullOrEmpty().not() && mainText.isNullOrEmpty().not() && mainText.orEmpty()
            .lowercase().contains(
                searchText.orEmpty().lowercase())
    ) {
        SpannableString(mainText).apply {
            var index: Int =
                mainText.orEmpty().lowercase().indexOf(searchText.orEmpty().lowercase())
            while (index >= 0 && index < mainText?.length.orZero()) {
                val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1,
                    ColorStateList(arrayOf(intArrayOf()),
                        intArrayOf(ContextCompat.getColor(context, R.color.span_text))), null)
                setSpan(highlightSpan,
                    index, index + searchText?.length.orZero(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                index = mainText.orEmpty().lowercase()
                    .indexOf(searchText.orEmpty().lowercase(), index + 1)
            }
            text = this
        }
    } else {
        text = mainText
    }
}