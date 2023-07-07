package com.tarasovvp.smartblocker.presentation.main.settings.settings_sign_up

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsSignUpBinding
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsSignUpFragment : BaseFragment<FragmentSettingsSignUpBinding, SettingSignUpViewModel>() {

    override var layoutId = R.layout.fragment_settings_sign_up
    override val viewModelClass = SettingSignUpViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide()
        (binding?.root as? ViewGroup)?.hideKeyboardWithLayoutTouch()
        setContinueButton(binding?.container?.getViewsFromLayout(EditText::class.java))
        binding?.settingsTransferDataSwitch?.setOnCheckedChangeListener { _, isChecked ->
            binding?.settingsTransferDataDescribe?.text = getString( if (isChecked) R.string.settings_account_transfer_data_turn_on else R.string.settings_account_transfer_data_turn_off)
        }
    }

    private fun setContinueButton(editTextList: ArrayList<EditText>?) {
        binding?.apply {
            isInactive = editTextList?.any { it.text.isNullOrEmpty() }.isTrue()
            editTextList?.onEach { editText ->
                editText.doAfterTextChanged {
                    isInactive = editTextList.any { it.text.isNullOrEmpty() }.isTrue()
                }
            }
            binding?.settingsSignUpContinue?.setSafeOnClickListener {
                binding?.root?.hideKeyboard()
                viewModel.createUserWithEmailAndPassword(binding?.settingsSignUpEmail.inputText(),
                    binding?.settingsSignUpPassword.inputText())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignUpLiveData.safeSingleObserve(viewLifecycleOwner) {
                viewModel.createCurrentUser(CurrentUser())
            }
            createCurrentUserLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as? MainActivity)?.apply {
                    getAllData(true)
                    startBlocker()
                }
                findNavController().navigate(R.id.listBlockerFragment)
            }
        }
    }
}