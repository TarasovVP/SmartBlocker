package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.APP_EXIT
import com.tarasovvp.blacklister.databinding.DialogInfoBinding
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class AppExitDialog : BaseDialog<DialogInfoBinding>() {

    override fun getViewBinding() = DialogInfoBinding.inflate(layoutInflater)

    override fun initUI() {
        binding?.dialogInfoTitle?.text = getString(R.string.exit_application)
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            setFragmentResult(APP_EXIT, bundleOf())
            dismiss()
        }
    }
}