package com.tarasovvp.smartblocker.presentation.main.number.details

import android.os.Bundle
import androidx.core.view.isVisible
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSingleNumberDataDetailsBinding
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_TYPE
import com.tarasovvp.smartblocker.presentation.base.BaseBindingFragment
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel

class SingleDetailsFragment :
    BaseBindingFragment<FragmentSingleNumberDataDetailsBinding>() {

    override var layoutId = R.layout.fragment_single_number_data_details

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataClickListener: NumberDataClickListener? = null

    fun updateNumberDataList(numberDataUIModelList: ArrayList<NumberDataUIModel>, isFilteredCallItemDisable: Boolean = false) {
        val numberType = arguments?.getString(NUMBER_TYPE)
        binding?.apply {
            singleDetailsList.adapter =
                numberDataAdapter ?: NumberDataAdapter(numberDataUIModelList) { numberData ->
                    numberDataClickListener?.onNumberDataClick(numberData)
                }.apply {
                    this.isFilteredCallDetails = numberType == CallWithFilterUIModel::class.simpleName
                    this.isFilteredCallItemDisable = isFilteredCallItemDisable
                }
            singleDetailsList.contentDescription = when (numberType) {
                FilterWithFilteredNumberUIModel::class.simpleName -> getString(R.string.list_blocker)
                NumberDataUIModel::class.simpleName -> getString(R.string.list_contact)
                else -> getString(R.string.list_call)
            }
            singleDetailsListEmpty.setDescription( when (numberType) {
                FilterWithFilteredNumberUIModel::class.simpleName -> EmptyState.EMPTY_STATE_NUMBERS.description()
                NumberDataUIModel::class.simpleName -> EmptyState.EMPTY_STATE_FILTERS.description()
                else -> EmptyState.EMPTY_STATE_FILTERED_CALLS.description()
            })
            singleDetailsListEmpty.isVisible = numberDataUIModelList.isEmpty()
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