package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LOG_OUT
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class LogOutDialog : BaseDialog<DialogConfirmBinding>() {

    private val args: LogOutDialogArgs by navArgs()

    override var layoutId = R.layout.dialog_confirm

    override fun initUI() {
        binding?.apply {
            dialogConfirmTitle.text =
                when {
                    args.isAuthorised -> getString(R.string.settings_account_log_out)
                    else -> getString(R.string.settings_account_unauthorised_log_out)
                }
            dialogConfirmCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogConfirmSubmit.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(LOG_OUT, bundleOf())
            }
        }
    }
}