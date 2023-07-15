package com.tarasovvp.smartblocker.presentation.dialogs

import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogSettingsFeedbackBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SETTINGS_FEEDBACK
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class SettingsFeedbackDialog :
    BaseDialog<DialogSettingsFeedbackBinding>() {

    override var layoutId = R.layout.dialog_settings_feedback

    override fun initUI() {
        binding?.apply {
            settingsFeedbackInput.doAfterTextChanged {
                binding?.settingsFeedbackSend?.isEnabled = it.toString().isNotBlank()
            }
            settingsFeedbackCancel.setSafeOnClickListener {
                dismiss()
            }
            setConfirmButton()
        }
    }

    private fun setConfirmButton() {
        binding?.apply {
            isInactive = settingsFeedbackInput.text.isNullOrEmpty()
            settingsFeedbackInput.doAfterTextChanged {
                isInactive = it.isNullOrEmpty()
            }
            settingsFeedbackSend.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(SETTINGS_FEEDBACK,
                    bundleOf(SETTINGS_FEEDBACK to settingsFeedbackInput.text.toString()))
            }
        }
    }
}