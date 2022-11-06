package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.IncludeEmptyStateBinding
import com.tarasovvp.blacklister.extensions.showPopUpWindow
import com.tarasovvp.blacklister.model.Info
import com.tarasovvp.blacklister.ui.MainActivity

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
            toolbar?.setOnMenuItemClickListener { menuItem ->
                //TODO add info
                toolbar?.showPopUpWindow(Info("Test"))
                return@setOnMenuItemClickListener true
            }
        }
    }
}