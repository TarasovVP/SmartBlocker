package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogDeleteAccountBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CURRENT_PASSWORD
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DELETE_ACCOUNT
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.utils.extensions.inputText
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class DeleteAccountDialog : BaseDialog<DialogDeleteAccountBinding>() {

    override var layoutId = R.layout.dialog_delete_account

    override fun initUI() {
        binding?.deleteAccountCancel?.setSafeOnClickListener {
            dismiss()
        }
        setConfirmButton()
    }

    private fun setConfirmButton() {
        binding?.apply {
            isInactive = binding?.deleteAccountCurrentInput?.text?.isBlank()
                binding?.deleteAccountCurrentInput?.doAfterTextChanged {
                    isInactive = it?.isBlank()
            }
            deleteAccountConfirm.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(
                    DELETE_ACCOUNT,
                    bundleOf(CURRENT_PASSWORD to binding?.deleteAccountCurrentInput?.inputText()))
            }
        }
    }
}