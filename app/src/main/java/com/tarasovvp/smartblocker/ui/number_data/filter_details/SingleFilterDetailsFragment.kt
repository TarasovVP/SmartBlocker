package com.tarasovvp.smartblocker.ui.number_data.filter_details

import androidx.core.view.isVisible
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSingleFilterDetailsBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment
import com.tarasovvp.smartblocker.ui.number_data.NumberDataAdapter

class SingleFilterDetailsFragment(var isFilteredCallDetails: Boolean = false, var numberDataResult: (NumberData) -> Unit) :
    BaseBindingFragment<FragmentSingleFilterDetailsBinding>() {

    override var layoutId = R.layout.fragment_single_filter_details

    private var numberDataAdapter: NumberDataAdapter? = null

    fun updateNumberDataList(numberDataList: ArrayList<NumberData>) {
        binding?.apply {
            singleFilterDetailsList.adapter =
                numberDataAdapter ?: NumberDataAdapter(numberDataList) { numberData ->
                    numberDataResult.invoke(numberData)
                }
            filterDetailsNumberListEmpty.emptyState =
                EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_BLOCKER
            filterDetailsNumberListEmpty.root.isVisible = numberDataList.isEmpty()
        }
    }
}