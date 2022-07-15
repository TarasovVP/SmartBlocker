package com.tarasovvp.blacklister.ui.main.blacknumberlist

import android.app.AlertDialog
import android.util.Log
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentBlackNumberListBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeObserve
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*
import kotlin.collections.ArrayList

class BlackNumberListFragment :
    BaseListFragment<FragmentBlackNumberListBinding, BlackNumberListViewModel, BlackNumber>() {

    override fun getViewBinding() = FragmentBlackNumberListBinding.inflate(layoutInflater)

    override val viewModelClass = BlackNumberListViewModel::class.java

    private var blackNumberList: ArrayList<BlackNumber>? = null
    private var isDeleteMode = false
    private var selectedFilterItems: BooleanArray = booleanArrayOf(false, false, false)

    override fun createAdapter(): BaseAdapter<BlackNumber>? {
        return context?.let {
            BlackNumberAdapter(object : BlackNumberClickListener {
                override fun onBlackNumberClick(blackNumber: BlackNumber) {
                    findNavController().navigate(BlackNumberListFragmentDirections.startNumberAddFragment(
                        blackNumber = blackNumber))
                }

                override fun onBlackNumberLongClick() {
                    changeDeleteMode()
                }

                override fun onBlackNumberDeleteCheckChange(blackNumber: BlackNumber) {
                    blackNumberList?.find {
                        it.number == blackNumber.number
                    }?.isCheckedForDelete = blackNumber.isCheckedForDelete
                    binding?.blackNumberListDeleteBtn?.isVisible = blackNumberList?.none { it.isCheckedForDelete }.isTrue().not()
                }

            })
        }
    }

    override fun initView() {
        swipeRefresh = binding?.blackNumberListRefresh
        recyclerView = binding?.blackNumberListRecyclerView
        emptyListText = binding?.blackNumberListEmpty
        binding?.blackNumberListDeleteAll?.setOnCheckedChangeListener { _, checked ->
            blackNumberList?.forEach {
                it.isCheckedForDelete = checked
            }
            adapter?.notifyDataSetChanged()
        }
        binding?.blackNumberListDeleteBtn?.setSafeOnClickListener {
            blackNumberList?.removeAll { it.isCheckedForDelete }
            Log.e("deleteTAG", "BlackNumberListFragment blackNumberListDeleteBtn blackNumberList?.size ${blackNumberList?.size}")
            changeDeleteMode()
        }
        binding?.blackNumberListFabNew?.setSafeOnClickListener {
            findNavController().navigate(BlackNumberListFragmentDirections.startNumberAddFragment(
                blackNumber = BlackNumber()))
        }
        binding?.blackNumberListFilter?.setSafeOnClickListener {
            filterDataList()
        }
    }

    private fun changeDeleteMode() {
        Log.e("deleteTAG", "BlackNumberListFragment changeDeleteMode isDeleteMode $isDeleteMode blackNumberList?.size ${blackNumberList?.size}")
        isDeleteMode = isDeleteMode.not()
        binding?.blackNumberListFilter?.isVisible = isDeleteMode.not()
        binding?.blackNumberListDeleteAll?.isVisible = isDeleteMode
        binding?.blackNumberListDeleteBtn?.isVisible = isDeleteMode && blackNumberList?.find { it.isCheckedForDelete }?.isNotNull().isTrue()
        if (isDeleteMode.not()) {
            blackNumberList?.forEach {
                it.isCheckedForDelete = false
            }
            binding?.blackNumberListDeleteAll?.isChecked = false
        }
        (adapter as BlackNumberAdapter).apply {
            isDeleteMode = this@BlackNumberListFragment.isDeleteMode
            notifyDataSetChanged()
            Log.e("deleteTAG", "BlackNumberListFragment BlackNumberAdapter changeDeleteMode blackNumberList?.size ${blackNumberList?.size}")
        }
    }

    private fun filterDataList(): Boolean {
        val filterItems = arrayOf(getString(R.string.black_number_contain),
            getString(R.string.black_number_start),
            getString(R.string.black_number_end))
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
            binding?.blackNumberListFilter?.text =
                if (itemsTitleList.isEmpty()) getString(R.string.black_number_no_filter) else itemsTitleList.joinToString(
                    ", ")
            searchDataList()
        }
        builder.show()
        return true
    }

    override fun observeLiveData() {
        with(viewModel) {
            blackNumberList.safeObserve(viewLifecycleOwner) { blackNumberList ->
                this@BlackNumberListFragment.blackNumberList = blackNumberList as ArrayList<BlackNumber>
                if (!checkDataListEmptiness(blackNumberList)) {
                    getHashMapFromBlackNumberList(blackNumberList)
                }
            }
            blackNumberHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { blackNumberHashMap ->
                blackNumberHashMap?.let { setDataList(it) }
            }
        }
    }

    override fun searchDataList() {
        val filteredBlackNumberList = blackNumberList?.filter { blackNumber ->
            blackNumber.number.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ) && (if (selectedFilterItems[0]) blackNumber.contain else true)
                    && (if (selectedFilterItems[1]) blackNumber.start else true)
                    && if (selectedFilterItems[2]) blackNumber.end else true
        } as ArrayList<BlackNumber>
        if (!checkDataListEmptiness(filteredBlackNumberList)) {
            viewModel.getHashMapFromBlackNumberList(filteredBlackNumberList)
        }
    }

    override fun getData() {
        Log.e("getAllDataTAG", "BlackNumberListFragment getAllData")
        viewModel.getBlackNumberList()
    }
}

