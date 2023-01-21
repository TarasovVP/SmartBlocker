package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.EMAIL
import com.tarasovvp.smartblocker.constants.Constants.FORGOT_PASSWORD
import com.tarasovvp.smartblocker.databinding.DialogForgotPasswordBinding
import com.tarasovvp.smartblocker.extensions.inputText
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class ForgotPasswordDialog : BaseDialog<DialogForgotPasswordBinding>() {

    override var layoutId = R.layout.dialog_forgot_password

    private val args: ForgotPasswordDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            forgotPassEmailInput.setText(args.email)
            forgotPasswordCancel.setSafeOnClickListener {
                dismiss()
            }
            forgotPasswordConfirm.setSafeOnClickListener {
                dismiss()
                setFragmentResult(FORGOT_PASSWORD,
                    bundleOf(EMAIL to forgotPassEmailInput.inputText()))
            }
        }
    }
}