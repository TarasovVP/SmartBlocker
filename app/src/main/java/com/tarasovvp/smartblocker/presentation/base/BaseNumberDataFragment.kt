package com.tarasovvp.smartblocker.presentation.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.hideKeyboard
import com.tarasovvp.smartblocker.utils.extensions.isNotTrue

abstract class BaseNumberDataFragment<B : ViewDataBinding, T : BaseViewModel> :
    BaseFragment<B, T>() {

    abstract fun initViews()
    abstract fun setClickListeners()
    abstract fun setFragmentResultListeners()
    abstract fun getData()
    abstract fun showInfoScreen()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setClickListeners()
        setFragmentResultListeners()
        if (activity?.intent?.getBooleanExtra(Constants.IS_INSTRUMENTAL_TEST,false).isNotTrue()) getData()
    }

    override fun onPause() {
        super.onPause()
        binding?.root?.hideKeyboard()
    }
}