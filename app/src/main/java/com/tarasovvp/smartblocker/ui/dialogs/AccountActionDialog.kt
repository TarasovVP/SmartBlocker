package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.DELETE_USER
import com.tarasovvp.smartblocker.constants.Constants.LOG_OUT
import com.tarasovvp.smartblocker.databinding.DialogInfoBinding
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class AccountActionDialog : BaseDialog<DialogInfoBinding>() {

    private val args: AccountActionDialogArgs by navArgs()

    override var layoutId = R.layout.dialog_info

    override fun initUI() {
        binding?.dialogInfoTitle?.text =
            if (args.isLogOut) getString(R.string.log_out) else getString(R.string.delete_account)
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            dismiss()
            setFragmentResult(if (args.isLogOut) LOG_OUT else DELETE_USER, bundleOf())
        }
    }
}