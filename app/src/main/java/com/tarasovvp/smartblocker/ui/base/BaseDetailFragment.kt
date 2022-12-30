package com.tarasovvp.smartblocker.ui.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.ui.MainActivity

abstract class BaseDetailFragment<B : ViewDataBinding, T : BaseViewModel> :
    BaseFragment<B, T>() {

    abstract fun initViews()
    abstract fun setClickListeners()
    abstract fun setFragmentResultListeners()
    abstract fun createAdapter()
    abstract fun getData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setClickListeners()
        setFragmentResultListeners()
        createAdapter()
        getData()
        setInfoMenu()
    }

    private fun setInfoMenu() {
        (activity as MainActivity).apply {
            toolbar?.inflateMenu(R.menu.toolbar_info)
            toolbar?.setOnMenuItemClickListener {
                //TODO implement
                return@setOnMenuItemClickListener true
            }
        }
    }
}