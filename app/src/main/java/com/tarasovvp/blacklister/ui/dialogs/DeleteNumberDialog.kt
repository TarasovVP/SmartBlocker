package com.tarasovvp.blacklister.ui.dialogs

import androidx.navigation.fragment.findNavController
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
        binding?.dialogInfoTitle?.text = String.format(getString(R.string.delete),
            if (args.blackNumber?.number.isNullOrEmpty()) args.whiteNumber?.number.orEmpty() else args.blackNumber?.number.orEmpty())
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(DELETE_NUMBER, true)
            dismiss()
        }
    }
}