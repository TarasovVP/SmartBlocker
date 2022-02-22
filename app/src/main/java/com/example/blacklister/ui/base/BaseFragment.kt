package com.example.blacklister.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding, T : ViewModel> : Fragment() {

    protected open var binding: VB? = null
    abstract fun getViewBinding(): VB

    abstract val viewModelClass: Class<T>

    protected open val viewModel: T by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[viewModelClass]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}