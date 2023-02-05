package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.CALL_DELETE
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.databinding.DialogConfirmBinding
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.ui.base.BaseDialog

class BlockedCallDeleteDialog : BaseDialog<DialogConfirmBinding>() {

    override var layoutId = R.layout.dialog_confirm

    private val args: BlockedCallDeleteDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            dialogConfirmTitle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_delete, 0, 0)
            dialogConfirmTitle.text = args.callDelete
            dialogConfirmCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogConfirmSubmit.setSafeOnClickListener {
                dismiss()
                setFragmentResult(FILTER_ACTION, bundleOf(CALL_DELETE to true))
            }
        }
    }
}