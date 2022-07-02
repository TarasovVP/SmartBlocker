package com.tarasovvp.blacklister.ui.dialogs

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_USER
import com.tarasovvp.blacklister.constants.Constants.LOG_OUT
import com.tarasovvp.blacklister.databinding.DialogInfoBinding
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class AccountActionDialog : BaseDialog<DialogInfoBinding>() {

    private val args: AccountActionDialogArgs by navArgs()

    override fun getViewBinding() = DialogInfoBinding.inflate(layoutInflater)

    override fun initUI() {
        binding?.dialogInfoTitle?.text = if (args.isLogOut) getString(R.string.log_out) else getString(R.string.delete_account)
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(if (args.isLogOut) LOG_OUT else DELETE_USER, true)
            dismiss()
        }
    }
}