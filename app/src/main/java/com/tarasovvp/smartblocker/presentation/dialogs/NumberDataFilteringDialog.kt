package com.tarasovvp.smartblocker.presentation.dialogs

import android.view.LayoutInflater
import android.widget.CheckBox
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogNumberDataFilteringBinding
import com.tarasovvp.smartblocker.databinding.ItemCheckBoxBinding
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.utils.extensions.getViewsFromLayout
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class NumberDataFilteringDialog : BaseDialog<DialogNumberDataFilteringBinding>() {
    override var layoutId = R.layout.dialog_number_data_filtering

    private val args: NumberDataFilteringDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            val numberDataFiltering =
                when (args.previousDestinationId) {
                    R.id.listCallFragment -> NumberDataFiltering.values().sliceArray(3..4)
                    R.id.listContactFragment -> NumberDataFiltering.values().sliceArray(5..6)
                    else -> NumberDataFiltering.values().sliceArray(0..2)
                }
            dialogNumberDataFilteringContainer.removeAllViews()
            numberDataFiltering.forEach { dataFiltering ->
                val checkBox = ItemCheckBoxBinding.inflate(LayoutInflater.from(context))
                checkBox.itemCheckBox.setText(dataFiltering.title())
                checkBox.itemCheckBox.tag = dataFiltering.ordinal
                checkBox.itemCheckBox.isChecked =
                    args.filteringList?.contains(dataFiltering.ordinal).isTrue()
                dialogNumberDataFilteringContainer.addView(checkBox.root)
            }
            dialogNumberDataFilteringCancel.setSafeOnClickListener {
                dismiss()
            }
            dialogNumberDataFilteringConfirm.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(
                    FILTER_CONDITION_LIST,
                    bundleOf(
                        FILTER_CONDITION_LIST to
                            arrayListOf<Int>().apply {
                                dialogNumberDataFilteringContainer.getViewsFromLayout(CheckBox::class.java)
                                    .forEach { checkBox ->
                                        if (checkBox.isChecked) add(checkBox.tag.toString().toInt())
                                    }
                            },
                    ),
                )
            }
        }
    }
}
