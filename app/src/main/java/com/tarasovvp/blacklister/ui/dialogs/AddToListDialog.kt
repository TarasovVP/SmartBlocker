package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.constants.Constants.ADD_TO_LIST
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST
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
            findNavController().navigateUp()
            setFragmentResult(ADD_TO_LIST,
                bundleOf(WHITE_LIST to binding?.dialogAddToListPriority?.isChecked))
        }
    }
}