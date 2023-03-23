package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SETTINGS_REVIEW
import com.tarasovvp.smartblocker.databinding.DialogSettingsReviewBinding
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.presentation.base.BaseDialog

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
            setConfirmButton()
        }
    }

    private fun setConfirmButton() {
        binding?.apply {
            isInactive = settingsReviewInput.text.isNullOrEmpty()
            settingsReviewInput.doAfterTextChanged {
                isInactive = it.isNullOrEmpty()
            }
            settingsReviewSend.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(SETTINGS_REVIEW,
                    bundleOf(SETTINGS_REVIEW to settingsReviewInput.text.toString()))
            }
        }
    }
}