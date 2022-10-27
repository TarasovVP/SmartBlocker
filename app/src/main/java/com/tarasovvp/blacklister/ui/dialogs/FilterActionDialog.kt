package com.tarasovvp.blacklister.ui.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.DialogInfoBinding
import com.tarasovvp.blacklister.enums.FilterAction
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.ui.base.BaseDialog
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class FilterActionDialog : BaseDialog<DialogInfoBinding>() {

    override var layoutId = R.layout.dialog_info

    private val args: FilterActionDialogArgs by navArgs()

    override fun initUI() {
        val filterActionText = when(args.filterAction) {
            FilterAction.FILTER_ACTION_ADD.name -> "Добавить ${args.filter?.filter.orEmpty()} в ${getString(if (args.filter?.isBlackFilter().isTrue()) R.string.black_list else R.string.white_list)}"
            FilterAction.FILTER_ACTION_DELETE.name -> "Удалить ${args.filter?.filter.orEmpty()} из ${getString(if (args.filter?.isBlackFilter().isTrue()) R.string.black_list else R.string.white_list)}"
            FilterAction.FILTER_ACTION_CHANGE.name -> "Переместить ${args.filter?.filter.orEmpty()} в ${getString(if (args.filter?.isBlackFilter().isTrue()) R.string.white_list else R.string.black_list)}"
            else -> "Перейти на ${getString(if (args.filter?.isBlackFilter().isTrue()) R.string.white_list else R.string.black_list)}"
        }
        binding?.dialogInfoTitle?.text = filterActionText
        binding?.dialogInfoCancel?.setSafeOnClickListener {
            dismiss()
        }
        binding?.dialogInfoConfirm?.setSafeOnClickListener {
            setFragmentResult(args.filterAction.orEmpty(), bundleOf())
            dismiss()
        }
    }
}