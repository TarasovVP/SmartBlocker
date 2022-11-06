package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.IncludeEmptyStateBinding
import com.tarasovvp.blacklister.enums.Info
import com.tarasovvp.blacklister.extensions.showPopUpWindow
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.number_data.call_detail.CallDetailFragment
import com.tarasovvp.blacklister.ui.number_data.filter_detail.FilterDetailFragment

abstract class BaseDetailFragment<B : ViewDataBinding, T : BaseViewModel> :
    BaseFragment<B, T>() {

    protected var emptyStateContainer: IncludeEmptyStateBinding? = null

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
                    is CallDetailFragment -> Info.INFO_BLOCKED_CALL_DETAIL
                    else -> Info.INFO_NUMBER_DATA_DETAIL
                })
                return@setOnMenuItemClickListener true
            }
        }
    }
}