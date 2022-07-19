package com.tarasovvp.blacklister.ui.main.whitenumberlist

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
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.ui.main.blacknumberlist.BlackNumberAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

class WhiteNumberListFragment :
    BaseListFragment<FragmentBlackNumberListBinding, WhiteNumberListViewModel, WhiteNumber>() {

    override fun getViewBinding() = FragmentBlackNumberListBinding.inflate(layoutInflater)

    override val viewModelClass = WhiteNumberListViewModel::class.java

    private var whiteNumberList: ArrayList<WhiteNumber>? = null
    private var isDeleteMode = false
    private var selectedFilterItems: BooleanArray = booleanArrayOf(false, false, false)

    override fun createAdapter(): BaseAdapter<WhiteNumber>? {
        return context?.let {
            WhiteNumberAdapter(object : WhiteNumberClickListener {
                override fun onWhiteNumberClick(whiteNumber: WhiteNumber) {
                    findNavController().navigate(WhiteNumberListFragmentDirections.startNumberAddFragment(
                        whiteNumber = whiteNumber))
                }

                override fun onWhiteNumberLongClick() {
                    changeDeleteMode()
                }

                override fun onWhiteNumberDeleteCheckChange(whiteNumber: WhiteNumber) {
                    whiteNumberList?.find {
                        it.number == whiteNumber.number
                    }?.isCheckedForDelete = whiteNumber.isCheckedForDelete
                    binding?.numberListDeleteBtn?.isVisible =
                        whiteNumberList?.none { it.isCheckedForDelete }.isTrue().not()
                }

            })
        }
    }

    override fun initView() {
        swipeRefresh = binding?.numberListRefresh
        recyclerView = binding?.numberListRecyclerView
        emptyListText = binding?.numberListEmpty
        binding?.numberListDeleteAll?.setOnCheckedChangeListener { _, checked ->
            whiteNumberList?.forEach {
                it.isCheckedForDelete = checked
            }
            adapter?.notifyDataSetChanged()
        }
        binding?.numberListDeleteBtn?.setSafeOnClickListener {
            viewModel.deleteWhiteNumberList(whiteNumberList?.filter { it.isCheckedForDelete }
                .orEmpty())
        }
        binding?.numberListFabNew?.setSafeOnClickListener {
            findNavController().navigate(WhiteNumberListFragmentDirections.startNumberAddFragment(
                whiteNumber = WhiteNumber()))
        }
        binding?.numberListFilter?.setSafeOnClickListener {
            filterDataList()
        }
    }

    private fun changeDeleteMode() {
        Log.e("deleteTAG",
            "WhiteNumberListFragment changeDeleteMode isDeleteMode $isDeleteMode whiteNumberList?.size ${whiteNumberList?.size}")
        isDeleteMode = isDeleteMode.not()
        binding?.numberListFilter?.isVisible = isDeleteMode.not()
        binding?.numberListDeleteAll?.isVisible = isDeleteMode
        binding?.numberListDeleteBtn?.isVisible =
            isDeleteMode && whiteNumberList?.find { it.isCheckedForDelete }?.isNotNull().isTrue()
        if (isDeleteMode.not()) {
            whiteNumberList?.forEach {
                it.isCheckedForDelete = false
            }
            binding?.numberListDeleteAll?.isChecked = false
        }
        (adapter as WhiteNumberAdapter).apply {
            isDeleteMode = this@WhiteNumberListFragment.isDeleteMode
            notifyDataSetChanged()
            Log.e("deleteTAG",
                "WhiteNumberListFragment BlackNumberAdapter changeDeleteMode whiteNumberList?.size ${whiteNumberList?.size}")
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
            binding?.numberListFilter?.text =
                if (itemsTitleList.isEmpty()) getString(R.string.black_number_no_filter) else itemsTitleList.joinToString(
                    ", ")
            searchDataList()
        }
        builder.show()
        return true
    }

    override fun observeLiveData() {
        with(viewModel) {
            whiteNumberList.safeObserve(viewLifecycleOwner) { whiteNumberList ->
                this@WhiteNumberListFragment.whiteNumberList =
                    whiteNumberList as ArrayList<WhiteNumber>
                if (!checkDataListEmptiness(whiteNumberList)) {
                    getHashMapFromWhiteNumberList(whiteNumberList)
                }
            }
            whiteNumberHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { whiteNumberList ->
                whiteNumberList?.let { setDataList(it) }
            }
            successDeleteNumberLiveData.safeSingleObserve(viewLifecycleOwner) {
                this@WhiteNumberListFragment.whiteNumberList?.removeAll { it.isCheckedForDelete }
                changeDeleteMode()
                searchDataList()
            }
        }
    }

    override fun searchDataList() {
        val filteredWhiteNumberList = whiteNumberList?.filter { whiteNumber ->
            whiteNumber.number.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ) && (if (selectedFilterItems[0]) whiteNumber.contain else true)
                    && (if (selectedFilterItems[1]) whiteNumber.start else true)
                    && if (selectedFilterItems[2]) whiteNumber.end else true
        } as ArrayList<WhiteNumber>
        if (!checkDataListEmptiness(filteredWhiteNumberList)) {
            viewModel.getHashMapFromWhiteNumberList(filteredWhiteNumberList)
        }
    }

    override fun getData() {
        Log.e("getAllDataTAG", "WhiteNumberListFragment getAllData")
        viewModel.getWhiteNumberList()
    }
}

