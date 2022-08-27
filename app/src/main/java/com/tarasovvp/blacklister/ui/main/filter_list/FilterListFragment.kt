package com.tarasovvp.blacklister.ui.main.filter_list

import android.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentFilterListBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeObserve
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

open class FilterListFragment :
    BaseListFragment<FragmentFilterListBinding, FilterListViewModel, Filter>() {

    override var layoutId = R.layout.fragment_filter_list
    override val viewModelClass = FilterListViewModel::class.java

    private var filterList: ArrayList<Filter>? = null
    private var isDeleteMode = false
    private var selectedFilterItems: BooleanArray = booleanArrayOf(false, false, false)

    override fun createAdapter(): BaseAdapter<Filter>? {
        return context?.let {
            FilterAdapter(object : FilterClickListener {
                override fun onNumberClick(filter: Filter) {
                    findNavController().navigate(WhiteFilterListFragmentDirections.startAddFragment(
                        filter = filter))
                }

                override fun onNumberLongClick() {
                    changeDeleteMode()
                }

                override fun onNumberDeleteCheckChange(filter: Filter) {
                    filterList?.find { it.filter == filter.filter }?.isCheckedForDelete =
                        filter.isCheckedForDelete
                    binding?.filterListDeleteBtn?.isVisible =
                        filterList?.none { it.isCheckedForDelete }.isTrue().not()
                    binding?.filterListDeleteAll?.isChecked =
                        filterList?.none { it.isCheckedForDelete.not() }.isTrue()
                }

            })
        }
    }

    override fun initView() {
        swipeRefresh = binding?.filterListRefresh
        recyclerView = binding?.filterListRecyclerView
        emptyListText = binding?.filterListEmpty
        binding?.filterListDeleteAll?.setOnCheckedChangeListener { _, checked ->
            filterList?.forEach { it.isCheckedForDelete = checked }
            adapter?.notifyDataSetChanged()
        }
        setClickListeners()
        setFragmentResultListener(Constants.DELETE_NUMBER) { _, _ ->
            viewModel.deleteFilterList(filterList?.filter { it.isCheckedForDelete }.orEmpty(),
                this is BlackFilterListFragment)
        }
    }

    private fun setClickListeners() {
        binding?.filterListDeleteBtn?.setSafeOnClickListener {
            findNavController().navigate(WhiteFilterListFragmentDirections.startDeleteFilterDialog())
        }
        binding?.filterListFabNew?.setSafeOnClickListener {
            findNavController().navigate(WhiteFilterListFragmentDirections.startAddFragment(
                filter = Filter().apply {
                    isBlackFilter = this@FilterListFragment is BlackFilterListFragment
                }))
        }
        binding?.filterListFilter?.setSafeOnClickListener {
            filterDataList()
        }
    }

    private fun changeDeleteMode() {
        isDeleteMode = isDeleteMode.not()
        (adapter as FilterAdapter).apply {
            isDeleteMode = this@FilterListFragment.isDeleteMode
            notifyDataSetChanged()
        }
        binding?.apply {
            filterListFilter.isVisible = isDeleteMode.not()
            filterListDeleteAll.isVisible = isDeleteMode
            filterListDeleteBtn.isVisible =
                isDeleteMode && filterList?.find { it.isCheckedForDelete }?.isNotNull().isTrue()
            if (isDeleteMode.not()) {
                filterList?.forEach {
                    it.isCheckedForDelete = false
                }
                filterListDeleteAll.isChecked = false
            }
        }
    }

    private fun filterDataList(): Boolean {
        val filterItems = arrayOf(getString(R.string.filter_contain),
            getString(R.string.filter_start),
            getString(R.string.filter_end))
        val builder =
            AlertDialog.Builder(ContextThemeWrapper(context, R.style.MultiChoiceAlertDialog))
        builder.setMultiChoiceItems(filterItems, selectedFilterItems
        ) { _, position, isChecked -> selectedFilterItems[position] = isChecked }
        builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        builder.setPositiveButton(R.string.ok) { _, _ ->
            val itemsTitleList = arrayListOf<String>()
            filterItems.forEachIndexed { index, title ->
                if (selectedFilterItems[index]) {
                    itemsTitleList.add(title)
                }
            }
            binding?.filterListFilter?.text =
                if (itemsTitleList.isEmpty()) getString(R.string.filter_no_filter) else itemsTitleList.joinToString(
                    ", ")
            searchDataList()
        }
        builder.show()
        return true
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeObserve(viewLifecycleOwner) { filterList ->
                this@FilterListFragment.filterList = filterList as ArrayList<Filter>
                if (checkDataListEmptiness(filterList).not()) {
                    getHashMapFromFilterList(filterList)
                }
            }
            filterHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filterList?.let { setDataList(it) }
            }
            successDeleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                this@FilterListFragment.filterList?.removeAll { it.isCheckedForDelete }
                changeDeleteMode()
                searchDataList()
            }
        }
    }

    override fun searchDataList() {
        val filteredList = filterList?.filter { filter ->
            filter.filter.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ) && (if (selectedFilterItems[0]) filter.contain else true)
                    && (if (selectedFilterItems[1]) filter.start else true)
                    && if (selectedFilterItems[2]) filter.end else true
        }.orEmpty()
        if (checkDataListEmptiness(filteredList).not()) {
            viewModel.getHashMapFromFilterList(filteredList)
        }
    }

    override fun getData() {
        viewModel.getFilterList(this is BlackFilterListFragment)
    }
}

