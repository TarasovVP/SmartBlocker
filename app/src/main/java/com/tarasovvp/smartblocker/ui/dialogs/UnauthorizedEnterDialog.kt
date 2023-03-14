package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.UNAUTHORIZED_ENTER
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.ui.base.BaseDialog

class UnauthorizedEnterDialog : BaseDialog<DialogConfirmBinding>() {

    override var layoutId = R.layout.dialog_confirm

    override fun initUI() {
        binding?.apply {
            dialogConfirmTitle.text = getString(R.string.unauthorized_enter)
            dialogConfirmCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogConfirmSubmit.text = getString(R.string.authorization_enter)
            dialogConfirmSubmit.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(UNAUTHORIZED_ENTER, bundleOf())
            }
        }
    }
}