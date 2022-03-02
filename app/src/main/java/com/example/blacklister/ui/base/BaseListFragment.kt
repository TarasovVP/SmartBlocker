package com.example.blacklister.ui.base

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseListFragment<VB : ViewBinding, T : ViewModel, M : Any> : BaseFragment<VB, T>() {

    val adapter: BaseAdapter<M, *>? by lazy { createAdapter() }

    abstract fun createAdapter(): BaseAdapter<M, *>?

    protected fun RecyclerView.initRecyclerView(@RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL) {
        layoutManager = LinearLayoutManager(context, orientation, false)
        this.adapter = this@BaseListFragment.adapter
    }

    protected open fun onInitialDataLoaded(newData: List<M>) {
        adapter?.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }

    protected open fun onDataRangeLoaded(newData: List<M>) {
        adapter?.apply {
            addAll(newData)
            if (newData.isNotEmpty()) notifyItemRangeInserted(itemCount, newData.size)
        }
    }

    protected open fun updateListItems(newData: List<M>) {
        adapter?.updateListItems(newData, null)
    }
}