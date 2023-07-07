package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DELETE_USER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LOG_OUT
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class AccountActionDialog : BaseDialog<DialogConfirmBinding>() {

    private val args: AccountActionDialogArgs by navArgs()

    override var layoutId = R.layout.dialog_confirm

    override fun initUI() {
        binding?.apply {
            dialogConfirmTitle.text =
                when {
                    args.isLogOut && args.isAuthorised -> getString(R.string.settings_account_log_out)
                    args.isLogOut -> getString(R.string.settings_account_unauthorised_log_out)
                    else -> getString(R.string.settings_account_delete)
                }
            dialogConfirmCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogConfirmSubmit.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(if (args.isLogOut) LOG_OUT else DELETE_USER, bundleOf())
            }
        }
    }
}