package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
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
        (activity as MainActivity).showMessage(message, isError)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}