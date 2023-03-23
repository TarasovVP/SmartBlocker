package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.view.isVisible
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.presentation.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseDialog

class UnavailableNetworkDialog : BaseDialog<DialogConfirmBinding>() {

    override var layoutId = R.layout.dialog_confirm

    override fun initUI() {
        binding?.dialogConfirmTitle?.text = getString(R.string.authorization_network_unavailable)
        binding?.dialogConfirmSubmit?.isVisible = false
        binding?.dialogConfirmCancel?.text = getString(R.string.button_ok)
        binding?.dialogConfirmCancel?.setSafeOnClickListener {
            dismiss()
            (activity as MainActivity).finish()
        }
    }
}