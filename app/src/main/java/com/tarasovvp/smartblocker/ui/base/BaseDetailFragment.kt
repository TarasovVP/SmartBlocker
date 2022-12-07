package com.tarasovvp.smartblocker.ui.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.showPopUpWindow
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.number_data.filter_detail.FilterDetailFragment
import com.tarasovvp.smartblocker.ui.number_data.filtered_calls_detail.FilteredCallsDetailFragment

abstract class BaseDetailFragment<B : ViewDataBinding, T : BaseViewModel> :
    BaseFragment<B, T>() {

    abstract fun initViews()
    abstract fun setClickListeners()
    abstract fun createAdapter()
    abstract fun getData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setClickListeners()
        createAdapter()
        getData()
        setInfoMenu()
    }

    private fun setInfoMenu() {
        (activity as MainActivity).apply {
            toolbar?.inflateMenu(R.menu.toolbar_info)
            toolbar?.setOnMenuItemClickListener {
                toolbar?.showPopUpWindow(when (this@BaseDetailFragment) {
                    is FilterDetailFragment -> Info.INFO_FILTER_DETAIL
                    is FilteredCallsDetailFragment -> Info.INFO_BLOCKED_CALL_DETAIL
                    else -> Info.INFO_NUMBER_DATA_DETAIL
                })
                return@setOnMenuItemClickListener true
            }
        }
    }
}