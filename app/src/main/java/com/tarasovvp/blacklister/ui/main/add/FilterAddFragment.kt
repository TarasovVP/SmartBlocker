package com.tarasovvp.blacklister.ui.main.add

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterAddBinding
import com.tarasovvp.blacklister.extensions.getViewsFromLayout
import com.tarasovvp.blacklister.extensions.inputText
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.BlackFilter
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.WhiteFilter


class FilterAddFragment(private var filter: Filter?) :
    BaseAddFragment<FragmentFilterAddBinding>(filter) {

    override var layoutId = R.layout.fragment_filter_add

    override val viewModelClass = AddViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.filter = filter
        setCheckChangeListeners(filter)
    }

    override fun initViews() {
        binding?.apply {
            title = filterAddTitle
            icon = filterAddIcon
            filterInput = filterAddInput
            submitButton = filterAddSubmit
            contactByFilterList = filterAddContactByFilterList
        }
    }

    private fun setCheckChangeListeners(filter: Filter?) {
        binding?.apply {
            val checkChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
                filterAddSubmit.isVisible =
                    filterAddInput.inputText().isNotEmpty() && filter?.isFilterIdentical(filterAddStart.isChecked,
                        filterAddContain.isChecked,
                        filterAddEnd.isChecked).isTrue().not()
                if (filterAddInput.inputText().isNotEmpty() && filter?.isFilterIdentical(filterAddStart.isChecked,
                        filterAddContain.isChecked,
                        filterAddEnd.isChecked).isTrue().not()
                ) {
                    viewModel.checkContactListByFilter(getFilterObject())
                }
            }
            container.getViewsFromLayout(CheckBox::class.java).forEach { checkBox ->
                checkBox.setOnCheckedChangeListener(checkChangeListener)
            }
        }
    }

    private fun getFilterObject(): Filter {
        val filter = if (filter?.isBlackFilter.isTrue()) {
            BlackFilter(filter = binding?.filterAddInput.inputText())
        } else {
            WhiteFilter(filter = binding?.filterAddInput.inputText())
        }
        filter.apply {
            start = binding?.filterAddStart?.isChecked.isTrue()
            contain = binding?.filterAddContain?.isChecked.isTrue()
            end = binding?.filterAddEnd?.isChecked.isTrue()
            isBlackFilter = filter.isBlackFilter.isTrue()
        }
        return filter
    }
}