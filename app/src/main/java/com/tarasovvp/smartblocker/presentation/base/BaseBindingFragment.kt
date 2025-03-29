package com.tarasovvp.smartblocker.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_EXIT
import com.tarasovvp.smartblocker.presentation.main.MainActivity

abstract class BaseBindingFragment<B : ViewDataBinding> : Fragment() {
    abstract var layoutId: Int
    var binding: B? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater, layoutId, container, false,
            )
        getCurrentBackStackEntry()
        return binding?.root
    }

    fun showMessage(
        message: String,
        isError: Boolean,
    ) {
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
