package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        checkBottomBarVisibility()
        return binding?.root
    }

    private fun checkBottomBarVisibility() {
        (activity as MainActivity).apply {
            bottomNavigationView?.isVisible = navigationScreens.contains(findNavController().currentDestination?.id)
        }
    }

    fun showMessage(message: String, isError: Boolean) {
        (activity as MainActivity).showMessage(message, isError)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}