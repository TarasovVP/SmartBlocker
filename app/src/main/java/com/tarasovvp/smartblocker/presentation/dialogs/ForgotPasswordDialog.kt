package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.EMAIL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FORGOT_PASSWORD
import com.tarasovvp.smartblocker.databinding.DialogForgotPasswordBinding
import com.tarasovvp.smartblocker.utils.extensions.inputText
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.presentation.base.BaseDialog

class ForgotPasswordDialog : BaseDialog<DialogForgotPasswordBinding>() {

    override var layoutId = R.layout.dialog_forgot_password

    private val args: ForgotPasswordDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            forgotPassEmailInput.setText(args.email)
            forgotPasswordCancel.setSafeOnClickListener {
                dismiss()
            }
            setConfirmButton()
        }
    }

    private fun setConfirmButton() {
        binding?.apply {
            isInactive = forgotPassEmailInput.text.isNullOrEmpty()
            forgotPassEmailInput.doAfterTextChanged {
                isInactive = it.isNullOrEmpty()
            }
            forgotPasswordConfirm.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(FORGOT_PASSWORD,
                    bundleOf(EMAIL to forgotPassEmailInput.inputText()))
            }
        }
    }
}