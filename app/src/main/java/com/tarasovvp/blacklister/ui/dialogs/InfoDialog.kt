package com.tarasovvp.blacklister.ui.dialogs

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.CONTACT
import com.tarasovvp.blacklister.constants.Constants.DELETE_NUMBER
import com.tarasovvp.blacklister.databinding.DialogInfoBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class InfoDialog : BaseDialog<DialogInfoBinding>() {

    override fun getViewBinding() = DialogInfoBinding.inflate(layoutInflater)

    private val args: InfoDialogArgs by navArgs()

    override fun initUI() {
        binding?.dialogInfoTitle?.text =
            if (args.blackNumber.isNotNull()) String.format(getString(R.string.delete),
                args.blackNumber?.number) else String.format(if (args.contact?.isBlackList.isTrue()) getString(
                R.string.delete_contact_from_black_list) else getString(R.string.add_contact_to_black_list),
                args.contact?.name)
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            if (args.contact.isNotNull()) args.contact?.isBlackList =
                args.contact?.isBlackList.isTrue().not()
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                if (args.blackNumber.isNotNull()) DELETE_NUMBER else CONTACT,
                if (args.blackNumber.isNotNull()) args.blackNumber else args.contact
            )
            dismiss()
        }
    }
}