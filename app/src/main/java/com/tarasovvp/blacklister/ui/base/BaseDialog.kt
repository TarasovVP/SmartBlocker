package com.tarasovvp.blacklister.ui.base

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.tarasovvp.blacklister.extensions.isNotNull

abstract class BaseDialog<VB : ViewBinding> : DialogFragment() {

    protected open var binding: VB? = null
    abstract fun getViewBinding(): VB

    abstract fun initUI()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        binding = getViewBinding()
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
            dialog?.window?.setLayout((width * 0.85).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog?.window?.setGravity(Gravity.CENTER)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setCancelable(true)
        }
    }
}