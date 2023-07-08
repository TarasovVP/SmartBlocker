package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogDeleteAccountBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CURRENT_PASSWORD
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DELETE_ACCOUNT
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.utils.extensions.inputText
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class DeleteAccountDialog : BaseDialog<DialogDeleteAccountBinding>() {

    override var layoutId = R.layout.dialog_delete_account

    private val args: DeleteAccountDialogArgs by navArgs()

    override fun initUI() {
        binding?.deleteAccountCancel?.setSafeOnClickListener {
            dismiss()
        }
        setConfirmButton()
    }

    private fun setConfirmButton() {
        binding?.apply {
            deleteAccountTitle.text = getString( if (args.isGoogleAuth) R.string.settings_account_google_delete else R.string.settings_account_email_delete)
            isInactive = args.isGoogleAuth.not() && binding?.deleteAccountCurrentInput?.text?.isBlank().isTrue()
            deleteAccountCurrentContainer.isVisible = args.isGoogleAuth.not()
            deleteAccountCurrentInput.doAfterTextChanged {
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