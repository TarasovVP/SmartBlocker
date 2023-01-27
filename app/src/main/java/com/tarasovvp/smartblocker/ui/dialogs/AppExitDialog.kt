package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.APP_EXIT
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class AppExitDialog : BaseDialog<DialogConfirmBinding>() {

    override var layoutId = R.layout.dialog_confirm

    override fun initUI() {
        binding?.dialogConfirmTitle?.text = getString(R.string.app_exit)
        binding?.dialogConfirmCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogConfirmSubmit?.setSafeOnClickListener {
            dismiss()
            setFragmentResult(APP_EXIT, bundleOf())
        }
    }
}