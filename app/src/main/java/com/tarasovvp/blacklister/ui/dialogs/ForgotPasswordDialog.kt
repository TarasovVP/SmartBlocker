package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.EMAIL
import com.tarasovvp.blacklister.constants.Constants.FORGOT_PASSWORD
import com.tarasovvp.blacklister.databinding.DialogForgotPasswordBinding
import com.tarasovvp.blacklister.extensions.inputText
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ForgotPasswordDialog : BaseDialog<DialogForgotPasswordBinding>() {

    override var layoutId = R.layout.dialog_forgot_password

    private val args: ForgotPasswordDialogArgs by navArgs()

    override fun initUI() {
        binding?.dialogForgotPassEmailInput?.setText(args.email)
        binding?.dialogForgotPasswordCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogForgotPasswordConfirm?.setSafeOnClickListener {
            setFragmentResult(FORGOT_PASSWORD,
                bundleOf(EMAIL to binding?.dialogForgotPassEmailInput.inputText()))
            dismiss()
        }
    }
}