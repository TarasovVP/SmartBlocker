package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.APP_EXIT
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.extensions.withColor
import com.tarasovvp.blacklister.ui.MainActivity

abstract class BaseFragment<VB : ViewBinding, T : BaseViewModel> : Fragment() {

    protected open var binding: VB? = null
    abstract fun getViewBinding(): VB

    abstract val viewModelClass: Class<T>
    abstract fun observeLiveData()

    protected open val viewModel: T by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[viewModelClass]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        checkTopBottomBarVisibility()
        binding = getViewBinding()
        getCurrentBackStackEntry()
        observeExceptionLiveData()
        observeLiveData()
        return binding?.root
    }

    open fun observeExceptionLiveData() {
        with(viewModel) {
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner, { exception ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun showMessage(message: String, isError: Boolean) {
        (activity as MainActivity).apply {
            context?.let {
                ContextCompat.getColor(it,
                    if (isError) android.R.color.holo_red_light else R.color.black)
            }
                ?.let { color ->
                    Snackbar.make(findViewById(android.R.id.content),
                        message,
                        Snackbar.LENGTH_LONG).withColor(color).show()
                }
        }
    }

    private fun checkTopBottomBarVisibility() {
        (activity as MainActivity).apply {
            if (findNavController().currentDestination?.id != R.id.infoDialog) {
                bottomNavigationView?.isVisible =
                    navigationScreens.contains(findNavController().currentDestination?.id)
                toolbar?.isVisible =
                    navigationScreens.contains(findNavController().currentDestination?.id)
            }
            toolbar?.navigationIcon = ContextCompat.getDrawable(
                this,
                if (findNavController().currentDestination?.id == R.id.callLogListFragment) R.drawable.ic_arrow_transparent else R.drawable.ic_arrow_back
            )
            toolbar?.setNavigationOnClickListener {
                if (findNavController().currentDestination?.id == R.id.callLogListFragment) return@setNavigationOnClickListener
                findNavController().popBackStack()
            }
        }
    }

}