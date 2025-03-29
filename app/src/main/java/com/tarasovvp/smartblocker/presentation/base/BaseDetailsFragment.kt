package com.tarasovvp.smartblocker.presentation.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.presentation.main.MainActivity

abstract class BaseDetailsFragment<B : ViewDataBinding, T : BaseViewModel> :
    BaseNumberDataFragment<B, T>() {
    abstract fun createAdapter()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        createAdapter()
        setInfoMenu()
    }

    private fun setInfoMenu() {
        (activity as? MainActivity)?.apply {
            toolbar?.inflateMenu(R.menu.toolbar_info)
            toolbar?.setOnMenuItemClickListener {
                showInfoScreen()
                return@setOnMenuItemClickListener true
            }
        }
    }
}
