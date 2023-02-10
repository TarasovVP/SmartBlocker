package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.databinding.DialogFilterActionBinding
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.ui.base.BaseDialog

class FilterActionDialog : BaseDialog<DialogFilterActionBinding>() {

    override var layoutId = R.layout.dialog_filter_action

    private val args: FilterActionDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            filter = args.filter
            filterActionCancel.setSafeOnClickListener {
                dismiss()
            }
            filterActionConfirm.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(FILTER_ACTION, bundleOf(FILTER_ACTION to filter?.filterAction))
            }
        }
    }
}