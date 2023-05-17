package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.databinding.DialogFilterActionBinding
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.presentation.base.BaseDialog

class FilterActionDialog : BaseDialog<DialogFilterActionBinding>() {

    override var layoutId = R.layout.dialog_filter_action

    private val args: FilterActionDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            filterWithFilteredNumberUIModel = args.filterWithFilteredNumberUIModel
            filterActionCancel.setSafeOnClickListener {
                dismiss()
            }
            filterActionConfirm.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(FILTER_ACTION, bundleOf(FILTER_ACTION to filterWithFilteredNumberUIModel?.filterAction))
            }
        }
    }
}