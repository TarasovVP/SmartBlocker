package com.tarasovvp.smartblocker.ui.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.showPopUpWindow
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.number_data.details.filter_details.FilterDetailsFragment

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
                toolbar?.showPopUpWindow(when (this@BaseDetailFragment) {
                    is FilterDetailsFragment -> Info.INFO_FILTER_DETAIL
                    else -> Info.INFO_NUMBER_DATA_DETAIL
                })
                return@setOnMenuItemClickListener true
            }
        }
    }
}