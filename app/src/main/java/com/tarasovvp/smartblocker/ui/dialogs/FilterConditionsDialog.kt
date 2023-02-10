package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.databinding.DialogFilterConditionBinding
import com.tarasovvp.smartblocker.enums.FilterCondition
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.ui.base.BaseDialog

class FilterConditionsDialog : BaseDialog<DialogFilterConditionBinding>() {

    override var layoutId = R.layout.dialog_filter_condition

    private val args: FilterConditionsDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            filteringList = ArrayList<Int>(args.filteringList?.toMutableList().orEmpty())
            dialogFilterConditionCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogFilterConditionConfirm.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(FILTER_CONDITION_LIST,
                    bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                        if (dialogFilterConditionFull.isChecked) add(FilterCondition.FILTER_CONDITION_FULL.index)
                        if (dialogFilterConditionStart.isChecked) add(FilterCondition.FILTER_CONDITION_START.index)
                        if (dialogFilterConditionContain.isChecked) add(FilterCondition.FILTER_CONDITION_CONTAIN.index)
                    }))
            }
        }
    }
}