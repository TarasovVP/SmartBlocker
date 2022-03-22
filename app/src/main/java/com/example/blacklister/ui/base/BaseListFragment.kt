package com.example.blacklister.ui.base

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.example.blacklister.R
import com.example.blacklister.utils.PermissionUtil.checkPermissions
import com.example.blacklister.utils.PermissionUtil.permissionsArray

abstract class BaseListFragment<VB : ViewBinding, T : ViewModel, D : BaseAdapter.MainData> :
    BaseFragment<VB, T>() {

    val adapter: BaseAdapter<D>? by lazy { createAdapter() }

    var swipeRefresh: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null
    var searchableEditText: EditText? = null

    abstract fun createAdapter(): BaseAdapter<D>?
    abstract fun initView()
    abstract fun getDataList()
    abstract fun filterDataList()

    protected fun RecyclerView.initRecyclerView() {
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        this.adapter = this@BaseListFragment.adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        initView()
        recyclerView?.initRecyclerView()
        if (context?.checkPermissions() == true) {
            swipeRefresh?.isRefreshing = true
            getDataList()
        } else {
            permissionLauncher.launch(permissionsArray())
        }
        swipeRefresh?.setOnRefreshListener {
            getDataList()
        }
        searchableEditText?.doAfterTextChanged {
            filterDataList()
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false) == true) {
                Toast.makeText(
                    context,
                    getString(R.string.give_all_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                getDataList()
            }
        }

    protected open fun dataLoaded(newData: List<D>, data: BaseAdapter.HeaderData) {
        swipeRefresh?.isRefreshing = false
        adapter?.apply {
            setHeaderAndData(newData, data)
        }
    }
}