package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CALL_DELETE
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.presentation.base.BaseDialog

class FilteredCallDeleteDialog : BaseDialog<DialogConfirmBinding>() {

    override var layoutId = R.layout.dialog_confirm

    private val args: FilteredCallDeleteDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            dialogConfirmTitle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_delete, 0, 0)
            dialogConfirmTitle.text = args.callDelete
            dialogConfirmCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogConfirmSubmit.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(CALL_DELETE, bundleOf(CALL_DELETE to true))
            }
        }
    }
}