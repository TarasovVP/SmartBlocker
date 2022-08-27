package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.tarasovvp.blacklister.ui.MainActivity

abstract class BaseBindingFragment<B : ViewDataBinding> : Fragment() {

    abstract var layoutId: Int
    var binding: B? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, layoutId, container, false
        )
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