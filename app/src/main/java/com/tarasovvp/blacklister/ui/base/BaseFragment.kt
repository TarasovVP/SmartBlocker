package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.APP_EXIT
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.main.add.FilterAddFragment
import com.tarasovvp.blacklister.ui.main.add.FullNumberAddFragment

abstract class BaseFragment<B : ViewDataBinding, T : BaseViewModel> : BaseBindingFragment<B>() {

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
        observeLiveData()
        setProgressVisibility()
    }

    private fun setProgressVisibility() {
        viewModel.isProgressProcess.safeSingleObserve(viewLifecycleOwner) { isVisible ->
            Log.e("getAllDataTAG", "BaseFragment setProgressVisibility isVisible $isVisible this $this")
            (activity as MainActivity).setProgressVisibility(isVisible)
        }
    }

    private fun getCurrentBackStackEntry() {
        setFragmentResultListener(APP_EXIT) { _, _ ->
            activity?.finish()
        }
    }

    private fun checkTopBottomBarVisibility() {
        (activity as MainActivity).apply {
            if (this.findNavController(R.id.host_main_fragment).currentDestination?.id != R.id.deleteFilterDialog) {
                bottomNavigationView?.isVisible =
                    navigationScreens.contains(this.findNavController(R.id.host_main_fragment).currentDestination?.id)
            }
        }
    }

    private fun checkToolbarSearchVisibility() {
        (activity as MainActivity).apply {
            if ((this@BaseFragment is FullNumberAddFragment || this@BaseFragment is FilterAddFragment).not()) {
                toolbar?.menu?.clear()
            }
            Log.e("toolbarTAG",
                "BaseFragment checkToolbarSearchVisibility currentDestination?.displayName ${
                    this.findNavController(R.id.host_main_fragment).currentDestination?.displayName
                } this ${this@BaseFragment}")
            if (navigationScreens.contains(this.findNavController(R.id.host_main_fragment).currentDestination?.id)) {
                toolbar?.inflateMenu(R.menu.toolbar_search)
            }
        }
    }
}