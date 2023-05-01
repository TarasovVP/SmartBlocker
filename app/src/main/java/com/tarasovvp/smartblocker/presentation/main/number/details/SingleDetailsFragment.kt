package com.tarasovvp.smartblocker.presentation.main.number.details

import android.os.Bundle
import androidx.core.view.isVisible
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSingleNumberDataDetailsBinding
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.presentation.base.BaseBindingFragment

class SingleDetailsFragment :
    BaseBindingFragment<FragmentSingleNumberDataDetailsBinding>() {

    override var layoutId = R.layout.fragment_single_number_data_details

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataClickListener: NumberDataClickListener? = null

    fun updateNumberDataList(numberDataList: ArrayList<NumberData>, isFilteredCallItemDisable: Boolean = false) {
        val numberType = arguments?.getString(NUMBER_TYPE)
        binding?.apply {
            singleDetailsList.adapter =
                numberDataAdapter ?: NumberDataAdapter(numberDataList) { numberData ->
                    numberDataClickListener?.onNumberDataClick(numberData)
                }.apply {
                    this.isFilteredCallDetails = numberType == FilteredCallWithFilter::class.simpleName
                    this.isFilteredCallItemDisable = isFilteredCallItemDisable
                }
            singleDetailsListEmpty.setDescription( when (numberType) {
                Filter::class.simpleName -> EmptyState.EMPTY_STATE_NUMBERS.description()
                NumberData::class.simpleName -> EmptyState.EMPTY_STATE_FILTERS.description()
                else -> EmptyState.EMPTY_STATE_FILTERED_CALLS.description()
            })
            singleDetailsListEmpty.isVisible = numberDataList.isEmpty()
        }
    }

    fun setNumberDataClick(numberDataClickListener: NumberDataClickListener) {
        this.numberDataClickListener = numberDataClickListener
    }

    companion object {
        @JvmStatic
        fun newInstance(numberType: String): SingleDetailsFragment {
            return SingleDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(NUMBER_TYPE, numberType)
                }
            }
        }
    }
}