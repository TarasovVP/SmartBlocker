package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_LIST_PREVIEW
import com.tarasovvp.blacklister.databinding.DialogBlackListPreviewBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class BlackListPreviewDialog : BaseDialog<DialogBlackListPreviewBinding>() {

    override fun getViewBinding() = DialogBlackListPreviewBinding.inflate(layoutInflater)

    private val args: BlackListPreviewDialogArgs by navArgs()

    override fun initUI() {
        val filteredContactList = args.contactList?.filterNot {
            if (args.number?.isBlackNumber.isTrue()) it.isWhiteList && SharedPreferencesUtil.isWhiteListPriority else it.isBlackList && SharedPreferencesUtil.isWhiteListPriority.not()
        }
        val filteredNotImplContactList = args.contactList?.filter {
            if (args.number?.isBlackNumber.isTrue()) it.isWhiteList && SharedPreferencesUtil.isWhiteListPriority else it.isBlackList && SharedPreferencesUtil.isWhiteListPriority.not()
        }
        binding?.dialogBlackListPreviewTitle?.isVisible = filteredContactList?.isNotEmpty().isTrue()
        binding?.dialogBlackListPreviewSubTitle?.isVisible =
            filteredContactList?.isNotEmpty().isTrue()
        binding?.dialogBlackListPreviewTitle?.text = "Фильтр ${args.number?.number} может ${
            if (args.number?.isBlackNumber.isTrue()) getString(R.string.can_block) else getString(R.string.can_unblock)
        } следующие контакты из списка контактов: "
        binding?.dialogBlackListPreviewSubTitle?.text =
            filteredContactList?.map { it.name }?.joinToString()
        binding?.dialogBlackListPreviewNotImplTitle?.isVisible =
            filteredNotImplContactList?.isNotEmpty().isTrue()
        binding?.dialogBlackListPreviewNotImplSubTitle?.isVisible =
            filteredNotImplContactList?.isNotEmpty().isTrue()
        binding?.dialogBlackListPreviewNotImplTitle?.text =
            "Фильтр ${args.number?.number} из-за другой приоритетности не может ${
                if (args.number?.isBlackNumber.isTrue()) getString(R.string.can_block) else getString(
                    R.string.can_unblock)
            } следующие контакты из списка контактов: "
        binding?.dialogBlackListPreviewNotImplSubTitle?.text =
            filteredNotImplContactList?.map { it.name }?.joinToString()
        binding?.dialogBlackListPreviewCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogBlackListPreviewConfirm?.setSafeOnClickListener {
            setFragmentResult(BLACK_LIST_PREVIEW, bundleOf())
            dismiss()
        }
    }
}