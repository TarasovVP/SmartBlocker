package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.UNAUTHORIZED_ENTER
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class UnauthorizedEnterDialog : BaseDialog<DialogConfirmBinding>() {

    override var layoutId = R.layout.dialog_confirm

    override fun initUI() {
        binding?.apply {
            dialogConfirmTitle.text = getString(R.string.authorization_unauthorized_enter)
            dialogConfirmCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogConfirmSubmit.text = getString(R.string.authorization_unauthorized_enter_button)
            dialogConfirmSubmit.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(UNAUTHORIZED_ENTER, bundleOf())
            }
        }
    }
}