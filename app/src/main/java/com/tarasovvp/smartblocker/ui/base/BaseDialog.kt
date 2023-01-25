package com.tarasovvp.smartblocker.ui.base

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.extensions.isNotNull
import com.tarasovvp.smartblocker.ui.dialogs.country_code_search_dialog.CountryCodeSearchDialog

abstract class BaseDialog<B : ViewDataBinding> : DialogFragment() {

    abstract var layoutId: Int
    var binding: B? = null

    abstract fun initUI()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, layoutId, container, false
        )
        initUI()
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog.isNotNull() && dialog?.window.isNotNull()) {
            val desiredHeight = binding?.root?.height?.let {
                View.MeasureSpec.makeMeasureSpec(it,
                    View.MeasureSpec.EXACTLY)
            }
            desiredHeight?.let { binding?.root?.measure(it, View.MeasureSpec.UNSPECIFIED) }
            val width = Resources.getSystem().displayMetrics.widthPixels
            val height = Resources.getSystem().displayMetrics.heightPixels
            dialog?.window?.setLayout((width * 0.85).toInt(),
                if (this is CountryCodeSearchDialog) (height * 0.6).toInt() else LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog?.window?.setGravity(Gravity.CENTER)
            dialog?.window?.setBackgroundDrawable(context?.let {
                ContextCompat.getColor(it, R.color.transparent)
            }?.let { ColorDrawable(it) })
            dialog?.setCancelable(true)
        }
    }
}