package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.APP_EXIT
import com.tarasovvp.smartblocker.databinding.DialogInfoBinding
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class AppExitDialog : BaseDialog<DialogInfoBinding>() {

    override var layoutId = R.layout.dialog_info

    override fun initUI() {
        binding?.dialogInfoTitle?.text = getString(R.string.exit_application)
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            dismiss()
            setFragmentResult(APP_EXIT, bundleOf())
        }
    }
}