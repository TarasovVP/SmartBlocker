package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_NUMBER
import com.tarasovvp.blacklister.databinding.DialogInfoBinding
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class DeleteNumberDialog : BaseDialog<DialogInfoBinding>() {

    override fun getViewBinding() = DialogInfoBinding.inflate(layoutInflater)

    private val args: DeleteNumberDialogArgs by navArgs()

    override fun initUI() {
        binding?.dialogInfoTitle?.text =
            String.format(getString(R.string.delete), args.number?.number.orEmpty())
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            setFragmentResult(DELETE_NUMBER, bundleOf())
            dismiss()
        }
    }
}