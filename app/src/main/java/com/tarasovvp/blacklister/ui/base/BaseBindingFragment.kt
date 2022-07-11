package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.withColor
import com.tarasovvp.blacklister.ui.MainActivity

abstract class BaseBindingFragment<VB : ViewBinding> : Fragment() {

    protected open var binding: VB? = null
    abstract fun getViewBinding(): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = getViewBinding()
        return binding?.root
    }

    fun showMessage(message: String, isError: Boolean) {
        (activity as MainActivity).apply {
            ContextCompat.getColor(this,
                if (isError) android.R.color.holo_red_light else R.color.main_bg).let { color ->
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).apply {
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.width = FrameLayout.LayoutParams.MATCH_PARENT
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                }.withColor(color).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}