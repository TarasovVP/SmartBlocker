package com.tarasovvp.smartblocker.presentation.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel> : BaseBindingFragment<B>() {
    abstract val viewModelClass: Class<VM>

    abstract fun observeLiveData()

    open val viewModel: VM by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[viewModelClass]
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setExceptionMessageDisplaying()
        observeLiveData()
        setProgressVisibility()
    }

    private fun setExceptionMessageDisplaying() {
        viewModel.exceptionLiveData.safeSingleObserve(viewLifecycleOwner) { exception ->
            showMessage(exception, true)
        }
    }

    private fun setProgressVisibility() {
        viewModel.isProgressProcessLiveData.safeSingleObserve(viewLifecycleOwner) { isVisible ->
            (activity as? MainActivity)?.setProgressVisibility(isVisible)
        }
        viewModel.isMainProgressProcessLiveData.safeSingleObserve(viewLifecycleOwner) { isVisible ->
            (activity as? MainActivity)?.setMainProgressVisibility(isVisible)
        }
    }
}
