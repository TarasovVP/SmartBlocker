package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.constants.Constants.EMAIL
import com.tarasovvp.blacklister.constants.Constants.FORGOT_PASSWORD
import com.tarasovvp.blacklister.databinding.DialogForgotPasswordBinding
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ForgotPasswordDialog : BaseDialog<DialogForgotPasswordBinding>() {

    override fun getViewBinding() = DialogForgotPasswordBinding.inflate(layoutInflater)

    private val args: ForgotPasswordDialogArgs by navArgs()

    override fun initUI() {
        binding?.dialogForgotPasswordInput?.setText(args.email)
        binding?.dialogForgotPasswordCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogForgotPasswordConfirm?.setSafeOnClickListener {
            setFragmentResult(FORGOT_PASSWORD,
                bundleOf(EMAIL to binding?.dialogForgotPasswordInput?.text.toString()))
            dismiss()
        }
    }
}