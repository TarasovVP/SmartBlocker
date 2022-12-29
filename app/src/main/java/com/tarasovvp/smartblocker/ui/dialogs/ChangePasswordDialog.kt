package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.CHANGE_PASSWORD
import com.tarasovvp.smartblocker.constants.Constants.CURRENT_PASSWORD
import com.tarasovvp.smartblocker.constants.Constants.NEW_PASSWORD
import com.tarasovvp.smartblocker.databinding.DialogChangePasswordBinding
import com.tarasovvp.smartblocker.extensions.inputText
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class ChangePasswordDialog : BaseDialog<DialogChangePasswordBinding>() {

    override var layoutId = R.layout.dialog_change_password

    override fun initUI() {

        binding?.dialogForgotPasswordCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogForgotPasswordConfirm?.setSafeOnClickListener {
            dismiss()
            setFragmentResult(CHANGE_PASSWORD,
                bundleOf(CURRENT_PASSWORD to binding?.dialogForgotPasswordCurrentInput.inputText(),
                    NEW_PASSWORD to binding?.dialogForgotPasswordNewInput.inputText()))
        }
    }
}