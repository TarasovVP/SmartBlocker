package com.tarasovvp.blacklister.ui.main.numberlist

import android.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentBlackNumberListBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeObserve
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

open class NumberListFragment :
    BaseListFragment<FragmentBlackNumberListBinding, NumberListViewModel, Number>() {

    override fun getViewBinding() = FragmentBlackNumberListBinding.inflate(layoutInflater)

    override val viewModelClass = NumberListViewModel::class.java

    private var numberList: ArrayList<Number>? = null
    private var isDeleteMode = false
    private var selectedFilterItems: BooleanArray = booleanArrayOf(false, false, false)

    override fun createAdapter(): BaseAdapter<Number>? {
        return context?.let {
            NumberAdapter(object : NumberClickListener {
                override fun onNumberClick(number: Number) {
                    findNavController().navigate(WhiteNumberListFragmentDirections.startNumberAddFragment())
                }

                override fun onNumberLongClick() {
                    changeDeleteMode()
                }

                override fun onNumberDeleteCheckChange(number: Number) {
                    numberList?.find { it.number == number.number }?.isCheckedForDelete =
                        number.isCheckedForDelete
                    binding?.numberListDeleteBtn?.isVisible =
                        numberList?.none { it.isCheckedForDelete }.isTrue().not()
                }

            })
        }
    }

    override fun initView() {
        swipeRefresh = binding?.numberListRefresh
        recyclerView = binding?.numberListRecyclerView
        emptyListText = binding?.numberListEmpty
        binding?.numberListDeleteAll?.setOnCheckedChangeListener { _, checked ->
            numberList?.forEach { it.isCheckedForDelete = checked }
            adapter?.notifyDataSetChanged()
        }
        binding?.numberListDeleteBtn?.setSafeOnClickListener {
            viewModel.deleteNumberList(numberList?.filter { it.isCheckedForDelete }.orEmpty(),
                this is BlackNumberListFragment)
        }
        binding?.numberListFabNew?.setSafeOnClickListener {
            findNavController().navigate(WhiteNumberListFragmentDirections.startNumberAddFragment(
                number = WhiteNumber()))
        }
        binding?.numberListFilter?.setSafeOnClickListener {
            filterDataList()
        }
    }

    private fun changeDeleteMode() {
        isDeleteMode = isDeleteMode.not()
        binding?.numberListFilter?.isVisible = isDeleteMode.not()
        binding?.numberListDeleteAll?.isVisible = isDeleteMode
        binding?.numberListDeleteBtn?.isVisible =
            isDeleteMode && numberList?.find { it.isCheckedForDelete }?.isNotNull().isTrue()
        if (isDeleteMode.not()) {
            numberList?.forEach {
                it.isCheckedForDelete = false
            }
            binding?.numberListDeleteAll?.isChecked = false
        }
        (adapter as NumberAdapter).apply {
            isDeleteMode = this@NumberListFragment.isDeleteMode
            notifyDataSetChanged()
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
            numberListLiveData.safeObserve(viewLifecycleOwner) { numberList ->
                this@NumberListFragment.numberList = numberList as ArrayList<Number>
                if (!checkDataListEmptiness(numberList)) {
                    getHashMapFromNumberList(numberList)
                }
            }
            numberHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { whiteNumberList ->
                whiteNumberList?.let { setDataList(it) }
            }
            successDeleteNumberLiveData.safeSingleObserve(viewLifecycleOwner) {
                this@NumberListFragment.numberList?.removeAll { it.isCheckedForDelete }
                changeDeleteMode()
                searchDataList()
            }
        }
    }

    override fun searchDataList() {
        val filteredNumberList = numberList?.filter { number ->
            number.number.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ) && (if (selectedFilterItems[0]) number.contain else true)
                    && (if (selectedFilterItems[1]) number.start else true)
                    && if (selectedFilterItems[2]) number.end else true
        }.orEmpty()
        if (!checkDataListEmptiness(filteredNumberList)) {
            viewModel.getHashMapFromNumberList(filteredNumberList)
        }
    }

    override fun getData() {
        viewModel.getWhiteNumberList(this is BlackNumberListFragment)
    }
}

