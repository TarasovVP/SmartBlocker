package com.tarasovvp.blacklister.ui.main.blacknumberlist

import android.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_NUMBER
import com.tarasovvp.blacklister.databinding.FragmentBlackNumberListBinding
import com.tarasovvp.blacklister.extensions.safeObserve
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.extensions.showPopUpMenu
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

class BlackNumberListFragment :
    BaseListFragment<FragmentBlackNumberListBinding, BlackNumberListViewModel, BlackNumber>() {

    override fun getViewBinding() = FragmentBlackNumberListBinding.inflate(layoutInflater)

    override val viewModelClass = BlackNumberListViewModel::class.java

    private var blackNumberList: List<BlackNumber>? = null
    private var selectedFilterItems: BooleanArray = booleanArrayOf(false, false, false)

    override fun createAdapter(): BaseAdapter<BlackNumber>? {
        return context?.let { context ->
            BlackNumberAdapter { blackNumber, view ->
                val listener = PopupMenu.OnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.delete -> {
                            findNavController().navigate(BlackNumberListFragmentDirections.startInfoDialog(
                                blackNumber = blackNumber))
                        }
                        R.id.edit -> {
                            findNavController().navigate(BlackNumberListFragmentDirections.startNumberAddFragment(
                                blackNumber = blackNumber))
                        }
                    }
                    true
                }
                context.showPopUpMenu(R.menu.black_number_menu, view, listener)
            }
        }
    }

    override fun initView() {
        swipeRefresh = binding?.blackNumberListRefresh
        recyclerView = binding?.blackNumberListRecyclerView
        emptyListText = binding?.blackNumberListEmpty
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BlackNumber>(DELETE_NUMBER)?.safeSingleObserve(viewLifecycleOwner) { blackNumber ->
                viewModel.deleteBlackNumber(blackNumber)
            }
        binding?.blackNumberListFabNew?.setSafeOnClickListener {
            findNavController().navigate(BlackNumberListFragmentDirections.startNumberAddFragment(
                blackNumber = BlackNumber()))
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

    override fun getDataList() {
        viewModel.getBlackNumberList()
    }

    override fun observeLiveData() {
        with(viewModel) {
            blackNumberList.safeObserve(viewLifecycleOwner, { blackNumberList ->
                this@BlackNumberListFragment.blackNumberList = blackNumberList
                if (!checkDataListEmptiness(blackNumberList)) {
                    getHashMapFromBlackNumberList(blackNumberList)
                }
            })
            blackNumberHashMapLiveData.safeSingleObserve(viewLifecycleOwner, { blackNumberHashMap ->
                blackNumberHashMap?.let { setDataList(it) }
            })
        }
    }

    override fun searchDataList() {
        val filteredBlackNumberList = blackNumberList?.filter { blackNumber ->
            blackNumber.blackNumber.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ) && (if (selectedFilterItems[0]) blackNumber.contain else true)
                    && (if (selectedFilterItems[1]) blackNumber.start else true)
                    && if (selectedFilterItems[2]) blackNumber.end else true
        } as ArrayList<BlackNumber>
        if (!checkDataListEmptiness(filteredBlackNumberList)) {
            viewModel.getHashMapFromBlackNumberList(filteredBlackNumberList)
        }
    }
}

