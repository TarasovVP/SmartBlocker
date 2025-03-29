package com.tarasovvp.smartblocker.presentation.dialogs

import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.DialogChangePasswordBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CHANGE_PASSWORD
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CURRENT_PASSWORD
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NEW_PASSWORD
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.utils.extensions.getViewsFromLayout
import com.tarasovvp.smartblocker.utils.extensions.inputText
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class ChangePasswordDialog : BaseDialog<DialogChangePasswordBinding>() {
    override var layoutId = R.layout.dialog_change_password

    override fun initUI() {
        binding?.changePasswordCancel?.setSafeOnClickListener {
            dismiss()
        }
        setConfirmButton(binding?.container?.getViewsFromLayout(EditText::class.java))
    }

    private fun setConfirmButton(editTextList: ArrayList<EditText>?) {
        binding?.apply {
            isInactive = editTextList?.any { it.text.isNullOrEmpty() }.isTrue()
            editTextList?.onEach { editText ->
                editText.doAfterTextChanged {
                    isInactive = editTextList.any { it.text.isNullOrEmpty() }.isTrue()
                }
            }
            changePasswordConfirm.setSafeOnClickListener {
                findNavController().navigateUp()
                setFragmentResult(
                    CHANGE_PASSWORD,
                    bundleOf(
                        CURRENT_PASSWORD to changePasswordCurrentInput.inputText(),
                        NEW_PASSWORD to changePasswordNewInput.inputText(),
                    ),
                )
            }
        }
    }
}
