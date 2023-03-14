package com.tarasovvp.smartblocker.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.constants.Constants.APP_EXIT
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.ui.MainActivity

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
        getCurrentBackStackEntry()
        return binding?.root
    }

    private fun checkBottomBarVisibility() {
        (activity as? MainActivity)?.apply {
            bottomNavigationView?.isVisible = try {
                navigationScreens.contains(findNavController().currentDestination?.id)
            } catch (e: Exception) {
                false
            }
            bottomNavigationDivider?.isVisible = bottomNavigationView?.isVisible.isTrue()
        }
    }

    fun showMessage(message: String, isError: Boolean) {
        (activity as? MainActivity)?.showInfoMessage(message, isError)
    }

    private fun getCurrentBackStackEntry() {
        setFragmentResultListener(APP_EXIT) { _, _ ->
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}