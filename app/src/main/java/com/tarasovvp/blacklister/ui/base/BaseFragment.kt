package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.APP_EXIT
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity

abstract class BaseFragment<VB : ViewBinding, T : BaseViewModel> : BaseBindingFragment<VB>() {

    abstract val viewModelClass: Class<T>
    abstract fun observeLiveData()

    protected open val viewModel: T by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[viewModelClass]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkTopBottomBarVisibility()
        checkToolbarSearchVisibility()
        getCurrentBackStackEntry()
        observeExceptionLiveData()
        observeLiveData()
    }

    open fun observeExceptionLiveData() {
        with(viewModel) {
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner, { exception ->
                showMessage(exception, true)
            })
        }
        (activity as MainActivity).apply {
            mainViewModel.exceptionLiveData.safeSingleObserve(viewLifecycleOwner, { exception ->
                showMessage(exception, true)
            })
        }
    }

    private fun getCurrentBackStackEntry() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(APP_EXIT)
            ?.safeSingleObserve(
                viewLifecycleOwner
            ) { exitApp ->
                if (exitApp) {
                    activity?.finish()
                }
            }
    }

    private fun checkTopBottomBarVisibility() {
        (activity as MainActivity).apply {
            if (findNavController().currentDestination?.id != R.id.deleteNumberDialog) {
                bottomNavigationView?.isVisible =
                    navigationScreens.contains(findNavController().currentDestination?.id)
            }
        }
    }

    private fun checkToolbarSearchVisibility() {
        (activity as MainActivity).apply {
            toolbar?.menu?.clear()
            if (navigationScreens.contains(findNavController().currentDestination?.id)) {
                toolbar?.inflateMenu(R.menu.toolbar_search)
            }
        }
    }
}