package com.tarasovvp.smartblocker.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.tarasovvp.smartblocker.constants.Constants.APP_EXIT
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.ui.MainActivity

abstract class BaseFragment<B : ViewDataBinding, T : BaseViewModel> : BaseBindingFragment<B>() {

    abstract val viewModelClass: Class<T>
    abstract fun observeLiveData()

    protected open val viewModel: T by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[viewModelClass]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("filterLifeCycleTAG",
            "BaseFragment onViewCreated this $this savedInstanceState $savedInstanceState")
        getCurrentBackStackEntry()
        observeLiveData()
        setProgressVisibility()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isResumed", true)
    }

    private fun setProgressVisibility() {
        viewModel.isProgressProcess.safeSingleObserve(viewLifecycleOwner) { isVisible ->
            (activity as MainActivity).setProgressVisibility(isVisible)
        }
        viewModel.isMainProgressProcess.safeSingleObserve(viewLifecycleOwner) { isVisible ->
            (activity as MainActivity).setMainProgressVisibility(isVisible)
        }
    }

    private fun getCurrentBackStackEntry() {
        setFragmentResultListener(APP_EXIT) { _, _ ->
            activity?.finish()
        }
    }
}