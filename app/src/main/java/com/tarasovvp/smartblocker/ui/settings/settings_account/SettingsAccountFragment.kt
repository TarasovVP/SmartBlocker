package com.tarasovvp.smartblocker.ui.settings.settings_account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.DELETE_USER
import com.tarasovvp.smartblocker.databinding.FragmentSettingsAccountBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class SettingsAccountFragment :
    BaseFragment<FragmentSettingsAccountBinding, SettingsAccountViewModel>() {

    override var layoutId = R.layout.fragment_settings_account
    override val viewModelClass = SettingsAccountViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setFragmentResultListener(DELETE_USER) { _, _ ->
            viewModel.deleteUser()
        }

        setFragmentResultListener(Constants.LOG_OUT) { _, _ ->
            viewModel.signOut()
        }
    }

    private fun initViews() {
        binding?.apply {
            includeEmptyState.emptyState = EmptyState.EMPTY_STATE_ACCOUNT
            includeEmptyState.root.isVisible =
                BlackListerApp.instance?.isLoggedInUser().isNotTrue()
            loginButton.isVisible =
                BlackListerApp.instance?.isLoggedInUser().isNotTrue()
            accountDetailsMainTitle.text = String.format(getString(R.string.welcome),
                BlackListerApp.instance?.auth?.currentUser?.email)
            (root as ViewGroup).hideKeyboardWithLayoutTouch()
            setOnclickListeners()
            initNewPasswordSet(accountDetailsChangePasswordContainer.getViewsFromLayout(EditText::class.java))
        }
    }

    private fun setOnclickListeners() {
        binding?.apply {
            accountDetailsLogOut.setSafeOnClickListener {
                (activity as MainActivity).stopBlocker()
                findNavController().navigate(SettingsAccountFragmentDirections.startAccountActionDialog(
                    isLogOut = true))
            }
            accountDetailsDeleteBtn.setSafeOnClickListener {
                (activity as MainActivity).stopBlocker()
                findNavController().navigate(SettingsAccountFragmentDirections.startAccountActionDialog())
            }
            accountDetailsNewPasswordBtn.setSafeOnClickListener {
                if (accountDetailsNewPasswordCreate.inputText() == accountDetailsNewPasswordConfirm.text.toString()) {
                    viewModel.changePassword(accountDetailsCurrentPassword.inputText(),
                        accountDetailsNewPasswordConfirm.text.toString())
                } else {
                    showMessage(getString(R.string.passwords_different), true)
                }
            }
            loginButton.setSafeOnClickListener {
                findNavController().navigate(SettingsAccountFragmentDirections.startLoginFragment())
            }
        }
    }

    private fun initNewPasswordSet(editTextList: ArrayList<EditText>?) {
        binding?.apply {
            accountDetailsNewPasswordCheck.setOnCheckedChangeListener { _, isChecked ->
                accountDetailsChangePasswordContainer.isVisible = isChecked
                if (isChecked) {
                    editTextList?.onEach { it.text.clear() }
                }
            }
            accountDetailsNewPasswordBtn.isEnabled =
                editTextList?.none { it.text.isNullOrEmpty() }.isTrue()
            editTextList?.onEach { editText ->
                editText.doAfterTextChanged {
                    binding?.accountDetailsNewPasswordBtn?.isEnabled =
                        editTextList.none { it.text.isNullOrEmpty() }.isTrue()
                }
            }
            accountDetailsNewPasswordBtn.setSafeOnClickListener {
                if (accountDetailsNewPasswordCreate.inputText() == accountDetailsNewPasswordConfirm.inputText()) {
                    viewModel.changePassword(binding?.accountDetailsCurrentPassword.inputText(),
                        accountDetailsNewPasswordConfirm.text.toString())
                } else {
                    showMessage(getString(R.string.passwords_different), true)
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successLiveData.safeSingleObserve(viewLifecycleOwner) {
                SharedPreferencesUtil.clearAll()
                BlackListerApp.instance?.database?.clearAllTables()
                showMessage(getString(R.string.operation_succeeded), false)
                (activity as MainActivity).apply {
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            successChangePasswordLiveData.safeSingleObserve(viewLifecycleOwner) {
                showMessage(String.format(getString(R.string.change_password_succeed)), false)
                binding?.accountDetailsNewPasswordCheck?.isChecked = false
            }
        }
    }

}