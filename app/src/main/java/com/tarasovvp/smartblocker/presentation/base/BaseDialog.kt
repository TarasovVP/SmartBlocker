package com.tarasovvp.smartblocker.presentation.base

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
import com.tarasovvp.smartblocker.presentation.dialogs.countrycodesearchdialog.CountryCodeSearchDialog
import com.tarasovvp.smartblocker.utils.extensions.isNotNull

abstract class BaseDialog<B : ViewDataBinding> : DialogFragment() {
    abstract var layoutId: Int
    var binding: B? = null

    abstract fun initUI()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater, layoutId, container, false,
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
            val width = Resources.getSystem().displayMetrics.widthPixels
            val height = Resources.getSystem().displayMetrics.heightPixels
            dialog?.window?.setLayout(
                (width * 0.9).toInt(),
                if (this is CountryCodeSearchDialog) (height * 0.6).toInt() else LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            dialog?.window?.setGravity(Gravity.CENTER)
            dialog?.window?.setBackgroundDrawable(
                context?.let {
                    ContextCompat.getColor(it, R.color.transparent)
                }?.let { ColorDrawable(it) },
            )
            (binding?.root as? ViewGroup)?.getChildAt(0)?.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.bg_main) }
            dialog?.setCancelable(true)
        }
    }
}
