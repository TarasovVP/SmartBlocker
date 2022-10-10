package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.view.isVisible
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.DialogInfoBinding
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

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