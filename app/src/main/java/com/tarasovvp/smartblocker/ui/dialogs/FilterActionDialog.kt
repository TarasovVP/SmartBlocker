package com.tarasovvp.smartblocker.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.databinding.DialogFilterActionBinding
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class FilterActionDialog : BottomSheetDialogFragment() {

    private val args: FilterActionDialogArgs by navArgs()

    private var binding: DialogFilterActionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_filter_action, container, false
        )
        initUI()
        return binding?.root
    }

    private fun initUI() {
        binding?.apply {
            filterAction = args.filterAction
            filterNumber = args.filterNumber
            root.setSafeOnClickListener {
                setFragmentResult(FILTER_ACTION, bundleOf(FILTER_ACTION to filterAction))
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}