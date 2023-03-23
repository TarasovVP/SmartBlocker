package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_EXIT
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.presentation.base.BaseDialog

class AppExitDialog : BaseDialog<DialogConfirmBinding>() {

    override var layoutId = R.layout.dialog_confirm

    override fun initUI() {
        binding?.dialogConfirmTitle?.text = getString(R.string.app_exit)
        binding?.dialogConfirmCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogConfirmSubmit?.setSafeOnClickListener {
            findNavController().navigateUp()
            setFragmentResult(APP_EXIT, bundleOf())
        }
    }
}