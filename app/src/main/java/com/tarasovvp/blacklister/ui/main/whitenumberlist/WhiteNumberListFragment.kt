package com.tarasovvp.blacklister.ui.main.whitenumberlist

import android.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentBlackNumberListBinding
import com.tarasovvp.blacklister.extensions.safeObserve
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

class WhiteNumberListFragment :
    BaseListFragment<FragmentBlackNumberListBinding, WhiteNumberListViewModel, WhiteNumber>() {

    override fun getViewBinding() = FragmentBlackNumberListBinding.inflate(layoutInflater)

    override val viewModelClass = WhiteNumberListViewModel::class.java

    private var whiteNumberList: List<WhiteNumber>? = null
    private var selectedFilterItems: BooleanArray = booleanArrayOf(false, false, false)

    override fun createAdapter(): BaseAdapter<WhiteNumber>? {
        return context?.let { context ->
            WhiteNumberAdapter { whiteNumber ->
                findNavController().navigate(WhiteNumberListFragmentDirections.startNumberAddFragment(
                    whiteNumber = whiteNumber))
            }
        }
    }

    override fun initView() {
        swipeRefresh = binding?.blackNumberListRefresh
        recyclerView = binding?.blackNumberListRecyclerView
        emptyListText = binding?.blackNumberListEmpty
        binding?.blackNumberListFabNew?.setSafeOnClickListener {
            findNavController().navigate(WhiteNumberListFragmentDirections.startNumberAddFragment(
                whiteNumber = WhiteNumber()))
        }
        binding?.blackNumberListFilter?.setSafeOnClickListener {
            filterDataList()
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
            whiteNumberList.safeObserve(viewLifecycleOwner, { whiteNumberList ->
                this@WhiteNumberListFragment.whiteNumberList = whiteNumberList
                if (!checkDataListEmptiness(whiteNumberList)) {
                    getHashMapFromWhiteNumberList(whiteNumberList)
                }
            })
            whiteNumberHashMapLiveData.safeSingleObserve(viewLifecycleOwner, { whiteNumberList ->
                whiteNumberList?.let { setDataList(it) }
            })
        }
        (activity as MainActivity).apply {
            mainViewModel?.successAllDataLiveData?.safeSingleObserve(this, { success ->
                viewModel.getWhiteNumberList()
            })
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
}

