package com.tarasovvp.blacklister.ui.dialogs

import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.constants.Constants.ADD_TO_LIST
import com.tarasovvp.blacklister.databinding.DialogAddToListBinding
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class AddToListDialog : BaseDialog<DialogAddToListBinding>() {

    override fun getViewBinding() = DialogAddToListBinding.inflate(layoutInflater)

    override fun initUI() {
        binding?.dialogAddToListCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogAddToListConfirm?.setSafeOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(ADD_TO_LIST,
                binding?.dialogAddToListPriority?.isChecked)
            dismiss()
        }
    }
}