package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMITTED_CALL
import com.tarasovvp.smartblocker.databinding.DialogNumberDataFilteringBinding
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.presentation.base.BaseDialog

class NumberDataFilteringDialog : BaseDialog<DialogNumberDataFilteringBinding>() {

    override var layoutId = R.layout.dialog_number_data_filtering

    private val args: NumberDataFilteringDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            isCallList = args.isCallList
            filteringList = ArrayList<Int>(args.filteringList?.toMutableList().orEmpty())
            dialogNumberDataCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogNumberDataConfirm.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(FILTER_CONDITION_LIST,
                    bundleOf(FILTER_CONDITION_LIST to arrayListOf<Int>().apply {
                        if (dialogNumberDataWithBlocker.isChecked) add(BLOCKER)
                        if (dialogNumberDataWithPermission.isChecked) add(PERMISSION)
                        if (dialogNumberDataByBlocker.isChecked) add(BLOCKED_CALL.toInt())
                        if (dialogNumberDataByPermission.isChecked) add(PERMITTED_CALL.toInt())
                    }))
            }
        }
    }
}