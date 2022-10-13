package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_FILTER
import com.tarasovvp.blacklister.databinding.DialogInfoBinding
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class DeleteFilterDialog : BaseDialog<DialogInfoBinding>() {

    override var layoutId = R.layout.dialog_info

    private val args: DeleteFilterDialogArgs by navArgs()

    override fun initUI() {
        binding?.dialogInfoTitle?.text =
            String.format(getString(R.string.delete), args.filter?.filter.orEmpty())
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            setFragmentResult(DELETE_FILTER, bundleOf())
            dismiss()
        }
    }
}