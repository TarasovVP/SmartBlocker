package com.tarasovvp.smartblocker.ui.main.number.details

import androidx.core.view.isVisible
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSingleNumberDataDetailsBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.FilteredCall
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment

class SingleDetailsFragment(
    var dataType: String = String.EMPTY,
    var numberDataResult: (NumberData) -> Unit,
) :
    BaseBindingFragment<FragmentSingleNumberDataDetailsBinding>() {

    override var layoutId = R.layout.fragment_single_number_data_details

    private var numberDataAdapter: NumberDataAdapter? = null

    fun updateNumberDataList(numberDataList: ArrayList<NumberData>) {
        binding?.apply {
            singleFilterDetailsList.adapter =
                numberDataAdapter ?: NumberDataAdapter(numberDataList) { numberData ->
                    numberDataResult.invoke(numberData)
                }.apply {
                    this.isFilteredCallDetails = dataType == FilteredCall::class.simpleName
                }
            filterDetailsNumberListEmpty.emptyState = when(dataType) {
                Filter::class.simpleName -> EmptyState.EMPTY_STATE_FILTERS
                NumberData::class.simpleName -> EmptyState.EMPTY_STATE_NUMBERS
                else -> EmptyState.EMPTY_STATE_FILTERED_CALLS
            }
            filterDetailsNumberListEmpty.root.isVisible = numberDataList.isEmpty()
        }
    }
}