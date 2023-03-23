package com.tarasovvp.smartblocker.presentation.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.tarasovvp.smartblocker.utils.extensions.hideKeyboard

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
        getData()
    }

    override fun onPause() {
        super.onPause()
        binding?.root?.hideKeyboard()
    }
}