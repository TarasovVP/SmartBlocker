package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.tarasovvp.blacklister.constants.Constants.APP_EXIT
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity

abstract class BaseFragment<B : ViewDataBinding, T : BaseViewModel> : BaseBindingFragment<B>() {

    abstract val viewModelClass: Class<T>
    abstract fun observeLiveData()

    protected open val viewModel: T by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[viewModelClass]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentBackStackEntry()
        observeLiveData()
        setProgressVisibility()
    }

    private fun setProgressVisibility() {
        viewModel.isProgressProcess.safeSingleObserve(viewLifecycleOwner) { isVisible ->
            Log.e("getAllDataTAG",
                "BaseFragment setProgressVisibility isVisible $isVisible this $this")
            (activity as MainActivity).setProgressVisibility(isVisible)
        }
    }

    private fun getCurrentBackStackEntry() {
        setFragmentResultListener(APP_EXIT) { _, _ ->
            activity?.finish()
        }
    }
}