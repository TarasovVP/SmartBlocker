package com.tarasovvp.smartblocker.ui.dialogs

import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.SETTINGS_REVIEW
import com.tarasovvp.smartblocker.databinding.DialogSettingsReviewBinding
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class SettingsReviewDialog :
    BaseDialog<DialogSettingsReviewBinding>() {

    override var layoutId = R.layout.dialog_settings_review

    override fun initUI() {
        binding?.apply {
            settingsReviewInput.doAfterTextChanged {
                binding?.settingsReviewSend?.isEnabled = it.toString().isNotBlank()
            }
            settingsReviewCancel.setSafeOnClickListener {
                dismiss()
            }
            settingsReviewSend.setSafeOnClickListener {
                dismiss()
                setFragmentResult(SETTINGS_REVIEW,
                    bundleOf(SETTINGS_REVIEW to settingsReviewInput.text.toString()))
            }
        }
    }
}