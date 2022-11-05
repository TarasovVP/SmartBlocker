package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.blacklister.databinding.DialogFilterConditionBinding
import com.tarasovvp.blacklister.enums.FilterCondition
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class FilterConditionsDialog : BaseDialog<DialogFilterConditionBinding>() {

    override var layoutId = R.layout.dialog_filter_condition

    private val args: FilterConditionsDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            args.filterConditionList?.let { filterConditionList ->
                dialogFilterConditionFull.isChecked =
                    filterConditionList.contains(FilterCondition.FILTER_CONDITION_FULL.index)
                dialogFilterConditionStart.isChecked =
                    filterConditionList.contains(FilterCondition.FILTER_CONDITION_START.index)
                dialogFilterConditionContain.isChecked =
                    filterConditionList.contains(FilterCondition.FILTER_CONDITION_CONTAIN.index)
            }
            dialogForgotPasswordCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogForgotPasswordConfirm.setSafeOnClickListener {
                setFragmentResult(FILTER_CONDITION_LIST,
                    bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                        if (dialogFilterConditionFull.isChecked) add(FilterCondition.FILTER_CONDITION_FULL.index)
                        if (dialogFilterConditionStart.isChecked) add(FilterCondition.FILTER_CONDITION_START.index)
                        if (dialogFilterConditionContain.isChecked) add(FilterCondition.FILTER_CONDITION_CONTAIN.index)
                    }))
                dismiss()
            }
        }
    }
}