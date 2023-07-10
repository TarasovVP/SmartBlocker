package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CANCEL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.EXIST_ACCOUNT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ID_TOKEN
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class ExistAccountDialog : BaseDialog<DialogConfirmBinding>() {

    override var layoutId = R.layout.dialog_confirm

    private val args: ExistAccountDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            dialogConfirmTitle.text = args.description
            dialogConfirmCancel.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(EXIST_ACCOUNT, bundleOf(ID_TOKEN to CANCEL))
            }
            dialogConfirmSubmit.text = getString(R.string.button_ok)
            dialogConfirmSubmit.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(EXIST_ACCOUNT, bundleOf(ID_TOKEN to args.idToken))
            }
        }
    }
}