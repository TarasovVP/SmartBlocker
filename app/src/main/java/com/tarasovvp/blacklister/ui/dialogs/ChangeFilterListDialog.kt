package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.CHANGE_FILTER
import com.tarasovvp.blacklister.databinding.DialogInfoBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ChangeFilterListDialog : BaseDialog<DialogInfoBinding>() {

    override var layoutId = R.layout.dialog_info

    private val args: ChangeFilterListDialogArgs by navArgs()

    override fun initUI() {
        binding?.dialogInfoTitle?.text = String.format(getString(R.string.change_filter),
            getString(if (args.filter?.isBlackFilter()
                    .isTrue()
            ) R.string.white_list else R.string.black_list))
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            setFragmentResult(CHANGE_FILTER, bundleOf())
            dismiss()
        }
    }
}