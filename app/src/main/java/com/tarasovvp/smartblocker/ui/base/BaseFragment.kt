package com.tarasovvp.smartblocker.ui.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.ui.MainActivity

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel> : BaseBindingFragment<B>() {

    abstract val viewModelClass: Class<VM>
    abstract fun observeLiveData()

    protected open val viewModel: VM by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[viewModelClass]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setProgressVisibility()
    }

    private fun setProgressVisibility() {
        viewModel.isProgressProcessLiveData.safeSingleObserve(viewLifecycleOwner) { isVisible ->
            (activity as MainActivity).setProgressVisibility(isVisible)
        }
        viewModel.isMainProgressProcessLiveData.safeSingleObserve(viewLifecycleOwner) { isVisible ->
            (activity as MainActivity).setMainProgressVisibility(isVisible)
        }
    }
}