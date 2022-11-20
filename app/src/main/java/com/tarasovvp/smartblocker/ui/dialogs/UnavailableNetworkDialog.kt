package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.view.isVisible
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogInfoBinding
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class UnavailableNetworkDialog : BaseDialog<DialogInfoBinding>() {

    override var layoutId = R.layout.dialog_info

    override fun initUI() {
        binding?.dialogInfoTitle?.text = getString(R.string.unavailable_network)
        binding?.dialogInfoConfirm?.isVisible = false
        binding?.dialogInfoCancel?.text = getString(R.string.ok)
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
            (activity as MainActivity).finish()
        }
    }
}