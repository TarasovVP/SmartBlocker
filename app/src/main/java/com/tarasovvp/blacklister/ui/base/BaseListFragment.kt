package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.IncludeEmptyStateBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.utils.DebouncingQueryTextListener
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import com.tarasovvp.blacklister.utils.PermissionUtil.permissionsArray

abstract class BaseListFragment<B : ViewDataBinding, T : BaseViewModel, D : BaseAdapter.MainData> :
    BaseFragment<B, T>() {

    val adapter: BaseAdapter<D>? by lazy { createAdapter() }

    var swipeRefresh: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null
    var emptyStateContainer: IncludeEmptyStateBinding? = null
    protected var searchQuery: String? = ""

    abstract fun createAdapter(): BaseAdapter<D>?
    abstract fun initView()
    abstract fun searchDataList()
    abstract fun getData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = this@BaseListFragment.adapter
        }
        swipeRefresh?.setOnRefreshListener {
            getData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (context?.checkPermissions().isTrue()) {
            swipeRefresh?.isRefreshing = true
            getData()
        } else {
            permissionLauncher.launch(permissionsArray())
        }
        (activity as MainActivity).apply {
            val searchView = SearchView(this)
            toolbar?.menu?.findItem(R.id.search_menu_item)?.apply {
                actionView = searchView
            }
            searchView.queryHint = getString(R.string.enter_phone_number)
            searchView.setOnQueryTextListener(DebouncingQueryTextListener(lifecycle) {
                searchQuery = it
                searchDataList()
            })

            toolbar?.menu?.findItem(R.id.settings_menu_item)?.setOnMenuItemClickListener {
                findNavController().navigate(R.id.startSettingsListFragment)
                return@setOnMenuItemClickListener true
            }
            mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                Log.e("callLogTAG", "BaseListFragment successAllDataLiveData getData()")
                this@BaseListFragment.getData()
            }
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false).isTrue()) {
                Toast.makeText(
                    context,
                    getString(R.string.give_all_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                getData()
            }
        }

    protected open fun checkDataListEmptiness(newData: List<D>) {
        emptyStateContainer?.root?.isVisible = newData.isEmpty()
        emptyStateContainer?.emptyStateTitle?.text = String.format(getString(R.string.empty_state_title), (activity as MainActivity).toolbar?.title)
        emptyStateContainer?.emptyStateIcon?.setImageResource(R.drawable.ic_empty_state)
        if (newData.isEmpty()) {
            adapter?.clearData()
            adapter?.notifyDataSetChanged()
        }
        swipeRefresh?.isRefreshing = false
    }

    protected open fun setDataList(dataListHashMap: Map<String, List<D>>) {
        adapter?.clearData()
        dataListHashMap.forEach { dataEntry ->
            adapter?.setHeaderAndData(
                dataEntry.value,
                HeaderDataItem(
                    headerType = HeaderDataItem.HEADER_TYPE,
                    header = dataEntry.key
                )
            )
        }
        adapter?.notifyDataSetChanged()
        swipeRefresh?.isRefreshing = false
    }
}