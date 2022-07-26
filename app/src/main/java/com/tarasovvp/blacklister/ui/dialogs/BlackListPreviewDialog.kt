package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.constants.Constants.BLACK_LIST_PREVIEW
import com.tarasovvp.blacklister.databinding.DialogBlackListPreviewBinding
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class BlackListPreviewDialog : BaseDialog<DialogBlackListPreviewBinding>() {

    override fun getViewBinding() = DialogBlackListPreviewBinding.inflate(layoutInflater)

    private val args: BlackListPreviewDialogArgs by navArgs()

    override fun initUI() {
        binding?.dialogBlackListPreviewTitle?.text = args.title
        binding?.dialogBlackListPreviewSubTitle?.text = args.numberList
        binding?.dialogBlackListPreviewCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogBlackListPreviewConfirm?.setSafeOnClickListener {
            setFragmentResult(BLACK_LIST_PREVIEW, bundleOf())
            dismiss()
        }
    }
}